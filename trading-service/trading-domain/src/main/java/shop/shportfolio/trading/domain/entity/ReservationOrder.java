package shop.shportfolio.trading.domain.entity;

import lombok.Builder;
import lombok.Getter;
import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.trading.domain.exception.TradingDomainException;
import shop.shportfolio.trading.domain.valueobject.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class ReservationOrder extends Order {

    private TriggerCondition triggerCondition;
    private ScheduledTime scheduledTime;
    private ExpireAt expireAt;
    private IsRepeatable isRepeatable;

    @Builder
    public ReservationOrder(OrderId orderId, UserId userId, MarketId marketId, OrderSide orderSide,
                            Quantity quantity, Quantity remainingQuantity,
                            OrderPrice orderPrice, OrderType orderType, CreatedAt createdAt,
                            OrderStatus orderStatus,
                            TriggerCondition triggerCondition, ScheduledTime scheduledTime,
                            ExpireAt expireAt, IsRepeatable isRepeatable) {
        super(orderId, userId, marketId, orderSide, quantity, remainingQuantity,
                orderPrice, orderType, createdAt,orderStatus);
        this.triggerCondition = triggerCondition;
        this.scheduledTime = scheduledTime;
        this.expireAt = expireAt;
        this.isRepeatable = isRepeatable;
    }

    private ReservationOrder(OrderId orderId, UserId userId, MarketId marketId, OrderSide orderSide,
                            Quantity quantity, OrderType orderType, CreatedAt createdAt,
                            TriggerCondition triggerCondition, ScheduledTime scheduledTime,
                            ExpireAt expireAt, IsRepeatable isRepeatable) {
        super(orderId,userId, marketId, orderSide, quantity, null, orderType,createdAt);
        this.triggerCondition = triggerCondition;
        this.scheduledTime = scheduledTime;
        this.expireAt = expireAt;
        this.isRepeatable = isRepeatable;
    }

    public static ReservationOrder createReservationOrder(
            UserId userId, MarketId marketId, OrderSide orderSide,
            Quantity quantity, OrderType orderType,
            TriggerCondition triggerCondition, ScheduledTime scheduledTime,
            ExpireAt expireAt, IsRepeatable isRepeatable) {

        ReservationOrder reservationOrder = new ReservationOrder(
                new OrderId(UUID.randomUUID().toString()),
                userId, marketId, orderSide, quantity,
                orderType, CreatedAt.now(),
                triggerCondition, scheduledTime, expireAt, isRepeatable
        );
        reservationOrder.validatePlaceable();
        return reservationOrder;
    }

    public boolean canExecute(OrderPrice currentMarketPrice, LocalDateTime currentTime) {
        if (!triggerCondition.isSatisfiedBy(currentMarketPrice)) return false;
        if (!scheduledTime.isDue(currentTime)) return false;
        return !isExpired(currentTime);
    }

    public boolean isExpired(LocalDateTime currentTime) {
        return expireAt != null && expireAt.isBefore(currentTime);
    }

    public boolean shouldRepeat() {
        return isRepeatable.isTrue();
    }

    @Override
    public void validatePlaceable() {
        validateTriggerCondition();
        validateScheduledTime();
        validateExpireTime();
        validateRepeatableCondition();
        validateOrderType();
        validateTriggerPrice();
        validateCommonPlaceable();
    }

    @Override
    public Boolean isPriceMatch(OrderPrice targetPrice) {
        if (targetPrice == null) return false;
        if (this.isBuyOrder()) {
            return this.triggerCondition.getTargetPrice().isGreaterThanOrEqualTo(targetPrice);
        } else {
            return this.triggerCondition.getTargetPrice().isLessThanOrEqualTo(targetPrice);
        }
    }

    // ================================
    // Private Validation Methods
    // ================================

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

    private void validateTriggerPrice() {
        if (this.triggerCondition.getTargetPrice() == null ||
                this.triggerCondition.getTargetPrice().isZeroOrLess()) {
            throw new TradingDomainException("Reservation order must have a positive price.");
        }
    }

    @Override
    public OrderPrice getOrderPrice() {
        throw new UnsupportedOperationException("ReservationOrder does not have an OrderPrice." +
                " Use TriggerCondition.getTargetPrice() instead.");
    }
}
