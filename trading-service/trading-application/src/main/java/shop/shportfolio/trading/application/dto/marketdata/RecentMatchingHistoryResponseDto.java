package shop.shportfolio.trading.application.dto.marketdata;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RecentMatchingHistoryResponseDto {

    private final String market;                 // 마켓 구분 코드
    private final String tradeDateUtc;          // 체결 일자 (UTC 기준) yyyy-MM-dd
    private final String tradeTimeUtc;          // 체결 시각 (UTC 기준) HH:mm:ss
    private final Long timestamp;               // 체결 타임스탬프
    private final Double tradePrice;            // 체결 가격
    private final Double tradeVolume;           // 체결량
    private final Double prevClosingPrice;      // 전일 종가 (UTC 0시 기준)
    private final Double changePrice;           // 변화량
    private final String askBid;                // 매도/매수
    private final Long sequentialId;            // 체결 번호 (Unique)
}
