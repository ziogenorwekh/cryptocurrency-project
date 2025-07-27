package shop.shportfolio.portfolio.application.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class PortfolioCreatedResponse {

    private final UUID portfolioId;
    private final UUID userId;
    private final BigDecimal amount;
    private final LocalDateTime createdAt;
}
