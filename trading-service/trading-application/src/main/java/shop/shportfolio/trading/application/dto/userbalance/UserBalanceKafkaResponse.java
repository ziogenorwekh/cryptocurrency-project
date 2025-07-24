package shop.shportfolio.trading.application.dto.userbalance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import shop.shportfolio.common.domain.valueobject.TransactionType;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class UserBalanceKafkaResponse {
    private final UUID userId;
    private final String assetCode;
    private final BigDecimal amount;
    private final TransactionType transactionType; // "DEPOSIT" or "WITHDRAWAL"
}
