package shop.shportfolio.trading.application.command.track.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CandleTrackQuery {
    @NotNull(message = "market 아이디는 필수입니다.")
    private String marketId;
    @NotNull(message = "마지막 캔들 시각은 필수입니다.")
    private String to;
    @NotNull(message = "캔들의 개수는 필수입니다.")
    private Integer count;
}
