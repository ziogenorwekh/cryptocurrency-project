package shop.shportfolio.marketdata.insight.application.dto.candle;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@AllArgsConstructor
public class CandleMinuteResponseDto {

    private final String marketId;
    private final String candleDateTimeUTC;
    private final String candleDateTimeKST;
    private final Double openingPrice;
    private final Double highPrice;
    private final Double lowPrice;
    private final Double tradePrice;
    private final Long timestamp;
    private final Double candleAccTradePrice;
    private final Double candleAccTradeVolume;
    private final Integer unit;
}
