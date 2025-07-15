package shop.shportfolio.trading.application.dto.marketdata.trade;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradeTickResponseDto {

    /** 마켓 구분 코드 */
    private String market;

    /** 체결 일자(UTC 기준) yyyy-MM-dd */
    private String tradeDateUtc;

    /** 체결 시각(UTC 기준) HH:mm:ss */
    private String tradeTimeUtc;

    /** 체결 타임스탬프 */
    private Long timestamp;

    /** 체결 가격 */
    private Double tradePrice;

    /** 체결량 */
    private Double tradeVolume;

    /** 전일 종가(UTC 0시 기준) */
    private Double prevClosingPrice;

    /** 변화량 */
    private Double changePrice;

    /** 매도/매수 */
    private String askBid;

    /** 체결 번호(Unique) */
    private Long sequentialId;
}
