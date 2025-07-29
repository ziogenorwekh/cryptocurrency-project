package shop.shportfolio.portfolio.application.command.create;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class WithdrawalCreatedResponse {
    private final UUID userId;
    private final Long withdrawalAmount;
    private final LocalDateTime transactionTime;
    private final String message;
}
