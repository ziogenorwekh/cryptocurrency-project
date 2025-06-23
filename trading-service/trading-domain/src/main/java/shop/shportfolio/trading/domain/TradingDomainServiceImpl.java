package shop.shportfolio.trading.domain;

import shop.shportfolio.common.domain.valueobject.MarketId;
import shop.shportfolio.common.domain.valueobject.UserId;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.MarketOrder;
import shop.shportfolio.trading.domain.entity.Order;
import shop.shportfolio.trading.domain.entity.ReservationOrder;
import shop.shportfolio.trading.domain.valueobject.*;

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
                                   Quantity quantity, OrderPrice price, OrderType orderType) {
        return MarketOrder.createMarketOrder(userId, marketId, orderSide, quantity, price, orderType);
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
    public void applyTrade(Order order, Quantity executedQty) {
        order.applyTrade(executedQty);
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
}
