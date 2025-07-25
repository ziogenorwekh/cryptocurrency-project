package shop.shportfolio.portfolio.application.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class TotalAssetValueTrackQueryResponse {
    private final UUID portfolioId;
    private final UUID userId;
    private final BigDecimal totalAssetValue;
    private final LocalDateTime updatedAt;
}
