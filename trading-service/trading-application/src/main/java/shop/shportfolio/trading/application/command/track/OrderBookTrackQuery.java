package shop.shportfolio.trading.application.command.track;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderBookTrackQuery {

    @NotBlank(message = "마켓 ID는 필수입니다.")
    private String marketId;
}
