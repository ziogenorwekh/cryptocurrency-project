package shop.shportfolio.trading.domain;

import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.trading.domain.entity.CouponInfo;
import shop.shportfolio.trading.domain.entity.userbalance.UserBalance;
import shop.shportfolio.trading.domain.valueobject.Money;

public class UserBalanceDomainServiceImpl implements UserBalanceDomainService {
    @Override
    public CouponInfo createCouponInfo(CouponId couponId, UserId userId, FeeDiscount feeDiscount, IssuedAt issuedAt, UsageExpiryDate usageExpiryDate) {
        return CouponInfo.createCouponInfo(couponId, userId, feeDiscount, issuedAt, usageExpiryDate);
    }

    @Override
    public void validateOrder(UserBalance userBalance, OrderPrice orderPrice, Quantity quantity, FeeAmount feeAmount) {
        userBalance.validateOrder(orderPrice, quantity, feeAmount);
    }

    @Override
    public void lockMoney(UserBalance userBalance, Money amount) {
    }

    @Override
    public void unlockMoney(UserBalance userBalance, Money amount) {
    }

    @Override
    public void deductBalanceForTrade(UserBalance userBalance, Money amount) {
    }

    @Override
    public void depositMoney(UserBalance userBalance, Money amount) {
        userBalance.deposit(amount);
    }

    @Override
    public void withdrawMoney(UserBalance userBalance, Money amount) {
        userBalance.withdraw(amount);
    }

}
