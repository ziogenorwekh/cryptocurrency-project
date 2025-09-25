package shop.shportfolio.trading.application.ports.input.kafka.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import shop.shportfolio.common.domain.valueobject.MessageType;
import shop.shportfolio.common.domain.valueobject.Money;
import shop.shportfolio.common.domain.valueobject.TransactionId;
import shop.shportfolio.common.domain.valueobject.UserId;
import shop.shportfolio.trading.application.dto.userbalance.DepositWithdrawalKafkaResponse;
import shop.shportfolio.trading.application.handler.UserBalanceHandler;
import shop.shportfolio.trading.application.ports.input.kafka.DepositWithdrawalListener;
import shop.shportfolio.trading.application.ports.output.kafka.TradingDepositWithdrawalPublisher;
import shop.shportfolio.trading.domain.entity.userbalance.UserBalance;
import shop.shportfolio.trading.domain.event.DepositWithdrawalUpdatedEvent;
import shop.shportfolio.trading.domain.event.UserBalanceUpdatedEvent;
import shop.shportfolio.trading.domain.exception.TradingDomainException;
import shop.shportfolio.trading.domain.model.DepositWithdrawal;

import java.math.BigDecimal;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.UUID;

@Slf4j
@Component
public class DepositWithdrawalListenerImpl implements DepositWithdrawalListener {

    private final UserBalanceHandler userBalanceHandler;
    private final TradingDepositWithdrawalPublisher tradingDepositWithdrawalPublisher;
    public DepositWithdrawalListenerImpl(
            UserBalanceHandler userBalanceHandler,
            TradingDepositWithdrawalPublisher tradingDepositWithdrawalPublisher) {
        this.userBalanceHandler = userBalanceHandler;
        this.tradingDepositWithdrawalPublisher = tradingDepositWithdrawalPublisher;
    }

    @Override
    @Transactional
    public void deposit(DepositWithdrawalKafkaResponse response) {
        UserBalance userBalance = userBalanceHandler.findUserOptionalBalanceByUserId(response.getUserId())
                .orElseGet(() -> userBalanceHandler.createUserBalance(response.getUserId()));
        UserBalanceUpdatedEvent event = userBalanceHandler.deposit(userBalance,
                Money.of(BigDecimal.valueOf(response.getAmount())));

        UserBalance updated = event.getDomainType();
        DepositWithdrawal deposit = DepositWithdrawal.createDepositWithdrawal(
                new TransactionId(UUID.fromString(response.getTransactionId()))
                ,updated.getUserId(),
                updated.getAvailableMoney(),
                response.getTransactionType());
        DepositWithdrawalUpdatedEvent depositWithdrawalUpdatedEvent =
                new DepositWithdrawalUpdatedEvent(deposit,
                MessageType.UPDATE, ZonedDateTime.now(ZoneOffset.UTC));
        tradingDepositWithdrawalPublisher.publish(depositWithdrawalUpdatedEvent);
    }

    @Override
    @Transactional
    public void withdrawal(DepositWithdrawalKafkaResponse response) {
        log.info("withdrawal response message -> {}", response.toString());
        DepositWithdrawalUpdatedEvent depositWithdrawalUpdatedEvent;
        UserBalance userBalance = userBalanceHandler.findUserBalanceByUserId(new UserId(response.getUserId()));
        try {
            UserBalanceUpdatedEvent updatedEvent = userBalanceHandler.withdraw(userBalance,
                    Money.of(BigDecimal.valueOf(response.getAmount())));
            UserBalance updated = updatedEvent.getDomainType();
            DepositWithdrawal deposit = DepositWithdrawal.createDepositWithdrawal(
                    new TransactionId(UUID.fromString(response.getTransactionId())),
                    updated.getUserId(),
                    Money.of(BigDecimal.valueOf(response.getAmount())),
                    response.getTransactionType());
            depositWithdrawalUpdatedEvent = new DepositWithdrawalUpdatedEvent(deposit,
                    MessageType.UPDATE, ZonedDateTime.now(ZoneOffset.UTC));
            tradingDepositWithdrawalPublisher.publish(depositWithdrawalUpdatedEvent);
            log.info("success withdrawal userId -> {}, amount -> {}",
                    response.getUserId(), response.getAmount());
        } catch (TradingDomainException e) {
            log.error("failed withdrawal error is -> {}", e.getMessage());
            DepositWithdrawal deposit = DepositWithdrawal.createDepositWithdrawal(
                    new TransactionId(UUID.fromString(response.getTransactionId())),
                    userBalance.getUserId(),
                    Money.of(BigDecimal.valueOf(response.getAmount())),
                    response.getTransactionType());
            depositWithdrawalUpdatedEvent = new DepositWithdrawalUpdatedEvent(deposit,
                    MessageType.FAIL, ZonedDateTime.now(ZoneOffset.UTC));
            tradingDepositWithdrawalPublisher.publish(depositWithdrawalUpdatedEvent);
        }
    }
}
