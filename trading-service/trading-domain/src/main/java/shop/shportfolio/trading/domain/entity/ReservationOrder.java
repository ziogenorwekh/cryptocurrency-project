package shop.shportfolio.trading.domain.entity;

import lombok.Builder;
import shop.shportfolio.common.domain.valueobject.MarketId;
import shop.shportfolio.common.domain.valueobject.UserId;
import shop.shportfolio.trading.domain.exception.TradingDomainException;
import shop.shportfolio.trading.domain.valueobject.*;

// 예약 매수
public class ReservationOrder extends Order {

    private OrderPrice orderPrice;

    private TriggerCondition triggerCondition;
    private ScheduledTime scheduledTime;
    private ExpireAt expireAt;
    private IsRepeatable isRepeatable;

    public ReservationOrder(UserId userId, MarketId marketId, OrderSide orderSide,
                            Quantity quantity, OrderPrice orderPrice, OrderType orderType,
                            TriggerCondition triggerCondition, ScheduledTime scheduledTime,
                            ExpireAt expireAt, IsRepeatable isRepeatable) {
        super(userId, marketId, orderSide, quantity, orderType);
        this.orderPrice = orderPrice;
        this.triggerCondition = triggerCondition;
        this.scheduledTime = scheduledTime;
        this.expireAt = expireAt;
        this.isRepeatable = isRepeatable;
    }

    public static ReservationOrder createReservationOrder(
            UserId userId, MarketId marketId, OrderSide orderSide,
            Quantity quantity, OrderPrice orderPrice, OrderType orderType,
            TriggerCondition triggerCondition, ScheduledTime scheduledTime,
            ExpireAt expireAt, IsRepeatable isRepeatable) {

        ReservationOrder reservationOrder = new ReservationOrder(userId, marketId, orderSide, quantity,
                orderPrice, orderType, triggerCondition, scheduledTime, expireAt, isRepeatable);
        reservationOrder.validatePlaceable();
        return reservationOrder;
    }

    private void validate() {
        validateTriggerCondition();
        validateScheduledTime();
        validateExpireTime();
        validateRepeatableCondition();
        validateOrderType();
    }

    private void validateTriggerCondition() {
        if (triggerCondition == null) {
            throw new TradingDomainException("TriggerCondition must not be null.");
        }
    }

    private void validateScheduledTime() {
        if (scheduledTime == null) {
            throw new TradingDomainException("ScheduledTime must not be null.");
        }
    }

    private void validateExpireTime() {
        if (expireAt != null && expireAt.isBefore(scheduledTime.getValue())) {
            throw new TradingDomainException("ExpireAt cannot be before ScheduledTime.");
        }
    }

    private void validateRepeatableCondition() {
        if (isRepeatable.isTrue() && scheduledTime == null) {
            throw new TradingDomainException("Repeatable order must have a ScheduledTime.");
        }
    }

    private void validateOrderType() {
        if (!this.getOrderType().isReservationType()) {
            throw new TradingDomainException("OrderType is not valid for ReservationOrder.");
        }
    }

    @Override
    public void validatePlaceable() {
        if (triggerCondition == null) {
            throw new TradingDomainException("TriggerCondition must not be null.");
        }
        if (scheduledTime == null) {
            throw new TradingDomainException("ScheduledTime must not be null.");
        }
        if (expireAt != null && expireAt.isBefore(scheduledTime.getValue())) {
            throw new TradingDomainException("ExpireAt cannot be before ScheduledTime.");
        }
        if (isRepeatable.isTrue() && scheduledTime == null) {
            throw new TradingDomainException("Repeatable order must have a ScheduledTime.");
        }
        if (orderPrice == null || orderPrice.isZeroOrLess()) {
            throw new TradingDomainException("Reservation order must have a positive price.");
        }
        validateCommonPlaceable();
    }

    @Override
    public Boolean isPriceMatch(OrderPrice targetPrice) {
        if (targetPrice == null) return false;
        if (this.isBuyOrder()) {
            return this.orderPrice.isGreaterThanOrEqualTo(targetPrice);
        } else {
            return this.orderPrice.isLessThanOrEqualTo(targetPrice);
        }
    }
}
