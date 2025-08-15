package shop.shportfolio.marketdata.insight.domain.entity;

import lombok.Builder;
import lombok.Getter;
import shop.shportfolio.common.domain.entity.AggregateRoot;
import shop.shportfolio.common.domain.valueobject.MarketId;
import shop.shportfolio.marketdata.insight.domain.valueobject.*;

@Getter
public class AIAnalysisResult extends AggregateRoot<AIAnalysisResultId> {

    private final MarketId marketId;
    private final AnalysisTime analysisTime;
    private MomentumScore momentumScore;
    private final PeriodEnd periodEnd;
    private final PeriodStart periodStart;
    private PeriodType periodType;
    private PriceTrend priceTrend;
    private Signal signal;
    private SummaryComment summaryComment;

    @Builder
    public AIAnalysisResult(AIAnalysisResultId aiAnalysisResultId, MarketId marketId, AnalysisTime analysisTime, PeriodEnd periodEnd,
                            PeriodStart periodStart, MomentumScore momentumScore, PeriodType periodType,
                            PriceTrend priceTrend, Signal signal, SummaryComment summaryComment) {
        setId(aiAnalysisResultId);
        this.marketId = marketId;
        this.analysisTime = analysisTime;
        this.periodEnd = periodEnd;
        this.periodStart = periodStart;
        this.momentumScore = momentumScore;
        this.periodType = periodType;
        this.priceTrend = priceTrend;
        this.signal = signal;
        this.summaryComment = summaryComment;
    }

    public static AIAnalysisResult createAIAnalysisResult(AIAnalysisResultId aiAnalysisResultId, MarketId marketId, AnalysisTime analysisTime, PeriodEnd periodEnd,
                                                          PeriodStart periodStart, MomentumScore momentumScore, PeriodType periodType,
                                                          PriceTrend priceTrend, Signal signal, SummaryComment summaryComment) {
        AIAnalysisResult aiAnalysisResult = new AIAnalysisResult(aiAnalysisResultId,marketId, analysisTime,
                periodEnd, periodStart, momentumScore, periodType, priceTrend, signal, summaryComment);
        return aiAnalysisResult;
    }
}
