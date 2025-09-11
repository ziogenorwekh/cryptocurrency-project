package shop.shportfolio.marketdata.insight.application.command.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class CandleMinuteTrackResponse {
    private final String marketId;
    private final String candleDateTimeKST;
    private final BigDecimal openingPrice;
    private final BigDecimal highPrice;
    private final BigDecimal lowPrice;
    private final BigDecimal tradePrice;
    private final Long timestamp;
    private final BigDecimal candleAccTradePrice;
    private final BigDecimal candleAccTradeVolume;
    private final Integer unit;
}
