package shop.shportfolio.trading.application.dto.marketdata.candle;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CandleDayResponseDto {

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
