package shop.shportfolio.trading.domain;

import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.trading.domain.entity.*;
import shop.shportfolio.trading.domain.event.TradingRecordedEvent;
import shop.shportfolio.trading.domain.valueobject.*;

import java.time.ZonedDateTime;

public class TradingDomainServiceImpl implements TradingDomainService {
    @Override
    public void cancelOrder(Order order) {
        order.cancel();
    }

    @Override
    public LimitOrder createLimitOrder(UserId userId, MarketId marketId, OrderSide orderSide,
                                       Quantity quantity, OrderPrice price, OrderType orderType) {
        return LimitOrder.createLimitOrder(userId, marketId, orderSide, quantity, price, orderType);
    }

    @Override
    public MarketOrder createMarketOrder(UserId userId, MarketId marketId, OrderSide orderSide,
                                         Quantity quantity, OrderType orderType) {
        return MarketOrder.createMarketOrder(userId, marketId, orderSide, quantity, orderType);
    }

    @Override
    public MarketItem createMarketItem(String marketId, MarketKoreanName marketKoreanName,
                                       MarketEnglishName marketEnglishName, MarketWarning marketWarning) {
        return MarketItem.createMarketItem(marketId, marketKoreanName, marketEnglishName, marketWarning);
    }

    @Override
    public ReservationOrder createReservationOrder(UserId userId, MarketId marketId, OrderSide orderSide,
                                                   Quantity quantity, OrderPrice orderPrice, OrderType orderType,
                                                   TriggerCondition triggerCondition, ScheduledTime scheduledTime,
                                                   ExpireAt expireAt, IsRepeatable isRepeatable) {
        return ReservationOrder.createReservationOrder(userId, marketId, orderSide, quantity, orderPrice,
                orderType, triggerCondition, scheduledTime, expireAt, isRepeatable);
    }

    @Override
    public TradingRecordedEvent createMarketTrade(TradeId tradeId, UserId userId, OrderId buyOrderId, OrderPrice orderPrice,
                                                  Quantity quantity, CreatedAt createdAt, TransactionType transactionType) {
        Trade trade = Trade.createMarketTrade(tradeId, userId, buyOrderId, orderPrice, quantity, createdAt, transactionType);
        return new TradingRecordedEvent(trade, MessageType.CREATE, ZonedDateTime.now());
    }

    @Override
    public Boolean applyTrade(Order order, Quantity executedQty) {
        return order.applyTrade(executedQty);
    }

    @Override
    public Boolean canMatchWith(Order order, Order targetOrder) {
        return order.canMatchWith(targetOrder);
    }

    @Override
    public Boolean isPriceMatch(Order checkOrder, OrderPrice orderPrice) {

        return checkOrder.isPriceMatch(orderPrice);
    }

    @Override
    public Boolean isSellOrder(Order order) {
        return order.isSellOrder();
    }

    @Override
    public Boolean isBuyOrder(Order order) {
        return order.isBuyOrder();
    }

    @Override
    public OrderBook addOrderbyOrderBook(OrderBook orderBook, Order order) {
        orderBook.addOrder(order);
        return orderBook;
    }

    @Override
    public void applyExecutedTrade(OrderBook orderBook, Trade trade) {
        orderBook.applyExecutedTrade(trade);
    }

}
