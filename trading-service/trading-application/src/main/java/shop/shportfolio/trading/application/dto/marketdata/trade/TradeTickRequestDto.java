package shop.shportfolio.trading.application.dto.marketdata.trade;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradeTickRequestDto {
    /** 마켓 코드 (ex. KRW-BTC) */
    private String market;

    /** 마지막 체결 시각 [HHmmss 또는 HH:mm:ss]. 비우면 최신 데이터 */
    private String to;

    /** 체결 개수 (기본값: 1) */
    private Integer count;

    /** 페이지네이션 커서 (sequentialId) */
    private String cursor;

    /** 최근 체결 날짜 기준 7일 이내 이전 데이터 조회 (1~7). 비우면 최신 날짜 */
    private Integer daysAgo;
}
