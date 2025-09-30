package shop.shportfolio.trading.application.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import shop.shportfolio.common.domain.valueobject.MessageType;
import shop.shportfolio.common.domain.valueobject.Money;
import shop.shportfolio.common.domain.valueobject.TransactionId;
import shop.shportfolio.common.domain.valueobject.UserId;
import shop.shportfolio.trading.application.dto.userbalance.DepositWithdrawalKafkaResponse;
import shop.shportfolio.trading.domain.entity.userbalance.UserBalance;
import shop.shportfolio.trading.domain.event.DepositWithdrawalUpdatedEvent;
import shop.shportfolio.trading.domain.exception.TradingDomainException;
import shop.shportfolio.trading.domain.model.DepositWithdrawal;

import java.math.BigDecimal;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.UUID;

@Slf4j
@Component
public class DepositWithdrawalTransactionHandler {

    private final UserBalanceHandler userBalanceHandler;

    public DepositWithdrawalTransactionHandler(UserBalanceHandler userBalanceHandler) {
        this.userBalanceHandler = userBalanceHandler;
    }

    @Transactional
    public DepositWithdrawalUpdatedEvent processDepositTransaction(DepositWithdrawalKafkaResponse response) {
        UserBalance userBalance = userBalanceHandler.findUserOptionalBalanceByUserId(response.getUserId())
                .orElseGet(() -> userBalanceHandler.createUserBalance(response.getUserId()));

        // DB 변경 로직
        UserBalance deposited = userBalanceHandler.deposit(userBalance,
                Money.of(BigDecimal.valueOf(response.getAmount())));
        DepositWithdrawal deposit = DepositWithdrawal.createDepositWithdrawal(
                new TransactionId(UUID.fromString(response.getTransactionId()))
                ,deposited.getUserId(),
                deposited.getAvailableMoney(),
                response.getTransactionType());

        // 발행할 이벤트 객체만 리턴
        return new DepositWithdrawalUpdatedEvent(deposit,
                MessageType.UPDATE, ZonedDateTime.now(ZoneOffset.UTC));
    }

    // 네 지적대로 protected로 수정함.
    @Transactional
    public DepositWithdrawalUpdatedEvent processWithdrawalTransaction(DepositWithdrawalKafkaResponse response) {
        DepositWithdrawalUpdatedEvent depositWithdrawalUpdatedEvent;
        UserBalance userBalance = userBalanceHandler.findUserBalanceByUserId(new UserId(response.getUserId()));

        try {
            // DB 변경 로직
            UserBalance withdrawn = userBalanceHandler.withdraw(userBalance,
                    Money.of(BigDecimal.valueOf(response.getAmount())));

            // DB 커밋 성공 시 이벤트 객체 생성
            DepositWithdrawal deposit = DepositWithdrawal.createDepositWithdrawal(
                    new TransactionId(UUID.fromString(response.getTransactionId())),
                    withdrawn.getUserId(),
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
