package shop.shportfolio.trading.domain.entity.userbalance;

import lombok.Builder;
import lombok.Getter;
import shop.shportfolio.common.domain.entity.BaseEntity;
import shop.shportfolio.common.domain.valueobject.CreatedAt;
import shop.shportfolio.common.domain.valueobject.OrderId;
import shop.shportfolio.common.domain.valueobject.UserId;
import shop.shportfolio.trading.domain.valueobject.LockStatus;
import shop.shportfolio.common.domain.valueobject.Money;

@Getter
public class LockBalance extends BaseEntity<OrderId> {
    private final UserId userId;             // 락 밸런스 소유자
    private Money lockedAmount;               // 실제로 락 걸린 금액
    private LockStatus lockStatus;
    private CreatedAt lockedAt;           // 락 걸린 시점 (필요에 따라)

    @Builder
    public LockBalance(OrderId orderId, UserId userId,
                        Money lockedAmount, LockStatus lockStatus,
                        CreatedAt lockedAt) {
        this.lockStatus = lockStatus;
        setId(orderId);
        this.userId = userId;
        this.lockedAmount = lockedAmount;
        this.lockedAt = lockedAt;
    }

    public static LockBalance createLockBalance(OrderId orderId, UserId userId, Money lockedAmount, LockStatus lockStatus,
                                                CreatedAt lockedAt) {
        return new LockBalance(orderId, userId, lockedAmount, lockStatus, lockedAt);
    }

    public void subtractLockedAmount(Money amount) {
        if (lockedAmount.getValue().compareTo(amount.getValue()) < 0) {
            throw new IllegalArgumentException("Cannot subtract more than locked amount");
        }
        this.lockedAmount = lockedAmount.subtract(amount);
        if (lockedAmount.isZero()) {
            this.lockStatus = LockStatus.RELEASED;
        } else {
            this.lockStatus = LockStatus.PARTIALLY;
        }
    }

    public Boolean isLocked() {
        return lockStatus == LockStatus.LOCKED;
    }
    public Boolean isReleased() {
        return lockStatus == LockStatus.RELEASED;
    }
    public Boolean isPartiallyLocked() {
        return lockStatus == LockStatus.PARTIALLY;
    }

    @Override
    public String toString() {
        return "LockBalance{" +
                "userId=" + userId.getValue() +
                ",\n lockedAmount=" + lockedAmount.getValue() +
                ",\n lockStatus=" + lockStatus.toString() +
                ",\n lockedAt=" + lockedAt.getValue() +
                '}';
    }
}
