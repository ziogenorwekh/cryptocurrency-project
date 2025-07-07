package shop.shportfolio.trading.application.command.update;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class CancelReservationOrderCommand {
    @NotBlank(message = "주문 ID는 필수입니다.")
    private String orderId;

    @NotNull(message = "사용자 ID는 필수입니다.")
    private UUID userId;

    @NotBlank(message = "마켓 ID는 필수입니다.")
    private String marketId;
}
