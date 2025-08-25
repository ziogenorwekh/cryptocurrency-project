package shop.shportfolio.marketdata.insight.application.dto.candle.response;


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

    @Override
    public String toString() {
        return "{" +
                "\"market\":\"" + market + '\"' +
                ", \"candleDateTimeUtc\":\"" + candleDateTimeUtc + '\"' +
                ", \"candleDateTimeKst\":\"" + candleDateTimeKst + '\"' +
                ", \"openingPrice\":" + openingPrice +
                ", \"highPrice\":" + highPrice +
                ", \"lowPrice\":" + lowPrice +
                ", \"tradePrice\":" + tradePrice +
                ", \"candleAccTradePrice\":" + candleAccTradePrice +
                ", \"candleAccTradeVolume\":" + candleAccTradeVolume +
                ", \"prevClosingPrice\":" + prevClosingPrice +
                ", \"changePrice\":" + changePrice +
                ", \"changeRate\":" + changeRate +
                '}';
    }
}
