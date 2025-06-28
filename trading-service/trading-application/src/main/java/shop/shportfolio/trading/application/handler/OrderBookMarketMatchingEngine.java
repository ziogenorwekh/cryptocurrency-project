package shop.shportfolio.trading.application.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.valueobject.CreatedAt;
import shop.shportfolio.common.domain.valueobject.Quantity;
import shop.shportfolio.common.domain.valueobject.TransactionType;
import shop.shportfolio.trading.application.ports.output.repository.TradingRepositoryAdapter;
import shop.shportfolio.trading.domain.TradingDomainService;
import shop.shportfolio.trading.domain.entity.MarketOrder;
import shop.shportfolio.trading.domain.entity.Order;
import shop.shportfolio.trading.domain.entity.OrderBook;
import shop.shportfolio.trading.domain.entity.PriceLevel;
import shop.shportfolio.trading.domain.event.TradingRecordedEvent;
import shop.shportfolio.trading.domain.valueobject.TickPrice;
import shop.shportfolio.trading.domain.valueobject.TradeId;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.BiFunction;

@Slf4j
@Component
public class OrderBookMarketMatchingEngine {

    private final TradingDomainService tradingDomainService;
    private final TradingRepositoryAdapter tradingRepositoryAdapter;

    public OrderBookMarketMatchingEngine(TradingDomainService tradingDomainService,
                                         TradingRepositoryAdapter tradingRepositoryAdapter) {
        this.tradingDomainService = tradingDomainService;
        this.tradingRepositoryAdapter = tradingRepositoryAdapter;
    }

    public List<TradingRecordedEvent> execBidMarketOrder(OrderBook orderBook, MarketOrder marketOrder) {
        return execMarketOrder(
                marketOrder,
                orderBook.getBuyPriceLevels(),
                (tradeId, qty) -> tradingDomainService.createTrade(
                        tradeId, marketOrder.getUserId(), marketOrder.getId(),
                        marketOrder.getOrderPrice(), qty, new CreatedAt(LocalDateTime.now()),
                        TransactionType.TRADE_SELL));
    }

    public List<TradingRecordedEvent> execAsksMarketOrder(OrderBook orderBook, MarketOrder marketOrder) {
        return execMarketOrder(
                marketOrder,
                orderBook.getSellPriceLevels(),
                (tradeId, qty) -> tradingDomainService.createTrade(
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
                Quantity execQty = tradingDomainService.applyOrder(marketOrder, restingOrder.getRemainingQuantity());
                tradingDomainService.applyOrder(restingOrder, execQty);

                TradingRecordedEvent tradeEvent = tradeEventCreator.apply(new TradeId(UUID.randomUUID()), execQty);
                tradingRepositoryAdapter.saveTrade(tradeEvent.getDomainType());
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
        if (marketOrder.isOpen()) {
            tradingDomainService.cancelOrder(marketOrder);
        }
        return trades;
    }
}



