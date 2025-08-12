package shop.shportfolio.trading.application.command.track.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CandleMinuteTrackQuery {
    // 분 단위 1, 5, 3
    @NotNull(message = "분 단위는 필수입니다.")
    private Integer unit;
    // 마켓 정보 예) BTC-KRW
    @NotNull(message = "market 아이디는 필수입니다.")
    private String market;
    // 마지막 캔들 시각
//    @NotNull(message = "마지막 캔들 시각은 필수입니다.")
    private String to;

    // 캔들 개수
//    @NotNull(message = "캔들의 개수는 필수입니다.")
    private Integer count;
}
