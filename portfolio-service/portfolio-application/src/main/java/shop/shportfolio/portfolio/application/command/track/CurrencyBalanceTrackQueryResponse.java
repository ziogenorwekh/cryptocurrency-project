package shop.shportfolio.portfolio.application.command.track;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class CurrencyBalanceTrackQueryResponse {
    private final UUID balanceId;
    private final Long amount;
    private final LocalDateTime updatedAt;
}
