package shop.shportfolio.marketdata.insight.application.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import shop.shportfolio.marketdata.insight.domain.valueobject.PeriodType;
import shop.shportfolio.marketdata.insight.domain.valueobject.PriceTrend;
import shop.shportfolio.marketdata.insight.domain.valueobject.Signal;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class AiAnalysisResponseDto {
    private final String marketId;
    private final LocalDateTime analysisTime;
    private final BigDecimal momentumScore;
    private final LocalDateTime periodEnd;
    private final LocalDateTime periodStart;
    private final PeriodType periodType;
    private final PriceTrend priceTrend;
    private final Signal signal;
    private final String summaryComment;

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
                ", summaryComment='" + summaryComment + '\'' +
                '}';
    }
}
