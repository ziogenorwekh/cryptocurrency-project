package shop.shportfolio.portfolio.application.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class DepositCreatedResponse {

    private final UUID portfolioId;
    private final UUID userId;
    private final Long paidAmount;
    private final LocalDate issuedAt;
}
