package shop.shportfolio.trading.application.dto.userbalance;

import lombok.AllArgsConstructor;
import lombok.Getter;
import shop.shportfolio.common.domain.valueobject.MessageType;
import shop.shportfolio.common.domain.valueobject.TransactionType;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class DepositWithdrawalKafkaResponse {

    private final UUID userId;
    private final Long amount;
    private final TransactionType transactionType;
    private final Instant transactionTime;
    private final MessageType messageType;

}
