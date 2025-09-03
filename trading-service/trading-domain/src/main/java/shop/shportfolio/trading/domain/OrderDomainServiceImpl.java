package shop.shportfolio.trading.domain;

import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.trading.domain.entity.*;
import shop.shportfolio.trading.domain.entity.orderbook.OrderBook;
import shop.shportfolio.trading.domain.event.LimitOrderCreatedEvent;
import shop.shportfolio.trading.domain.event.MarketOrderCreatedEvent;
import shop.shportfolio.trading.domain.event.ReservationOrderCreatedEvent;
import shop.shportfolio.trading.domain.valueobject.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class OrderDomainServiceImpl implements OrderDomainService {
    @Override
    public void cancelOrder(Order order) {
        order.cancel();
    }

    @Override
    public LimitOrderCreatedEvent createLimitOrder(UserId userId, MarketId marketId, OrderSide orderSide,
                                       Quantity quantity, OrderPrice price, OrderType orderType) {
        LimitOrder limitOrder = LimitOrder.createLimitOrder(userId, marketId, orderSide, quantity, price, orderType);
        return new LimitOrderCreatedEvent(limitOrder,MessageType.CREATE,ZonedDateTime.now(ZoneOffset.UTC));
    }

    @Override
    public MarketOrderCreatedEvent createMarketOrder(UserId userId, MarketId marketId, OrderSide orderSide,
                                         OrderPrice orderPrice, OrderType orderType) {
        MarketOrder marketOrder = MarketOrder.createMarketOrder(userId, marketId, orderSide, orderPrice, orderType);
        return new MarketOrderCreatedEvent(marketOrder,MessageType.CREATE,ZonedDateTime.now(ZoneOffset.UTC));
    }

    @Override
    public ReservationOrderCreatedEvent createReservationOrder(UserId userId, MarketId marketId, OrderSide orderSide,
                                                   Quantity quantity, OrderType orderType,
                                                   TriggerCondition triggerCondition, ScheduledTime scheduledTime,
                                                   ExpireAt expireAt, IsRepeatable isRepeatable) {
        ReservationOrder reservationOrder = ReservationOrder.createReservationOrder(userId, marketId, orderSide, quantity,
                orderType, triggerCondition, scheduledTime, expireAt, isRepeatable);
        return new ReservationOrderCreatedEvent(reservationOrder,MessageType.CREATE,ZonedDateTime.now(ZoneOffset.UTC));
    }


    @Override
    public Boolean canMatchPrice(Order order, TickPrice counterPrice) {
        return order.canMatchPrice(order, counterPrice);
    }


    @Override
    public Quantity applyOrder(Order order, Quantity executedQty) {
        return order.applyTrade(executedQty);
    }

    @Override
    public Quantity applyMarketOrder(MarketOrder marketOrder, Quantity executedQty, OrderPrice executedPrice) {
        return marketOrder.applyMarketOrderTrade(executedPrice, executedQty);
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
    public OrderBook addOrderbyOrderBook(OrderBook orderBook, LimitOrder order) {
        orderBook.addOrder(order);
        return orderBook;
    }


    @Override
    public void orderAppliedPartialFilled(Order order) {
        order.partialFill();
    }

    @Override
    public Boolean isReservationOrderExecutable(ReservationOrder reservationOrder, OrderPrice currentPrice) {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

        return reservationOrder.canExecute(currentPrice, now);
    }

}
