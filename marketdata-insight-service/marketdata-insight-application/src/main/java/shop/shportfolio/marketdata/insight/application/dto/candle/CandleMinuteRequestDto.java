package shop.shportfolio.marketdata.insight.application.dto.candle;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CandleMinuteRequestDto {
    private final Integer unit;
    private final String market;
    private final String to;
    private final Integer count;
}
