package shop.shportfolio.portfolio.application.command.track;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class PortfolioTrackQueryResponse {
    private final UUID portfolioId;
    private final UUID userId;
    private final LocalDateTime updatedAt;
}
