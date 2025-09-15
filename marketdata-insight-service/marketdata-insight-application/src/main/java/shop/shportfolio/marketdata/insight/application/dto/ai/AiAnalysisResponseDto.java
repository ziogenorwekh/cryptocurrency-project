package shop.shportfolio.marketdata.insight.application.dto.ai;

import lombok.*;
import shop.shportfolio.marketdata.insight.domain.valueobject.PeriodType;
import shop.shportfolio.marketdata.insight.domain.valueobject.PriceTrend;
import shop.shportfolio.marketdata.insight.domain.valueobject.Signal;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiAnalysisResponseDto {
    private String marketId;
    @Setter
    private OffsetDateTime analysisTime;
    private BigDecimal momentumScore;
    private OffsetDateTime periodEnd;
    private OffsetDateTime periodStart;
    @Setter
    private PeriodType periodType;
    @Setter
    private PriceTrend priceTrend;
    @Setter
    private Signal signal;
    private String summaryCommentEng;
    private String summaryCommentKor;

    @Override
    public String toString() {
        return "AiAnalysisResponseDto{" +
                "marketId='" + marketId + '\'' +
                ", analysisTime=" + analysisTime +
                ", momentumScore=" + momentumScore +
                ", periodEnd=" + periodEnd +
                ", periodStart=" + periodStart +
                ", periodType=" + periodType +
                ", priceTrend=" + priceTrend +
                ", signal=" + signal +
                ", summaryCommentEng='" + summaryCommentEng + '\'' +
                ", summaryCommentKor='" + summaryCommentKor + '\'' +
                '}';
    }
}
