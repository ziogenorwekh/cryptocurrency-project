package shop.shportfolio.portfolio.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import shop.shportfolio.common.domain.valueobject.TransactionType;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class DepositWithdrawalKafkaResponse {

    private final String transactionId;
    private final UUID userId;
    private final TransactionType transactionType;
    private final Long amount;
    private final LocalDateTime transactionTime;
}
