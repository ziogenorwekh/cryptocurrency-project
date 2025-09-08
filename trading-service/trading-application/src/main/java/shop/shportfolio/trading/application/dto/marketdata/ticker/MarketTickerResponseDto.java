package shop.shportfolio.trading.application.dto.marketdata.ticker;

import lombok.*;

import java.text.DecimalFormat;

@Getter
@Builder
@AllArgsConstructor
public class MarketTickerResponseDto {
    private final String market;                    // 종목 구분 코드
    private final String tradeDate;                // 최근 거래 일자(UTC) yyyyMMdd
    private final String tradeTime;                // 최근 거래 시각(UTC) HHmmss
    private final String tradeDateKst;            // 최근 거래 일자(KST) yyyyMMdd
    private final String tradeTimeKst;            // 최근 거래 시각(KST) HHmmss
    private final Long tradeTimestamp;            // 최근 거래 일시(UTC) Unix Timestamp
    private final Double openingPrice;            // 시가
    private final Double highPrice;               // 고가
    private final Double lowPrice;                // 저가
    private final Double tradePrice;              // 종가(현재가)
    private final Double prevClosingPrice;       // 전일 종가(KST 0시 기준)
    private final String change;                 // EVEN / RISE / FALL
    private final Double changePrice;            // 변화액 절대값
    private final Double changeRate;             // 변화율 절대값
    private final Double signedChangePrice;     // 부호 있는 변화액
    private final Double signedChangeRate;      // 부호 있는 변화율
    private final Double tradeVolume;           // 최근 거래량
    private final Double accTradePrice;         // 누적 거래대금(KST 0시 기준)
    private final Double accTradePrice24h;      // 24시간 누적 거래대금
    private final Double accTradeVolume;        // 누적 거래량(KST 0시 기준)
    private final Double accTradeVolume24h;     // 24시간 누적 거래량
    private final Double highest52WeekPrice;   // 52주 신고가
    private final String highest52WeekDate;    // 52주 신고가 달성일 yyyy-MM-dd
    private final Double lowest52WeekPrice;    // 52주 신저가
    private final String lowest52WeekDate;     // 52주 신저가 달성일 yyyy-MM-dd
    private final Long timestamp;               // 타임스탬프

    @Override
    public String toString() {
        DecimalFormat df = new DecimalFormat("#");
        return "MarketTickerResponseDto{" +
                "market='" + market + '\'' +
                ", tradeDate='" + tradeDate + '\'' +
                ", tradeTime='" + tradeTime + '\'' +
                ", tradeDateKst='" + tradeDateKst + '\'' +
                ", tradeTimeKst='" + tradeTimeKst + '\'' +
                ", tradeTimestamp=" + tradeTimestamp +
                ", openingPrice=" + df.format(openingPrice) +
                ", highPrice=" + df.format(highPrice) +
                ", lowPrice=" + df.format(lowPrice) +
                ", tradePrice=" + df.format(tradePrice) +
                ", prevClosingPrice=" + df.format(prevClosingPrice) +
                ", change='" + change + '\'' +
                ", changePrice=" + df.format(changePrice) +
                ", changeRate=" + changeRate +
                ", signedChangePrice=" + signedChangePrice +
                ", signedChangeRate=" + signedChangeRate +
                ", tradeVolume=" + tradeVolume +
                ", accTradePrice=" + accTradePrice +
                ", accTradePrice24h=" + accTradePrice24h +
                ", accTradeVolume=" + accTradeVolume +
                ", accTradeVolume24h=" + accTradeVolume24h +
                ", highest52WeekPrice=" + highest52WeekPrice +
                ", highest52WeekDate='" + highest52WeekDate + '\'' +
                ", lowest52WeekPrice=" + lowest52WeekPrice +
                ", lowest52WeekDate='" + lowest52WeekDate + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
