package shop.shportfolio.trading.application.command.track;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CandleMinuteTrackQuery {
    private Integer unit;
    private String market;
    private String to;
    private Integer count;
}
