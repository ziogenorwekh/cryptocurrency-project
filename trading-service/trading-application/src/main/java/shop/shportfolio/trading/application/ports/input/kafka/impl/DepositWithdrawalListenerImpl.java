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
    public void deposit(DepositWithdrawalKafkaResponse response) { // 👈 @Transactional 제거
        try {
            // DB 트랜잭션 메서드 호출
            DepositWithdrawalUpdatedEvent eventToPublish = processDepositTransaction(response);
            tradingDepositWithdrawalPublisher.publish(eventToPublish);
        } catch (Exception e) {
            log.error("[Deposit] Failed to process deposit for user {}: {}", response.getUserId(), e.getMessage());
            // 트랜잭션 실패 시 발행을 시도하지 않음.
            // (입금은 일반적으로 실패할 경우 재시도가 기본이므로 별도 FAIL 메시지는 발행하지 않음)
        }
    }

    @Override
    public void withdrawal(DepositWithdrawalKafkaResponse response) { // 👈 @Transactional 제거
        log.info("withdrawal response message -> {}", response.toString());

        // 트랜잭션 메서드 호출 (try-catch 블록 유지)
        DepositWithdrawalUpdatedEvent eventToPublish = processWithdrawalTransaction(response);
        tradingDepositWithdrawalPublisher.publish(eventToPublish);

        log.info("Finished withdrawal process for user {}", response.getUserId());
    }

    @Transactional
    protected DepositWithdrawalUpdatedEvent processDepositTransaction(DepositWithdrawalKafkaResponse response) {
        UserBalance userBalance = userBalanceHandler.findUserOptionalBalanceByUserId(response.getUserId())
                .orElseGet(() -> userBalanceHandler.createUserBalance(response.getUserId()));

        // DB 변경 로직
        UserBalanceUpdatedEvent event = userBalanceHandler.deposit(userBalance,
                Money.of(BigDecimal.valueOf(response.getAmount())));

        UserBalance updated = event.getDomainType();
        DepositWithdrawal deposit = DepositWithdrawal.createDepositWithdrawal(
                new TransactionId(UUID.fromString(response.getTransactionId()))
                ,updated.getUserId(),
                updated.getAvailableMoney(),
                response.getTransactionType());

        // 발행할 이벤트 객체만 리턴
        return new DepositWithdrawalUpdatedEvent(deposit,
                MessageType.UPDATE, ZonedDateTime.now(ZoneOffset.UTC));
    }

    // 네 지적대로 protected로 수정함.
    @Transactional
    protected DepositWithdrawalUpdatedEvent processWithdrawalTransaction(DepositWithdrawalKafkaResponse response) {
        DepositWithdrawalUpdatedEvent depositWithdrawalUpdatedEvent;
        UserBalance userBalance = userBalanceHandler.findUserBalanceByUserId(new UserId(response.getUserId()));

        try {
            // DB 변경 로직
            UserBalanceUpdatedEvent updatedEvent = userBalanceHandler.withdraw(userBalance,
                    Money.of(BigDecimal.valueOf(response.getAmount())));

            // DB 커밋 성공 시 이벤트 객체 생성
            UserBalance updated = updatedEvent.getDomainType();
            DepositWithdrawal deposit = DepositWithdrawal.createDepositWithdrawal(
                    new TransactionId(UUID.fromString(response.getTransactionId())),
                    updated.getUserId(),
                    Money.of(BigDecimal.valueOf(response.getAmount())),
                    response.getTransactionType());

            depositWithdrawalUpdatedEvent = new DepositWithdrawalUpdatedEvent(deposit,
                    MessageType.UPDATE, ZonedDateTime.now(ZoneOffset.UTC));

            log.info("success withdrawal userId -> {}, amount -> {}",
                    response.getUserId(), response.getAmount());

        } catch (TradingDomainException e) {
            // DB 커밋 실패(잔액 부족 등) 시 롤백되고, FAIL 메시지 이벤트 객체 생성
            log.error("failed withdrawal error is -> {}", e.getMessage());
            DepositWithdrawal deposit = DepositWithdrawal.createDepositWithdrawal(
                    new TransactionId(UUID.fromString(response.getTransactionId())),
                    userBalance.getUserId(),
                    Money.of(BigDecimal.valueOf(response.getAmount())),
                    response.getTransactionType());

            depositWithdrawalUpdatedEvent = new DepositWithdrawalUpdatedEvent(deposit,
                    MessageType.FAIL, ZonedDateTime.now(ZoneOffset.UTC));
        }

        // 발행할 이벤트 객체 리턴
        return depositWithdrawalUpdatedEvent;
    }
}