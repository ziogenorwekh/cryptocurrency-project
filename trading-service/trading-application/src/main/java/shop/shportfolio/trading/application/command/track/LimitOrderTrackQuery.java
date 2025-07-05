package shop.shportfolio.trading.application.command.track;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LimitOrderTrackQuery {

    @NotBlank(message = "주문 ID는 필수입니다.")
    private String orderId;
}
