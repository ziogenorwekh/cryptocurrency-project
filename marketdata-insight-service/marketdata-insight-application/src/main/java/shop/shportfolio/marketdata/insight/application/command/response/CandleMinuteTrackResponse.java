package shop.shportfolio.marketdata.insight.application.command.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CandleMinuteTrackResponse {
    private final String marketId;
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
