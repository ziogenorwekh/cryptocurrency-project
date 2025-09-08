package shop.shportfolio.trading.application.dto.marketdata.trade;

import lombok.*;

import java.text.DecimalFormat;

@Getter
@Builder
@AllArgsConstructor
public class TradeTickResponseDto {

    /** 마켓 구분 코드 */
    private final String market;

    /** 체결 일자(UTC 기준) yyyy-MM-dd */
    private final String tradeDateUtc;

    /** 체결 시각(UTC 기준) HH:mm:ss */
    private final String tradeTimeUtc;

    /** 체결 타임스탬프 */
    private final Long timestamp;

    /** 체결 가격 */
    private final Double tradePrice;

    /** 체결량 */
    private final Double tradeVolume;

    /** 전일 종가(UTC 0시 기준) */
    private final Double prevClosingPrice;

    /** 변화량 */
    private final Double changePrice;

    /** 매도/매수 */
    private final String askBid;

    /** 체결 번호(Unique) */
    private final Long sequentialId;

    @Override
    public String toString() {
        DecimalFormat df = new DecimalFormat("#");
        return "TradeTickResponseDto{" +
                "market='" + market + '\'' +
                ", tradeDateUtc='" + tradeDateUtc + '\'' +
                ", tradeTimeUtc='" + tradeTimeUtc + '\'' +
                ", timestamp=" + timestamp +
                ", tradePrice=" + df.format(tradePrice) +
                ", tradeVolume=" + df.format(tradeVolume) +
                ", prevClosingPrice=" + df.format(prevClosingPrice) +
                ", changePrice=" + df.format(changePrice) +
                ", askBid='" + askBid + '\'' +
                ", sequentialId=" + sequentialId +
                '}';
    }
}
