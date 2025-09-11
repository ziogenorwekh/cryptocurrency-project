package shop.shportfolio.trading.application.command.track.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class TickerTrackResponse {

    private final String market;                    // 종목 구분 코드
    private final String tradeDate;                // 최근 거래 일자(UTC) yyyyMMdd
    private final String tradeTime;                // 최근 거래 시각(UTC) HHmmss
    private final String tradeDateKst;            // 최근 거래 일자(KST) yyyyMMdd
    private final String tradeTimeKst;            // 최근 거래 시각(KST) HHmmss
    private final Long tradeTimestamp;            // 최근 거래 일시(UTC) Unix Timestamp
    private final BigDecimal openingPrice;            // 시가
    private final BigDecimal highPrice;               // 고가
    private final BigDecimal lowPrice;                // 저가
    private final BigDecimal tradePrice;              // 종가(현재가)
    private final BigDecimal prevClosingPrice;       // 전일 종가(KST 0시 기준)
    private final String change;                 // EVEN / RISE / FALL
    private final BigDecimal changePrice;            // 변화액 절대값
    private final BigDecimal changeRate;             // 변화율 절대값
    private final BigDecimal signedChangePrice;     // 부호 있는 변화액
    private final BigDecimal signedChangeRate;      // 부호 있는 변화율
    private final BigDecimal tradeVolume;           // 최근 거래량
    private final BigDecimal accTradePrice;         // 누적 거래대금(KST 0시 기준)
    private final BigDecimal accTradePrice24h;      // 24시간 누적 거래대금
    private final BigDecimal accTradeVolume;        // 누적 거래량(KST 0시 기준)
    private final BigDecimal accTradeVolume24h;     // 24시간 누적 거래량
    private final BigDecimal highest52WeekPrice;   // 52주 신고가
    private final String highest52WeekDate;    // 52주 신고가 달성일 yyyy-MM-dd
    private final BigDecimal lowest52WeekPrice;    // 52주 신저가
    private final String lowest52WeekDate;     // 52주 신저가 달성일 yyyy-MM-dd
    private final Long timestamp;               // 타임스탬프
}
