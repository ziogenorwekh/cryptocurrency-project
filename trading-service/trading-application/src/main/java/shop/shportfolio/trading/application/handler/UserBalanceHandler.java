package shop.shportfolio.trading.application.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.trading.application.exception.UserBalanceNotFoundException;
import shop.shportfolio.trading.application.ports.output.repository.TradingUserBalanceRepositoryPort;
import shop.shportfolio.trading.domain.UserBalanceDomainService;
import shop.shportfolio.trading.domain.entity.userbalance.LockBalance;
import shop.shportfolio.trading.domain.entity.userbalance.UserBalance;
import shop.shportfolio.trading.domain.event.UserBalanceUpdatedEvent;

import java.math.BigDecimal;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Optional;

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

    public void deduct(UserId userId, OrderId orderId, BigDecimal amount) {
        if (!orderId.getValue().contains("anonymous")) {
            UserBalance userBalance = tradingUserBalanceRepositoryPort.findUserBalanceByUserId(userId.getValue())
                    .orElseThrow(() -> new UserBalanceNotFoundException(String.format("%s is not found", userId.getValue())));
            Money money = Money.of(amount);
            userBalanceDomainService.deductBalanceForTrade(userBalance, orderId, money);
            log.info("[BalanceDeduct] Deducted for trade: userId={}, orderId={}, amount={}, remainingAvail={}",
                    userBalance.getUserId().getValue(),
                    orderId.getValue(),
                    amount,
                    userBalance.getAvailableMoney().getValue());
            tradingUserBalanceRepositoryPort.saveUserBalance(userBalance);
        }
    }

    public Optional<UserBalanceUpdatedEvent> deductTrade(UserId userId, OrderId orderId, BigDecimal amount) {
        if (!orderId.getValue().contains("anonymous")) {
            UserBalance userBalance = tradingUserBalanceRepositoryPort.findUserBalanceByUserId(userId.getValue())
                    .orElseThrow(() -> new UserBalanceNotFoundException(String.format("%s is not found", userId.getValue())));
            Money money = Money.of(amount);
            userBalanceDomainService.deductBalanceForTrade(userBalance, orderId, money);
            log.info("[BalanceDeduct] Deducted for trade: userId={}, orderId={}, amount={}, remainingAvail={}",
                    userBalance.getUserId().getValue(),
                    orderId.getValue(),
                    amount,
                    userBalance.getAvailableMoney().getValue());
            tradingUserBalanceRepositoryPort.saveUserBalance(userBalance);
            return Optional.of(new UserBalanceUpdatedEvent(userBalance, MessageType.UPDATE, ZonedDateTime.now(ZoneOffset.UTC)));
        }
        return Optional.empty();
    }

    public Optional<UserBalanceUpdatedEvent> creditTrade(UserId userId, OrderId orderId, BigDecimal amount) {
        if (!orderId.getValue().contains("anonymous")) {
            UserBalance userBalance = tradingUserBalanceRepositoryPort.findUserBalanceByUserId(userId.getValue())
                    .orElseThrow(() -> new UserBalanceNotFoundException(String.format("%s is not found", userId.getValue())));
            userBalanceDomainService.depositMoney(userBalance, Money.of(amount));
            log.info("[BalanceCredit] Credited for trade: userId={}, orderId={}, amount={}, newAvailable={}",
                    userBalance.getUserId().getValue(),
                    orderId.getValue(),
                    amount,
                    userBalance.getAvailableMoney().getValue());
            tradingUserBalanceRepositoryPort.saveUserBalance(userBalance);
            return Optional.of(new UserBalanceUpdatedEvent(userBalance, MessageType.UPDATE, ZonedDateTime.now(ZoneOffset.UTC)));
        }
        return Optional.empty();
    }

    public void credit(UserId userId, OrderId orderId, BigDecimal amount) {
        if (!orderId.getValue().contains("anonymous")) {
            UserBalance userBalance = tradingUserBalanceRepositoryPort.findUserBalanceByUserId(userId.getValue())
                    .orElseThrow(() -> new UserBalanceNotFoundException(String.format("%s is not found", userId.getValue())));
            userBalanceDomainService.depositMoney(userBalance, Money.of(amount));
            log.info("[BalanceCredit] Credited for trade: userId={}, orderId={}, amount={}, newAvailable={}",
                    userBalance.getUserId().getValue(),
                    orderId.getValue(),
                    amount,
                    userBalance.getAvailableMoney().getValue());
            tradingUserBalanceRepositoryPort.saveUserBalance(userBalance);
        }
    }

    public void saveUserBalanceForLockBalance(UserBalance userBalance, OrderId orderId, Money amount) {
        LockBalance lockBalance = userBalanceDomainService.lockMoney(userBalance, orderId, amount);
        log.info("[LockBalance] Created lockBalance: {}, remainingAvail={}", lockBalance, userBalance.getAvailableMoney().getValue());
        tradingUserBalanceRepositoryPort.saveUserBalance(userBalance);
    }

    public UserBalanceUpdatedEvent finalizeLockedAmount(UserBalance userBalance, LockBalance lockBalance) {
        UserBalanceUpdatedEvent event = userBalanceDomainService.depositMoney(userBalance, lockBalance.getLockedAmount());
        userBalance.getLockBalances().remove(lockBalance);
        tradingUserBalanceRepositoryPort.saveUserBalance(userBalance);
        log.info("[FinalizeLock] Released locked amount: userId={}, lockBalance={}, newAvailable={}",
                userBalance.getUserId().getValue(),
                lockBalance,
                userBalance.getAvailableMoney().getValue());
        return event;
    }

    public UserBalance findUserBalanceByUserId(UserId userId) {
        UserBalance userBalance = tradingUserBalanceRepositoryPort.findUserBalanceByUserId(userId.getValue())
                .orElseThrow(() -> new UserBalanceNotFoundException(
                        String.format("No user balance found for userId: %s", userId.getValue())));
        log.info("[FindBalance] userId={}, availableMoney={}", userId.getValue(), userBalance.getAvailableMoney().getValue());
        return userBalance;
    }

    public UserBalanceUpdatedEvent deposit(UserBalance userBalance, Money amount) {
        UserBalanceUpdatedEvent event = userBalanceDomainService.depositMoney(userBalance, amount);
        tradingUserBalanceRepositoryPort.saveUserBalance(userBalance);
        log.info("[Deposit] Deposited: userId={}, amount={}, newAvailable={}",
                userBalance.getUserId().getValue(), amount.getValue(), userBalance.getAvailableMoney().getValue());
        return event;
    }

    public UserBalanceUpdatedEvent withdraw(UserBalance userBalance, Money amount) {
        UserBalanceUpdatedEvent event = userBalanceDomainService.withdrawMoney(userBalance, amount);
        tradingUserBalanceRepositoryPort.saveUserBalance(userBalance);
        log.info("[Withdraw] Withdrawn: userId={}, amount={}, newAvailable={}",
                userBalance.getUserId().getValue(), amount.getValue(), userBalance.getAvailableMoney().getValue());
        return event;
    }

}
