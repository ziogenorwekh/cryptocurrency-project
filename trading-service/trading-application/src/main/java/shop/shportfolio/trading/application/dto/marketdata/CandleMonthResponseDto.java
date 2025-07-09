package shop.shportfolio.trading.application.dto.marketdata;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CandleMonthResponseDto {
    private final String market;                     // 마켓명
    private final String candleDateTimeUtc;         // 캔들 기준 시각 (UTC 기준) yyyy-MM-dd'T'HH:mm:ss
    private final String candleDateTimeKst;         // 캔들 기준 시각 (KST 기준) yyyy-MM-dd'T'HH:mm:ss
    private final Double openingPrice;              // 시가
    private final Double highPrice;                 // 고가
    private final Double lowPrice;                  // 저가
    private final Double tradePrice;                // 종가
    private final Long timestamp;                   // 캔들 종료 시각 (KST 기준)
    private final Double candleAccTradePrice;      // 누적 거래 금액
    private final Double candleAccTradeVolume;     // 누적 거래량
    private final String firstDayOfPeriod;         // 캔들 기간의 가장 첫 날
}
