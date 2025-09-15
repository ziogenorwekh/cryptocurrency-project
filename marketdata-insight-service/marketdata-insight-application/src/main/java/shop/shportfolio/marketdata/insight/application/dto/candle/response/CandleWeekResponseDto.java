package shop.shportfolio.marketdata.insight.application.dto.candle.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class CandleWeekResponseDto {
    private final String market;                       // 마켓명
    private final String candleDateTimeUtc;           // 캔들 기준 시각 (UTC)
    private final String candleDateTimeKst;           // 캔들 기준 시각 (KST)
    private final BigDecimal openingPrice;                // 시가
    private final BigDecimal highPrice;                   // 고가
    private final BigDecimal lowPrice;                    // 저가
    private final BigDecimal tradePrice;                  // 종가
    private final Long timestamp;                     // 캔들 종료 시각 (KST)
    private final BigDecimal candleAccTradePrice;         // 누적 거래 금액
    private final BigDecimal candleAccTradeVolume;        // 누적 거래량
    private final String firstDayOfPeriod;            // 캔들 기간의 가장 첫 날

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
                ", \"timestamp\":" + timestamp +
                ", \"candleAccTradePrice\":" + candleAccTradePrice +
                ", \"candleAccTradeVolume\":" + candleAccTradeVolume +
                ", \"firstDayOfPeriod\":\"" + firstDayOfPeriod + '\"' +
                '}';
    }
}
