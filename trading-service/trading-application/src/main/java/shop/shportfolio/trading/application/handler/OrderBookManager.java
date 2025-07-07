package shop.shportfolio.trading.application.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.trading.application.dto.orderbook.OrderBookBithumbDto;
import shop.shportfolio.trading.application.exception.MarketItemNotFoundException;
import shop.shportfolio.trading.application.exception.MarketPausedException;
import shop.shportfolio.trading.application.exception.OrderBookNotFoundException;
import shop.shportfolio.trading.application.mapper.TradingDtoMapper;
import shop.shportfolio.trading.application.ports.output.redis.TradingMarketDataRedisPort;
import shop.shportfolio.trading.application.ports.output.redis.TradingOrderRedisPort;
import shop.shportfolio.trading.application.ports.output.repository.TradingMarketDataRepositoryPort;
import shop.shportfolio.trading.application.ports.output.repository.TradingOrderRepositoryPort;
import shop.shportfolio.trading.application.ports.output.repository.TradingTradeRecordRepositoryPort;
import shop.shportfolio.trading.application.support.RedisKeyPrefix;
import shop.shportfolio.trading.domain.TradingDomainService;
import shop.shportfolio.trading.domain.entity.*;

import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Component
public class OrderBookManager {

    private final TradingDomainService tradingDomainService;
    private final TradingOrderRepositoryPort tradingOrderRepositoryPort;
    private final TradingDtoMapper tradingDtoMapper;
    private final TradingOrderRedisPort tradingOrderRedisPort;
    private final TradingMarketDataRedisPort tradingMarketDataRedisPort;
    private final TradingTradeRecordRepositoryPort tradingTradeRecordRepositoryPort;
    private final TradingMarketDataRepositoryPort tradingMarketDataRepositoryPort;


    @Autowired
    public OrderBookManager(TradingDomainService tradingDomainService,
                            TradingOrderRepositoryPort tradingOrderRepositoryPort,
                            TradingDtoMapper tradingDtoMapper, TradingOrderRedisPort tradingOrderRedisPort,
                            TradingMarketDataRedisPort tradingMarketDataRedisPort,
                            TradingTradeRecordRepositoryPort tradingTradeRecordRepositoryPort,
                            TradingMarketDataRepositoryPort tradingMarketDataRepositoryPort) {
        this.tradingDomainService = tradingDomainService;
        this.tradingOrderRepositoryPort = tradingOrderRepositoryPort;
        this.tradingDtoMapper = tradingDtoMapper;
        this.tradingOrderRedisPort = tradingOrderRedisPort;
        this.tradingMarketDataRedisPort = tradingMarketDataRedisPort;
        this.tradingTradeRecordRepositoryPort = tradingTradeRecordRepositoryPort;
        this.tradingMarketDataRepositoryPort = tradingMarketDataRepositoryPort;
    }


    public MarketItem findMarketItemById(String marketId) {
        MarketItem marketItem = tradingMarketDataRepositoryPort.findMarketItemByMarketId(marketId).orElseThrow(() ->
                {
                    log.info("marketId:{} not found", marketId);
                    return new MarketItemNotFoundException(String.format("MarketItem with id %s not found",
                            marketId));
                }
        );
        if (!marketItem.isActive()) {
            throw new MarketPausedException(String.format("MarketItem with id %s is not active", marketId));
        }
        return marketItem;
    }

    public OrderBook loadAdjustedOrderBook(String marketId, BigDecimal tickPrice) {
        OrderBookBithumbDto externalOrderBook = tradingMarketDataRedisPort
                .findOrderBookByMarket(RedisKeyPrefix.market(marketId)).orElseThrow(() ->
                        new OrderBookNotFoundException(String.format("Market id %s not found",
                                marketId)));
        OrderBook adjustedOrderBook = tradingDtoMapper.orderBookDtoToOrderBook(externalOrderBook, tickPrice);
        List<LimitOrder> orders = tradingOrderRedisPort.findLimitOrdersByMarketId(marketId);
        OrderBook adjustedOrderBookAddLimitOrder = adjustedOrderBookByLimitOrders(adjustedOrderBook, orders);

        List<Trade> trades = tradingTradeRecordRepositoryPort.findTradesByMarketId(marketId);
        trades.forEach(trade -> {
            tradingDomainService.applyExecutedTrade(adjustedOrderBookAddLimitOrder, trade);
        });
        return adjustedOrderBookAddLimitOrder;
    }


    private OrderBook adjustedOrderBookByLimitOrders(OrderBook orderBook, List<LimitOrder> orders) {

        orders.forEach(limitOrder -> {
            tradingDomainService.addOrderbyOrderBook(orderBook, limitOrder);
        });
        return orderBook;
    }

}
