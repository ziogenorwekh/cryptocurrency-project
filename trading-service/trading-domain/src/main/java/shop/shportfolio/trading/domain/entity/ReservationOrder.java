package shop.shportfolio.trading.domain.entity;

import lombok.Getter;
import shop.shportfolio.common.domain.valueobject.MarketId;
import shop.shportfolio.common.domain.valueobject.OrderPrice;
import shop.shportfolio.common.domain.valueobject.Quantity;
import shop.shportfolio.common.domain.valueobject.UserId;
import shop.shportfolio.trading.domain.exception.TradingDomainException;
import shop.shportfolio.trading.domain.valueobject.*;

import java.time.LocalDateTime;

// 예약 매수
@Getter
public class ReservationOrder extends Order {

    private TriggerCondition triggerCondition;
    private ScheduledTime scheduledTime;
    private ExpireAt expireAt;
    private IsRepeatable isRepeatable;

    public ReservationOrder(UserId userId, MarketId marketId, OrderSide orderSide,
                            Quantity quantity, OrderPrice orderPrice, OrderType orderType,
                            TriggerCondition triggerCondition, ScheduledTime scheduledTime,
                            ExpireAt expireAt, IsRepeatable isRepeatable) {
        super(userId, marketId, orderSide, quantity, orderPrice, orderType);
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

    public boolean canExecute(OrderPrice currentMarketPrice, LocalDateTime currentTime) {
        // 트리거 조건 + 예약시간 체크
        if (!triggerCondition.isSatisfiedBy(currentMarketPrice)) {
            return false;
        }
        if (!scheduledTime.isDue(currentTime)) {
            return false;
        }
        return !isExpired(currentTime);
    }

    public boolean isExpired(LocalDateTime currentTime) {
        if (expireAt == null) {
            return false;
        }
        return expireAt.isBefore(currentTime);
    }

    public boolean shouldRepeat() {
        return isRepeatable.isTrue();
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
        if (getOrderPrice() == null || getOrderPrice().isZeroOrLess()) {
            throw new TradingDomainException("Reservation order must have a positive price.");
        }
        validateCommonPlaceable();
    }

    @Override
    public Boolean isPriceMatch(OrderPrice targetPrice) {
        if (targetPrice == null) return false;
        if (this.isBuyOrder()) {
            return getOrderPrice().isGreaterThanOrEqualTo(targetPrice);
        } else {
            return getOrderPrice().isLessThanOrEqualTo(targetPrice);
        }
    }
}
