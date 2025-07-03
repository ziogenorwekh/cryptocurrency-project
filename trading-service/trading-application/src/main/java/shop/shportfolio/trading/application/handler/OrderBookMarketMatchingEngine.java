package shop.shportfolio.trading.application.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.valueobject.Quantity;
import shop.shportfolio.common.domain.valueobject.TransactionType;
import shop.shportfolio.trading.application.ports.output.repository.TradingRepositoryPort;
import shop.shportfolio.trading.domain.TradingDomainService;
import shop.shportfolio.trading.domain.entity.MarketOrder;
import shop.shportfolio.trading.domain.entity.Order;
import shop.shportfolio.trading.domain.entity.OrderBook;
import shop.shportfolio.trading.domain.entity.PriceLevel;
import shop.shportfolio.trading.domain.event.TradingRecordedEvent;
import shop.shportfolio.trading.domain.valueobject.TickPrice;
import shop.shportfolio.trading.domain.valueobject.TradeId;

import java.util.*;
import java.util.function.BiFunction;

@Slf4j
@Component
public class OrderBookMarketMatchingEngine {

    private final TradingDomainService tradingDomainService;
    private final TradingRepositoryPort tradingRepositoryPort;

    public OrderBookMarketMatchingEngine(TradingDomainService tradingDomainService,
                                         TradingRepositoryPort tradingRepositoryPort) {
        this.tradingDomainService = tradingDomainService;
        this.tradingRepositoryPort = tradingRepositoryPort;
    }

    public List<TradingRecordedEvent> execBidMarketOrder(OrderBook orderBook, MarketOrder marketOrder) {
        return execMarketOrder(
                marketOrder,
                orderBook.getBuyPriceLevels(),
                (tradeId, qty) -> tradingDomainService.createTrade(
                        tradeId, marketOrder.getUserId(), marketOrder.getId(),
                        marketOrder.getOrderPrice(), qty,
                        TransactionType.TRADE_SELL));
    }

    public List<TradingRecordedEvent> execAskMarketOrder(OrderBook orderBook, MarketOrder marketOrder) {
        return execMarketOrder(
                marketOrder,
                orderBook.getSellPriceLevels(),
                (tradeId, qty) -> tradingDomainService.createTrade(
                        tradeId, marketOrder.getUserId(), marketOrder.getId(),
                        marketOrder.getOrderPrice(), qty,
                        TransactionType.TRADE_BUY));
    }


    /**
     * 수정 됌
     *
     * @param marketOrder
     * @return
     */
    private List<TradingRecordedEvent> execMarketOrder(MarketOrder marketOrder,
                                                       NavigableMap<TickPrice, PriceLevel> priceLevels,
                                                       BiFunction<TradeId, Quantity, TradingRecordedEvent> tradeEventCreator) {
        List<TradingRecordedEvent> trades = new ArrayList<>();

        log.info("Start executing MarketOrder. OrderId={}, RemainingQty={}",
                marketOrder.getId().getValue(), marketOrder.getRemainingQuantity().getValue());

        while (marketOrder.isUnfilled() && !priceLevels.isEmpty()) {
            Map.Entry<TickPrice, PriceLevel> entry = priceLevels.firstEntry();
            PriceLevel priceLevel = entry.getValue();


            while (marketOrder.isUnfilled() && !priceLevel.isEmpty()) {
                Order restingOrder = priceLevel.peekOrder();
                Quantity execQty = tradingDomainService.applyOrder(marketOrder, restingOrder.getRemainingQuantity());
                tradingDomainService.applyOrder(restingOrder, execQty);

                TradingRecordedEvent tradeEvent = tradeEventCreator.apply(new TradeId(UUID.randomUUID()), execQty);
                tradingRepositoryPort.saveTrade(tradeEvent.getDomainType());
                trades.add(tradeEvent);

                log.info("Executed trade: {} qty at price {}", execQty.getValue(), entry.getKey().getValue());

                if (restingOrder.isFilled()) {
                    priceLevel.popOrder();
                }

                if (marketOrder.isFilled()) {
                    tradingRepositoryPort.saveMarketOrder(marketOrder);
                    break;
                }
            }

            if (priceLevel.isEmpty()) {
                priceLevels.remove(entry.getKey());
            }
        }
        if (marketOrder.isUnfilled()) {
            log.info("market is unfilled");
            tradingDomainService.cancelOrder(marketOrder);
            log.info("market is unfilled And Status Update : {}",
                    marketOrder.getOrderStatus().name());
            tradingRepositoryPort.saveMarketOrder(marketOrder);
        }
        return trades;
    }
}



