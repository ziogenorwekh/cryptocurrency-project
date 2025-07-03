package shop.shportfolio.trading.application.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.trading.application.dto.OrderBookDto;
import shop.shportfolio.trading.application.exception.MarketItemNotFoundException;
import shop.shportfolio.trading.application.exception.MarketPausedException;
import shop.shportfolio.trading.application.exception.OrderBookNotFoundException;
import shop.shportfolio.trading.application.mapper.TradingDtoMapper;
import shop.shportfolio.trading.application.ports.output.redis.MarketDataRedisAdapter;
import shop.shportfolio.trading.application.ports.output.repository.TradingRepositoryAdapter;
import shop.shportfolio.trading.domain.TradingDomainService;
import shop.shportfolio.trading.domain.entity.*;

import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Component
public class OrderBookManager {

    private final TradingDomainService tradingDomainService;
    private final TradingRepositoryAdapter tradingRepositoryAdapter;
    private final TradingDtoMapper tradingDtoMapper;
    private final MarketDataRedisAdapter marketDataRedisAdapter;

    @Autowired
    public OrderBookManager(TradingDomainService tradingDomainService,
                            TradingRepositoryAdapter tradingRepositoryAdapter,
                            TradingDtoMapper tradingDtoMapper, MarketDataRedisAdapter marketDataRedisAdapter) {
        this.tradingDomainService = tradingDomainService;
        this.tradingRepositoryAdapter = tradingRepositoryAdapter;
        this.tradingDtoMapper = tradingDtoMapper;
        this.marketDataRedisAdapter = marketDataRedisAdapter;
    }


    public MarketItem findMarketItemById(String marketId) {
        MarketItem marketItem = tradingRepositoryAdapter.findMarketItemByMarketId(marketId).orElseThrow(() ->
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
        OrderBookDto externalOrderBook = marketDataRedisAdapter
                .findOrderBookByMarket(marketId).orElseThrow(() ->
                        new OrderBookNotFoundException(String.format("Market id %s not found",
                                marketId)));
        OrderBook adjustedOrderBook = tradingDtoMapper.orderBookDtoToOrderBook(externalOrderBook, tickPrice);
        List<Trade> trades = tradingRepositoryAdapter.findTradesByMarketId(marketId);
        trades.forEach(trade -> {
            tradingDomainService.applyExecutedTrade(adjustedOrderBook, trade);
        });
        return adjustedOrderBook;
    }



}
