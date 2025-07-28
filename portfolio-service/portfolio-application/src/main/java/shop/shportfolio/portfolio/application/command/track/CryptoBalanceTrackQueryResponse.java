package shop.shportfolio.portfolio.application.command.track;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class CryptoBalanceTrackQueryResponse {
    private final UUID balanceId;
    private final UUID portfolioId;
    private final String marketId;
    private final BigDecimal quantity;
    private final BigDecimal purchasePrice;
    private final LocalDateTime updatedAt;
}
