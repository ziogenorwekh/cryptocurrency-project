package shop.shportfolio.trading.domain;

import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.trading.domain.entity.CouponInfo;
import shop.shportfolio.trading.domain.entity.userbalance.UserBalance;
import shop.shportfolio.trading.domain.valueobject.Money;

public interface UserBalanceDomainService {

    CouponInfo createCouponInfo(CouponId couponId, UserId userId, FeeDiscount feeDiscount,
                                IssuedAt issuedAt, UsageExpiryDate usageExpiryDate);

    void validateOrder(UserBalance userBalance, OrderPrice orderPrice, Quantity quantity, FeeAmount feeAmount);

    void lockMoney(UserBalance userBalance, Money amount);

    void unlockMoney(UserBalance userBalance, Money amount);

    void deductBalanceForTrade(UserBalance userBalance, Money amount);

    void depositMoney(UserBalance userBalance, Money amount);

    void withdrawMoney(UserBalance userBalance, Money amount);
}
