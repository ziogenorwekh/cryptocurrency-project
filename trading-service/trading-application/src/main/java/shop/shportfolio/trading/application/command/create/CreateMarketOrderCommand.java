package shop.shportfolio.trading.application.command.create;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateMarketOrderCommand {
    @NotNull(message = "사용자 ID는 필수입니다.")
    private UUID userId;

    @NotBlank(message = "마켓 ID는 필수입니다.")
    private String marketId;

    @NotBlank(message = "주문 방향은 필수입니다.")
    @Pattern(regexp = "BUY|SELL", message = "주문 방향은 BUY 또는 SELL 이어야 합니다.")
    private String orderSide;

    @NotNull(message = "수량은 필수입니다.")
    @DecimalMin(value = "0.0001", inclusive = true, message = "수량은 0보다 커야 합니다.")
    private BigDecimal quantity;

    @NotBlank(message = "주문 유형은 필수입니다.")
    @Pattern(regexp = "LIMIT|MARKET|RESERVATION", message = "주문 유형은 LIMIT, MARKET, RESERVATION 중 하나여야 합니다.")
    private String orderType;
}
