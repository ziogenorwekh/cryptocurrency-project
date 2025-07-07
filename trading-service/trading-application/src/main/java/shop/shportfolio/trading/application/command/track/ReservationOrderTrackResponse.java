package shop.shportfolio.trading.application.command.track;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class ReservationOrderTrackResponse {

    private final String orderId;
    private final UUID userId;
    private final String triggerType;
    private final BigDecimal targetPrice;
    private final LocalDateTime expireAt;
    private final Boolean isRepeatable;
    private final LocalDateTime scheduledTime;
    private final BigDecimal quantity;
}
