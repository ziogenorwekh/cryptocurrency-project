package shop.shportfolio.trading.application.dto.marketdata;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CandleTrackQuery {
    private String market;
    private String to;
    private Integer count;
}
