package shop.shportfolio.trading.domain.entity.userbalance;

import lombok.Getter;
import shop.shportfolio.common.domain.entity.BaseEntity;
import shop.shportfolio.common.domain.valueobject.CreatedAt;
import shop.shportfolio.common.domain.valueobject.OrderId;
import shop.shportfolio.common.domain.valueobject.UserId;
import shop.shportfolio.trading.domain.valueobject.Money;

@Getter
public class LockBalance extends BaseEntity<OrderId> {
    private final UserId userId;             // 락 밸런스 소유자
    private Money lockedAmount;               // 실제로 락 걸린 금액
    private CreatedAt lockedAt;           // 락 걸린 시점 (필요에 따라)

    private LockBalance(OrderId orderId, UserId userId,
                       Money lockedAmount,
                       CreatedAt lockedAt) {
        setId(orderId);
        this.userId = userId;
        this.lockedAmount = lockedAmount;
        this.lockedAt = lockedAt;
    }

    public static LockBalance createLockBalance(OrderId orderId, UserId userId, Money lockedAmount,
                                                CreatedAt lockedAt) {
        return new LockBalance(orderId, userId, lockedAmount, lockedAt);
    }

    public void subtractLockedAmount(Money amount) {
        if (lockedAmount.getValue().compareTo(amount.getValue()) < 0) {
            throw new IllegalArgumentException("Cannot subtract more than locked amount");
        }
        this.lockedAmount = lockedAmount.subtract(amount);
    }
}
