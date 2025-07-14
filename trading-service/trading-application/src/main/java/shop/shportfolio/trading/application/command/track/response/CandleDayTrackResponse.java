package shop.shportfolio.trading.application.command.track.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CandleDayTrackResponse {
    private final String market;
    private final String candleDateTimeUtc;
    private final String candleDateTimeKst;
    private final Double openingPrice;
    private final Double highPrice;
    private final Double lowPrice;
    private final Double tradePrice;
    private final Double candleAccTradePrice;
    private final Double candleAccTradeVolume;
    private final Double prevClosingPrice;
    private final Double changePrice;
    private final Double changeRate;
}
