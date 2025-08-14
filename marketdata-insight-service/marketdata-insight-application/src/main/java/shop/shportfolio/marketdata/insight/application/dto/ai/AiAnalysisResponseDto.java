package shop.shportfolio.marketdata.insight.application.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Getter;
import shop.shportfolio.marketdata.insight.domain.valueobject.PeriodType;
import shop.shportfolio.marketdata.insight.domain.valueobject.PriceTrend;
import shop.shportfolio.marketdata.insight.domain.valueobject.Signal;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
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
}
