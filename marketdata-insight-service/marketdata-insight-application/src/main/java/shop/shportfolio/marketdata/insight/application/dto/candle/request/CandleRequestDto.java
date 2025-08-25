package shop.shportfolio.marketdata.insight.application.dto.candle.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CandleRequestDto {
    private final String market;
    private final String to;
    private final Integer count;
}
