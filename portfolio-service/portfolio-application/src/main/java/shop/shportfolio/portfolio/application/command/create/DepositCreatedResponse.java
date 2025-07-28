package shop.shportfolio.portfolio.application.command.create;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class DepositCreatedResponse {

    private final UUID portfolioId;
    private final UUID balanceId;
    private final UUID userId;
    private final Long paidAmount;
    private final LocalDateTime issuedAt;
}
