package shop.shportfolio.trading.domain.entity.userbalance;

import lombok.Getter;
import shop.shportfolio.common.domain.entity.AggregateRoot;
import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.trading.domain.exception.TradingDomainException;
import shop.shportfolio.trading.domain.valueobject.AssetCode;
import shop.shportfolio.trading.domain.valueobject.Money;
import shop.shportfolio.trading.domain.valueobject.UserBalanceId;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
public class UserBalance extends AggregateRoot<UserBalanceId> {
    private final UserId userId;
    private final AssetCode assetCode;
    private Money availableMoney;
    private final List<LockBalance> lockBalances;

    private UserBalance(UserBalanceId userBalanceId, UserId userId,
                        AssetCode assetCode, Money availableMoney, List<LockBalance> lockBalances) {
        setId(userBalanceId);
        this.userId = userId;
        this.assetCode = assetCode;
        this.availableMoney = availableMoney;
        this.lockBalances = lockBalances == null ? new ArrayList<>() : lockBalances;
    }

    public static UserBalance createUserBalance(UserBalanceId userBalanceId,
                                                UserId userId, AssetCode assetCode,
                                                Money availableMoney,
                                                List<LockBalance> lockBalances) {
        return new UserBalance(userBalanceId, userId, assetCode, availableMoney, lockBalances);
    }


    public void validateOrder(OrderPrice orderPrice, Quantity quantity, FeeAmount feeAmount) {
        BigDecimal totalAmount = orderPrice.getValue().multiply(quantity.getValue()).add(feeAmount.getValue());
        validateSufficientBalance(totalAmount);
    }

    public LockBalance lockMoney(OrderId orderId, Money amount) {
        if (availableMoney.getValue().compareTo(amount.getValue()) < 0) {
            throw new TradingDomainException("Insufficient available balance to lock");
        }
        // 이미 락 밸런스 존재하면 에러 던지기 (중복 방지)
        boolean exists = lockBalances.stream()
                .anyMatch(lb -> lb.getId().equals(orderId));
        if (exists) {
            throw new TradingDomainException("LockBalance for this order already exists");
        }

        this.availableMoney = availableMoney.subtract(amount);
        LockBalance lockBalance = LockBalance.createLockBalance(orderId, userId, amount,
                new CreatedAt(LocalDateTime.now(ZoneOffset.UTC)));
        lockBalances.add(lockBalance);
        return lockBalance;
    }

    public LockBalance unlockMoney(OrderId orderId, Money amount) {
        LockBalance lockBalance = lockBalances.stream()
                .filter(lb -> lb.getId().equals(orderId))
                .findFirst()
                .orElseThrow(() -> new TradingDomainException("LockBalance not found for order: " + orderId.getValue()));

        if (lockBalance.getLockedAmount().getValue().compareTo(amount.getValue()) < 0) {
            throw new TradingDomainException("Insufficient locked balance to unlock");
        }

        lockBalance.subtractLockedAmount(amount);
        this.availableMoney = this.availableMoney.add(amount);

        if (lockBalance.getLockedAmount().getValue().compareTo(BigDecimal.ZERO) == 0) {
            lockBalances.remove(lockBalance);
        }
        return lockBalance;
    }

    public LockBalance deductBalanceForTrade(OrderId orderId, Money amount) {
        LockBalance lockBalance = lockBalances.stream()
                .filter(lb -> lb.getId().equals(orderId))
                .findFirst()
                .orElseThrow(() -> new TradingDomainException("LockBalance not found for order: " + orderId.getValue()));

        if (lockBalance.getLockedAmount().getValue().compareTo(amount.getValue()) < 0) {
            throw new TradingDomainException("Insufficient locked balance to deduct");
        }

        lockBalance.subtractLockedAmount(amount);

        if (lockBalance.getLockedAmount().getValue().compareTo(BigDecimal.ZERO) == 0) {
            lockBalances.remove(lockBalance);
        }
        return lockBalance;
    }

    /**
     * 입금 처리 — 사용자의 가용 잔고를 증가시킨다.
     */
    public void deposit(Money amount) {
        if (amount.getValue().compareTo(BigDecimal.ZERO) <= 0) {
            throw new TradingDomainException("Deposit amount must be positive");
        }
        this.availableMoney = this.availableMoney.add(amount);
    }

    /**
     * 출금 처리 — 사용자의 가용 잔고를 감소시킨다.
     */
    public void withdraw(Money amount) {
        if (amount.getValue().compareTo(BigDecimal.ZERO) <= 0) {
            throw new TradingDomainException("Withdrawal amount must be positive");
        }
        if (availableMoney.getValue().compareTo(amount.getValue()) < 0) {
            throw new TradingDomainException("Insufficient available balance to withdraw");
        }
        this.availableMoney = this.availableMoney.subtract(amount);
    }


    private void validateSufficientBalance(BigDecimal totalAmount) {
        if (totalAmount.compareTo(availableMoney.getValue()) > 0) {
            throw new TradingDomainException(
                    String.format("Order amount %s exceeds available balance %s", totalAmount, availableMoney.getValue()));
        }
    }
}
