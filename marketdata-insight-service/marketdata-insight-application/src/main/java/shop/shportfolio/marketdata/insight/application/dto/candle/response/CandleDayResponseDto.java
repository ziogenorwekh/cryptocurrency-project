package shop.shportfolio.marketdata.insight.application.dto.candle.response;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class CandleDayResponseDto {

    private final String market;
    private final String candleDateTimeUtc;
    private final String candleDateTimeKst;
    private final BigDecimal openingPrice;
    private final BigDecimal highPrice;
    private final BigDecimal lowPrice;
    private final BigDecimal tradePrice;
    private final BigDecimal candleAccTradePrice;
    private final BigDecimal candleAccTradeVolume;
    private final BigDecimal prevClosingPrice;
    private final BigDecimal changePrice;
    private final BigDecimal changeRate;

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
