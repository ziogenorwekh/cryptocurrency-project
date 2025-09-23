package shop.shportfolio.marketdata.insight.application.dto.candle.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class CandleMinuteResponseDto {

    private final String marketId;
    private final String candleDateTimeUtc;
    private final String candleDateTimeKst;
    private final BigDecimal openingPrice;
    private final BigDecimal highPrice;
    private final BigDecimal lowPrice;
    private final BigDecimal tradePrice;
    private final Long timestamp;
    private final BigDecimal candleAccTradePrice;
    private final BigDecimal candleAccTradeVolume;
    private final Integer unit;

    @Override
    public String toString() {
        return "{" +
                "\"marketId\":\"" + marketId + '\"' +
                ", \"candleDateTimeUTC\":\"" + candleDateTimeUtc + '\"' +
                ", \"candleDateTimeKST\":\"" + candleDateTimeKst + '\"' +
                ", \"openingPrice\":" + openingPrice +
                ", \"highPrice\":" + highPrice +
                ", \"lowPrice\":" + lowPrice +
                ", \"tradePrice\":" + tradePrice +
                ", \"timestamp\":" + timestamp +
                ", \"candleAccTradePrice\":" + candleAccTradePrice +
                ", \"candleAccTradeVolume\":" + candleAccTradeVolume +
                ", \"unit\":" + unit +
                '}';
    }

}
