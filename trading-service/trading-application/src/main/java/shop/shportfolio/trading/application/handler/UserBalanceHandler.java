package shop.shportfolio.trading.application.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.trading.application.exception.InsufficientBalanceException;
import shop.shportfolio.trading.application.exception.UserBalanceNotFoundException;
import shop.shportfolio.trading.application.ports.output.repository.TradingUserBalanceRepositoryPort;
import shop.shportfolio.trading.domain.UserBalanceDomainService;
import shop.shportfolio.trading.domain.entity.userbalance.CryptoBalance;
import shop.shportfolio.trading.domain.entity.userbalance.LockBalance;
import shop.shportfolio.trading.domain.entity.userbalance.UserBalance;
import shop.shportfolio.trading.domain.event.UserBalanceUpdatedEvent;
import shop.shportfolio.trading.domain.valueobject.UserBalanceId;

import java.math.BigDecimal;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class UserBalanceHandler {

    private final TradingUserBalanceRepositoryPort tradingUserBalanceRepositoryPort;
    private final UserBalanceDomainService userBalanceDomainService;

    public UserBalanceHandler(TradingUserBalanceRepositoryPort tradingUserBalanceRepositoryPort,
                              UserBalanceDomainService userBalanceDomainService) {
        this.tradingUserBalanceRepositoryPort = tradingUserBalanceRepositoryPort;
        this.userBalanceDomainService = userBalanceDomainService;
    }

    public UserBalance validateMarketOrder(UserId userId, OrderPrice orderPrice, FeeAmount feeAmount) {
        UserBalance userBalance = findUserBalanceByUserId(userId);
        userBalanceDomainService.validateMarketOrderByUserBalance(userBalance, orderPrice, feeAmount);
        log.info("[MarketOrder] Validated user balance: userId={}, availableMoney={}",
                userId.getValue(), userBalance.getAvailableMoney().getValue());
        return userBalance;
    }

    public UserBalance validateLimitAndReservationOrder(UserId userId, OrderPrice orderPrice,
                                                        Quantity quantity, FeeAmount feeAmount) {
        UserBalance userBalance = findUserBalanceByUserId(userId);
        userBalanceDomainService.validateOrderByUserBalance(userBalance, orderPrice, quantity, feeAmount);
        log.info("[OrderValidation] Validated user balance for order: userId={}, availableMoney={}",
                userId.getValue(), userBalance.getAvailableMoney().getValue());
        return userBalance;
    }

    public Optional<UserBalanceUpdatedEvent> deduct(UserId userId, OrderId orderId, BigDecimal amount) {
        if (!orderId.getValue().contains("anonymous")) {
            UserBalance userBalance = tradingUserBalanceRepositoryPort.findUserBalanceByUserIdWithLock(userId.getValue())
                    .orElseThrow(() -> new UserBalanceNotFoundException(String.format("%s is not found", userId.getValue())));
            Money money = Money.of(amount);
            userBalanceDomainService.deductBalanceForTrade(userBalance, orderId, money);
            log.info("[BalanceDeduct] Deducted for trade: userId={}, orderId={}, amount={}, remainingAvail={}",
                    userBalance.getUserId().getValue(),
                    orderId.getValue(),
                    amount,
                    userBalance.getAvailableMoney().getValue());
            UserBalance savedUserBalance = tradingUserBalanceRepositoryPort.saveUserBalance(userBalance);
            return Optional.ofNullable(userBalanceDomainService.createUserBalanceUpdatedEvent(savedUserBalance,
                    DirectionType.SUB, AssetCode.KRW, Money.of(amount)));
        } else{
            return Optional.empty();
        }
    }

    public Optional<UserBalanceUpdatedEvent> credit(UserId userId, OrderId orderId, BigDecimal amount) {
        if (!orderId.getValue().contains("anonymous")) {
            UserBalance userBalance = tradingUserBalanceRepositoryPort.findUserBalanceByUserIdWithLock(userId.getValue())
                    .orElseThrow(() -> new UserBalanceNotFoundException(String.format("%s is not found", userId.getValue())));
            userBalanceDomainService.depositMoney(userBalance, Money.of(amount));
            log.info("[BalanceCredit] Credited for trade: userId={}, orderId={}, amount={}, newAvailable={}",
                    userBalance.getUserId().getValue(),
                    orderId.getValue(),
                    amount,
                    userBalance.getAvailableMoney().getValue());
            UserBalance savedUserBalance = tradingUserBalanceRepositoryPort.saveUserBalance(userBalance);
            return Optional.ofNullable(userBalanceDomainService.createUserBalanceUpdatedEvent(savedUserBalance, DirectionType.ADD
                    , AssetCode.KRW, Money.of(amount)));
        } else {
            return Optional.empty();
        }
    }

    public void saveUserBalanceForLockBalance(UserBalance userBalance, OrderId orderId, Money amount) {
        LockBalance lockBalance = userBalanceDomainService.lockMoney(userBalance, orderId, amount);
        log.info("[LockBalance] Created lockBalance: {}, remainingAvail={}", lockBalance, userBalance.getAvailableMoney().getValue());
        tradingUserBalanceRepositoryPort.saveUserBalance(userBalance);
    }

    public void validateSellOrder(UUID userId, String marketId, BigDecimal quantity) {
        CryptoBalance cryptoBalance = tradingUserBalanceRepositoryPort
                .findCryptoBalanceByUserIdAndMarketId(userId, marketId)
                .orElseThrow(() -> new UserBalanceNotFoundException(
                        String.format("Crypto balance for marketId %s not found (userId=%s)", marketId, userId)
                ));

        BigDecimal available = cryptoBalance.getPurchasedQuantity().getValue();
        if (available.compareTo(quantity) < 0) {
            throw new InsufficientBalanceException(
                    String.format("Insufficient balance for marketId %s (userId=%s). Required=%s, Available=%s",
                            marketId, userId, quantity, available)
            );
        }
    }


    public void finalizeLockedAmount(UserBalance userBalance, LockBalance lockBalance) {
        userBalanceDomainService.depositMoney(userBalance, lockBalance.getLockedAmount());
        userBalance.getLockBalances().remove(lockBalance);
        tradingUserBalanceRepositoryPort.saveUserBalance(userBalance);
        log.info("[FinalizeLock] Released locked amount: userId={}, lockBalance={}, newAvailable={}",
                userBalance.getUserId().getValue(),
                lockBalance,
                userBalance.getAvailableMoney().getValue());
    }

    @Transactional
    public void lockUserBalanceForOrder(UserId userId, OrderId orderId, OrderPrice orderPrice, FeeAmount feeAmount, Quantity quantity) {
        UserBalance userBalance = tradingUserBalanceRepositoryPort.findUserBalanceByUserIdWithLock(userId.getValue())
                .orElseThrow(() -> new UserBalanceNotFoundException("User balance not found"));

        if (quantity == null) { // Market Buy (Price 기준)
            userBalanceDomainService.validateMarketOrderByUserBalance(userBalance, orderPrice, feeAmount);
        } else { // Limit/Reservation Buy (Quantity 기준)
            userBalanceDomainService.validateOrderByUserBalance(userBalance, orderPrice, quantity, feeAmount);
        }

        // 3. 잔고 잠금 및 저장
        BigDecimal totalAmountValue = (quantity == null)
                ? orderPrice.getValue().add(feeAmount.getValue()) // Market Buy
                : orderPrice.getValue().multiply(quantity.getValue()).add(feeAmount.getValue()); // Limit/Reservation Buy

        Money totalAmount = Money.of(totalAmountValue);

        LockBalance lockBalance = userBalanceDomainService.lockMoney(userBalance, orderId, totalAmount);
        tradingUserBalanceRepositoryPort.saveUserBalance(userBalance);

        log.info("[LockBalance] Created lockBalance after re-lock: {}, remainingAvail={}", lockBalance, userBalance.getAvailableMoney().getValue());
    }

    public UserBalance findUserBalanceByUserId(UserId userId) {
        UserBalance userBalance = tradingUserBalanceRepositoryPort.findUserBalanceByUserId(userId.getValue())
                .orElseThrow(() -> new UserBalanceNotFoundException(
                        String.format("No user balance found for userId: %s", userId.getValue())));
        log.info("[FindBalance] userId={}, availableMoney={}", userId.getValue(), userBalance.getAvailableMoney().getValue());
        return userBalance;
    }

    public UserBalance deposit(UserBalance userBalance, Money amount) {
        userBalanceDomainService.depositMoney(userBalance, amount);
        tradingUserBalanceRepositoryPort.saveUserBalance(userBalance);
        log.info("[Deposit] Deposited: userId={}, amount={}, newAvailable={}",
                userBalance.getUserId().getValue(), amount.getValue(), userBalance.getAvailableMoney().getValue());
        return userBalance;
    }

    public UserBalance withdraw(UserBalance userBalance, Money amount) {
        userBalanceDomainService.withdrawMoney(userBalance, amount);
        tradingUserBalanceRepositoryPort.saveUserBalance(userBalance);
        log.info("[Withdraw] Withdrawn: userId={}, amount={}, newAvailable={}",
                userBalance.getUserId().getValue(), amount.getValue(),
                userBalance.getAvailableMoney().getValue());
        return userBalance;
    }

    public Optional<UserBalance> findUserOptionalBalanceByUserId(UUID userId) {
        return tradingUserBalanceRepositoryPort.findUserBalanceByUserIdWithLock(userId);
    }

    public UserBalance createUserBalance(UUID userId) {
        Optional<UserBalance> balance = tradingUserBalanceRepositoryPort.findUserBalanceByUserIdWithLock(userId);
        if (balance.isPresent()) {
            return balance.get();
        } else {
            log.info("[CreateBalance] Creating user balance for userId: {}", userId);
            UserBalance userBalance = userBalanceDomainService.createUserBalance(
                    new UserBalanceId(UUID.randomUUID()), new UserId(userId), AssetCode.KRW, Money.of(BigDecimal.ZERO));
            tradingUserBalanceRepositoryPort.saveUserBalance(userBalance);
            return userBalance;
        }
    }

    public void deleteUserBalance(UUID userId) {
        tradingUserBalanceRepositoryPort.findUserBalanceByUserIdWithLock(userId)
                .ifPresent(userBalance -> tradingUserBalanceRepositoryPort.deleteUserBalanceByUserId(userId));
        log.info("[DeleteBalance] Deleted user balance: userId={}", userId);
    }

    public void unlockBalance(UUID userId,String orderId) {
        tradingUserBalanceRepositoryPort.findUserBalanceByUserIdWithLock(userId)
                .ifPresent(userBalance -> {
                    Optional<LockBalance> lockBalance = userBalance.getLockBalances().stream()
                            .filter(l -> l.getId().getValue().equals(orderId))
                            .findFirst();

                    lockBalance.ifPresent(l -> {
                        log.info("lock balance found : {} , lock amount : {}", l.getId().getValue(),
                                l.getLockedAmount().getValue());
                        userBalanceDomainService.unlockMoney(userBalance, l.getId(), l.getLockedAmount());
                        userBalance.getLockBalances().remove(l);
                        tradingUserBalanceRepositoryPort.saveUserBalance(userBalance);
                        log.info("[UnlockBalance] Unlocked balance for orderId: {}, userId: {}, newAvailable={}",
                                orderId, userId, userBalance.getAvailableMoney().getValue());
                    });
                });
    }
}
