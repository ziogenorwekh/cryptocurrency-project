package shop.shportfolio.trading.application.command.create;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateReservationOrderCommand {
    @NotNull(message = "사용자 ID는 필수입니다.")
    private UUID userId;

    @NotBlank(message = "마켓 ID는 필수입니다.")
    private String marketId;

    @NotBlank(message = "주문 방향은 필수입니다.")
    @Pattern(regexp = "BUY|SELL", message = "주문 방향은 BUY 또는 SELL 이어야 합니다.")
    private String orderSide;

    @NotNull(message = "수량은 필수입니다.")
    @DecimalMin(value = "0.0001", message = "수량은 0보다 커야 합니다.")
    private BigDecimal quantity;

    @NotBlank(message = "주문 유형은 필수입니다.")
    @Pattern(regexp = "LIMIT|MARKET|RESERVATION", message = "주문 유형은 LIMIT 또는 MARKET 이어야 합니다.")
    private String orderType;

    @NotBlank(message = "트리거 방식은 필수입니다.")
    @Pattern(regexp = "ABOVE|BELOW",message = "트리거 방식은 ABOVE, BELOW 이어야 합니다.")
    private String TriggerType;

    @NotNull(message = "타겟 가격은 필수입니다.")
    @DecimalMin(value = "0.0001", message = "가격은 0보다 커야 합니다.")
    private BigDecimal targetPrice;

    @NotNull(message = "예약 실행 시간은 필수입니다.")
    @FutureOrPresent(message = "예약 실행 시간은 현재 시간 이후여야 합니다.")
    private LocalDateTime scheduledTime;

    @NotNull(message = "만료 시간은 필수입니다.")
    @FutureOrPresent(message = "만료 시간은 현재 시간 이후여야 합니다.")
    private LocalDateTime expireAt;

    @NotNull(message = "반복 여부는 필수입니다.")
    private Boolean isRepeatable;
}
