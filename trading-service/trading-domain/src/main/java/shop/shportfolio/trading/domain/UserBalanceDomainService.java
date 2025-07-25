package shop.shportfolio.trading.domain;

import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.trading.domain.entity.CouponInfo;
import shop.shportfolio.trading.domain.entity.userbalance.LockBalance;
import shop.shportfolio.trading.domain.entity.userbalance.UserBalance;
import shop.shportfolio.common.domain.valueobject.AssetCode;
import shop.shportfolio.common.domain.valueobject.Money;
import shop.shportfolio.trading.domain.valueobject.UserBalanceId;

public interface UserBalanceDomainService {

    CouponInfo createCouponInfo(CouponId couponId, UserId userId, FeeDiscount feeDiscount,
                                IssuedAt issuedAt, UsageExpiryDate usageExpiryDate);

    UserBalance createUserBalance(UserBalanceId userBalanceId, UserId userId, AssetCode assetCode, Money amount);

    void validateOrderByUserBalance(UserBalance userBalance, OrderPrice orderPrice, Quantity quantity, FeeAmount feeAmount);

    void validateMarketOrderByUserBalance(UserBalance userBalance, OrderPrice orderPrice, FeeAmount feeAmount);

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
