package shop.shportfolio.trading.domain;

import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.trading.domain.entity.CouponInfo;
import shop.shportfolio.trading.domain.entity.userbalance.LockBalance;
import shop.shportfolio.trading.domain.entity.userbalance.UserBalance;
import shop.shportfolio.trading.domain.valueobject.Money;

public interface UserBalanceDomainService {

    CouponInfo createCouponInfo(CouponId couponId, UserId userId, FeeDiscount feeDiscount,
                                IssuedAt issuedAt, UsageExpiryDate usageExpiryDate);

    void validateOrder(UserBalance userBalance, OrderPrice orderPrice, Quantity quantity, FeeAmount feeAmount);
    void validateMarketOrder(UserBalance userBalance, OrderPrice orderPrice, FeeAmount feeAmount);

    LockBalance lockMoney(UserBalance userBalance, OrderId orderId , Money amount);

    LockBalance unlockMoney(UserBalance userBalance,OrderId orderId , Money amount);

    LockBalance deductBalanceForTrade(UserBalance userBalance,OrderId orderId , Money amount);

    void depositMoney(UserBalance userBalance, Money amount);

    void withdrawMoney(UserBalance userBalance, Money amount);

    Boolean isLocked(LockBalance lockBalance);

    Boolean isPartiallyLocked(LockBalance lockBalance);

    Boolean isReleased(LockBalance lockBalance);

    void subtractLockedAmount(LockBalance lockBalance,Money amount);
}
