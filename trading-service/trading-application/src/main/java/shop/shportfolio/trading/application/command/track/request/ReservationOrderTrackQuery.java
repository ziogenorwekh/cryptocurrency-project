package shop.shportfolio.trading.application.command.track.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationOrderTrackQuery {
    @NotBlank(message = "주문 ID는 필수입니다.")
    private String orderId;

    @NotBlank(message = "유저 ID는 필수입니다.")
    private UUID userId;
}
