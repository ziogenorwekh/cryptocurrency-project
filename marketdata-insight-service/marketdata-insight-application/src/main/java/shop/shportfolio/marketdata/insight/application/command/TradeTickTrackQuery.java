package shop.shportfolio.marketdata.insight.application.command;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TradeTickTrackQuery {

    @NotNull(message = "마켓 아이디는 null이어서는 안됩니다.")
    private String marketId;

//    @Pattern(
//            regexp = "^(\\d{6}|\\d{2}:\\d{2}:\\d{2})$",
//            message = "마지막 체결 시각은 HHmmss 또는 HH:mm:ss 형식이어야 합니다."
//    )
    /** 마지막 체결 시각 [HHmmss 또는 HH:mm:ss]. 비우면 최신 데이터 */
    private String to;

//    @NotNull(message = "체결 개수는 필수입니다.")
    /** 체결 개수 (기본값: 1) */
    private Integer count;

    /** 페이지네이션 커서 (sequentialId) */
    private String cursor;

//    @Min(value = 1, message = "daysAgo는 1 이상이어야 합니다.")
//    @Max(value = 7, message = "daysAgo는 7 이하여야 합니다.")
    /** 최근 체결 날짜 기준 7일 이내 이전 데이터 조회 (1~7). 비우면 최신 날짜 */
    private Integer daysAgo;
}
