package shop.shportfolio.trading.domain;

import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.trading.domain.entity.CouponInfo;
import shop.shportfolio.trading.domain.entity.userbalance.LockBalance;
import shop.shportfolio.trading.domain.entity.userbalance.UserBalance;
import shop.shportfolio.trading.domain.valueobject.AssetCode;
import shop.shportfolio.common.domain.valueobject.Money;
import shop.shportfolio.trading.domain.valueobject.UserBalanceId;

public class UserBalanceDomainServiceImpl implements UserBalanceDomainService {
    @Override
    public CouponInfo createCouponInfo(CouponId couponId, UserId userId, FeeDiscount feeDiscount, IssuedAt issuedAt, UsageExpiryDate usageExpiryDate) {
        return CouponInfo.createCouponInfo(couponId, userId, feeDiscount, issuedAt, usageExpiryDate);
    }

    @Override
    public UserBalance createUserBalance(UserBalanceId userBalanceId, UserId userId, AssetCode assetCode, Money amount) {
        return UserBalance.createUserBalance(userBalanceId, userId, assetCode, amount, null);
    }

    @Override
    public void validateOrderByUserBalance(UserBalance userBalance, OrderPrice orderPrice, Quantity quantity, FeeAmount feeAmount) {
        userBalance.validateOrder(orderPrice, quantity, feeAmount);
    }

    @Override
    public void validateMarketOrderByUserBalance(UserBalance userBalance, OrderPrice orderPrice, FeeAmount feeAmount) {
        userBalance.validateMarketOrder(orderPrice, feeAmount);
    }

    @Override
    public LockBalance lockMoney(UserBalance userBalance, OrderId orderId, Money amount) {
        return userBalance.lockMoney(orderId, amount);
    }

    @Override
    public LockBalance unlockMoney(UserBalance userBalance, OrderId orderId, Money amount) {
        return userBalance.unlockMoney(orderId, amount);
    }

    @Override
    public LockBalance deductBalanceForTrade(UserBalance userBalance, OrderId orderId, Money amount) {
        return userBalance.deductBalanceForTrade(orderId, amount);
    }

    @Override
    public void depositMoney(UserBalance userBalance, Money amount) {
        userBalance.deposit(amount);
    }

    @Override
    public void withdrawMoney(UserBalance userBalance, Money amount) {
        userBalance.withdraw(amount);
    }

    @Override
    public Boolean isLocked(LockBalance lockBalance) {
        return lockBalance.isLocked();
    }

    @Override
    public Boolean isPartiallyLocked(LockBalance lockBalance) {
        return lockBalance.isPartiallyLocked();
    }

    @Override
    public Boolean isReleased(LockBalance lockBalance) {
        return lockBalance.isReleased();
    }

    @Override
    public void subtractLockedAmount(LockBalance lockBalance, Money amount) {
        lockBalance.subtractLockedAmount(amount);
    }

}
