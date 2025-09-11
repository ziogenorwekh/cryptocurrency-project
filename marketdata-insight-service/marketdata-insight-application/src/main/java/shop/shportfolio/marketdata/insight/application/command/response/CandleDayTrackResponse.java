package shop.shportfolio.marketdata.insight.application.command.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class CandleDayTrackResponse {
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
    private final Double changePrice;
    private final Double changeRate;
}
