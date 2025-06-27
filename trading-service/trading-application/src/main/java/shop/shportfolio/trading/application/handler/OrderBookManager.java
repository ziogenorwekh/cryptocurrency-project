package shop.shportfolio.trading.application.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.valueobject.CreatedAt;
import shop.shportfolio.common.domain.valueobject.MarketId;
import shop.shportfolio.common.domain.valueobject.Quantity;
import shop.shportfolio.common.domain.valueobject.TransactionType;
import shop.shportfolio.trading.application.dto.OrderBookDto;
import shop.shportfolio.trading.application.exception.MarketItemNotFoundException;
import shop.shportfolio.trading.application.exception.OrderBookNotFoundException;
import shop.shportfolio.trading.application.mapper.TradingDtoMapper;
import shop.shportfolio.trading.application.ports.output.redis.MarketDataRedisAdapter;
import shop.shportfolio.trading.application.ports.output.repository.TradingRepositoryAdapter;
import shop.shportfolio.trading.domain.TradingDomainService;
import shop.shportfolio.trading.domain.entity.*;
import shop.shportfolio.trading.domain.event.TradingRecordedEvent;
import shop.shportfolio.trading.domain.valueobject.TickPrice;
import shop.shportfolio.trading.domain.valueobject.TradeId;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.BiFunction;

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
        return tradingRepositoryAdapter.findMarketItemByMarketId(marketId).orElseThrow(() ->
                {
                    log.info("marketId:{} not found", marketId);
                    return new MarketItemNotFoundException(String.format("MarketItem with id %s not found",
                            marketId));
                }
        );
    }

    public OrderBook reflectOrderBookByTrades(String marketId, BigDecimal tickPrice) {
        OrderBookDto orderBookDto = marketDataRedisAdapter
                .findOrderBookByMarket(marketId).orElseThrow(() ->
                        new OrderBookNotFoundException(String.format("Market id %s not found",
                                marketId)));
        OrderBook orderBook = tradingDtoMapper.orderBookDtoToOrderBook(orderBookDto, tickPrice);
        List<Trade> trades = tradingRepositoryAdapter.findTradesByMarketId(marketId);
        trades.forEach(trade -> {
            tradingDomainService.applyExecutedTrade(orderBook, trade);
        });
        return orderBook;
    }


    public List<TradingRecordedEvent> execBidMarketOrder(OrderBook orderBook, MarketOrder marketOrder) {
        return execMarketOrder(
                marketOrder,
                orderBook.getBuyPriceLevels(),
                (tradeId, qty) -> tradingDomainService.createMarketTrade(
                        tradeId, marketOrder.getUserId(), marketOrder.getId(),
                        marketOrder.getOrderPrice(), qty, new CreatedAt(LocalDateTime.now()),
                        TransactionType.TRADE_SELL));
    }

    public List<TradingRecordedEvent> execAsksMarketOrder(OrderBook orderBook, MarketOrder marketOrder) {
        return execMarketOrder(
                marketOrder,
                orderBook.getSellPriceLevels(),
                (tradeId, qty) -> tradingDomainService.createMarketTrade(
                        tradeId, marketOrder.getUserId(), marketOrder.getId(),
                        marketOrder.getOrderPrice(), qty, new CreatedAt(LocalDateTime.now()),
                        TransactionType.TRADE_BUY));
    }


    /**
     * 수정 됌
     * @param marketOrder
     * @return
     */
    private List<TradingRecordedEvent> execMarketOrder(MarketOrder marketOrder,
                                                       NavigableMap<TickPrice, PriceLevel> priceLevels,
                                                       BiFunction<TradeId, Quantity, TradingRecordedEvent> tradeEventCreator) {
        List<TradingRecordedEvent> trades = new ArrayList<>();

        log.info("Start executing MarketOrder. OrderId={}, RemainingQty={}",
                marketOrder.getId().getValue(), marketOrder.getRemainingQuantity().getValue());

        while (marketOrder.isOpen() && !priceLevels.isEmpty()) {
            Map.Entry<TickPrice, PriceLevel> entry = priceLevels.firstEntry();
            PriceLevel priceLevel = entry.getValue();

            while (marketOrder.isOpen() && !priceLevel.isEmpty()) {
                Order restingOrder = priceLevel.peekOrder();
                Quantity execQty = marketOrder.applyTrade(restingOrder.getRemainingQuantity());
                restingOrder.applyTrade(execQty);

                TradingRecordedEvent tradeEvent = tradeEventCreator.apply(new TradeId(UUID.randomUUID()), execQty);
                trades.add(tradeEvent);

                log.info("Executed trade: {} qty at price {}", execQty.getValue(), entry.getKey().getValue());

                if (restingOrder.isFilled()) {
                    priceLevel.popOrder();
                }

                if (marketOrder.isFilled()) {
                    break;
                }
            }

            if (priceLevel.isEmpty()) {
                priceLevels.remove(entry.getKey());
            }
        }

        return trades;
    }
}
