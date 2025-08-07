package shop.shportfolio.trading.application.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.trading.application.exception.UserBalanceNotFoundException;
import shop.shportfolio.trading.application.ports.output.repository.TradingUserBalanceRepositoryPort;
import shop.shportfolio.trading.domain.UserBalanceDomainService;
import shop.shportfolio.trading.domain.entity.userbalance.LockBalance;
import shop.shportfolio.trading.domain.entity.userbalance.UserBalance;
import shop.shportfolio.common.domain.valueobject.Money;
import shop.shportfolio.trading.domain.event.UserBalanceUpdatedEvent;

import java.math.BigDecimal;


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
        return userBalance;
    }

    public UserBalance validateLimitAndReservationOrder(UserId userId, OrderPrice orderPrice,
                                                        Quantity quantity, FeeAmount feeAmount) {
        UserBalance userBalance = findUserBalanceByUserId(userId);
        userBalanceDomainService.validateOrderByUserBalance(userBalance, orderPrice, quantity, feeAmount);
        return userBalance;
    }

    /**
     * 매칭된 거래에 대해 잔고 차감
     * @param userBalance 대상 유저의 잔고
     * @param orderId 거래 주문 ID
     * @param amount 차감할 금액(BigDecimal)
     */
    public void deduct(UserBalance userBalance, OrderId orderId, BigDecimal amount) {
        Money money = Money.of(amount);
        userBalanceDomainService.deductBalanceForTrade(userBalance, orderId, money);
        log.info("Deducted balance for trade: userId={}, orderId={}, amount={}",
                userBalance.getUserId().getValue(), orderId.getValue(), amount);
        tradingUserBalanceRepositoryPort.saveUserBalance(userBalance);
    }

    public void saveUserBalanceForLockBalance(UserBalance userBalance, OrderId orderId, Money amount) {
        LockBalance lockBalance = userBalanceDomainService.lockMoney(userBalance, orderId, amount);
        log.info("create LockBalance : {}", lockBalance);
        log.info("remaining UserBalance AvailMoney is : {}",
                userBalance.getAvailableMoney().getValue());
        tradingUserBalanceRepositoryPort.saveUserBalance(userBalance);
    }

    public UserBalanceUpdatedEvent finalizeLockedAmount(UserBalance userBalance, LockBalance lockBalance) {
        UserBalanceUpdatedEvent userBalanceUpdatedEvent = userBalanceDomainService.depositMoney(userBalance,
                lockBalance.getLockedAmount());
        userBalance.getLockBalances().remove(lockBalance);
        tradingUserBalanceRepositoryPort.saveUserBalance(userBalance);
        return userBalanceUpdatedEvent;
    }

    public UserBalance findUserBalanceByUserId(UserId userId) {
        return tradingUserBalanceRepositoryPort.findUserBalanceByUserId(userId.getValue())
                .orElseThrow(() -> new UserBalanceNotFoundException(
                        String.format("No user balance found for userId: %s", userId.getValue())));
    }

    public UserBalanceUpdatedEvent deposit(UserBalance userBalance, Money amount) {
        UserBalanceUpdatedEvent userBalanceUpdatedEvent = userBalanceDomainService.depositMoney(userBalance, amount);
        tradingUserBalanceRepositoryPort.saveUserBalance(userBalance);
        return userBalanceUpdatedEvent;
    }

    public UserBalanceUpdatedEvent withdraw(UserBalance userBalance, Money amount) {
        UserBalanceUpdatedEvent userBalanceUpdatedEvent = userBalanceDomainService.withdrawMoney(userBalance, amount);
        tradingUserBalanceRepositoryPort.saveUserBalance(userBalance);
        return userBalanceUpdatedEvent;
    }

}
