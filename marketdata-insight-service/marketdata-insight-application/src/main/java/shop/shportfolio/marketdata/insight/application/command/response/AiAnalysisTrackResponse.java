package shop.shportfolio.marketdata.insight.application.command.response;

import lombok.Getter;
import shop.shportfolio.marketdata.insight.domain.valueobject.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class AiAnalysisTrackResponse {

    private final String marketId;
    private final LocalDateTime analysisTime;
    private final BigDecimal momentumScore;
    private final LocalDateTime periodEnd;
    private final LocalDateTime periodStart;
    private final PeriodType periodType;
    private final PriceTrend priceTrend;
    private final Signal signal;
    private final String summaryComment;

    public AiAnalysisTrackResponse(String marketId, LocalDateTime analysisTime, BigDecimal momentumScore,
                                   LocalDateTime periodEnd, LocalDateTime periodStart, PeriodType periodType,
                                   PriceTrend priceTrend, Signal signal, String summaryComment) {
        this.marketId = marketId;
        this.analysisTime = analysisTime;
        this.momentumScore = momentumScore;
        this.periodEnd = periodEnd;
        this.periodStart = periodStart;
        this.periodType = periodType;
        this.priceTrend = priceTrend;
        this.signal = signal;
        this.summaryComment = summaryComment;
    }
}
