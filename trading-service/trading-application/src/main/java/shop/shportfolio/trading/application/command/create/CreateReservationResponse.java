package shop.shportfolio.trading.application.command.create;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class CreateReservationResponse {

    private final String orderId;
    private final String status;
    private final LocalDateTime scheduledTime;
    private final LocalDateTime expireAt;
    private final BigDecimal targetPrice;

}
