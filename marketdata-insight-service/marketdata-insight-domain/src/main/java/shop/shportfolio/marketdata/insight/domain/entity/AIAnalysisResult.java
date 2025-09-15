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
    private SummaryComment summaryCommentEng;
    private SummaryComment summaryCommentKor;


    @Builder
    public AIAnalysisResult(AIAnalysisResultId aiAnalysisResultId, MarketId marketId, AnalysisTime analysisTime, PeriodEnd periodEnd,
                            PeriodStart periodStart, MomentumScore momentumScore, PeriodType periodType,
                            PriceTrend priceTrend, Signal signal, SummaryComment summaryCommentEng,
                            SummaryComment summaryCommentKor) {
        this.summaryCommentKor = summaryCommentKor;
        setId(aiAnalysisResultId);
        this.marketId = marketId;
        this.analysisTime = analysisTime;
        this.periodEnd = periodEnd;
        this.periodStart = periodStart;
        this.momentumScore = momentumScore;
        this.periodType = periodType;
        this.priceTrend = priceTrend;
        this.signal = signal;
        this.summaryCommentEng = summaryCommentEng;
    }

    public static AIAnalysisResult createAIAnalysisResult(AIAnalysisResultId aiAnalysisResultId, MarketId marketId, AnalysisTime analysisTime, PeriodEnd periodEnd,
                                                          PeriodStart periodStart, MomentumScore momentumScore, PeriodType periodType,
                                                          PriceTrend priceTrend, Signal signal, SummaryComment summaryCommentEng,SummaryComment summaryCommentKor) {
        AIAnalysisResult aiAnalysisResult = new AIAnalysisResult(aiAnalysisResultId,marketId, analysisTime,
                periodEnd, periodStart, momentumScore, periodType, priceTrend, signal, summaryCommentEng, summaryCommentKor);
        return aiAnalysisResult;
    }

    @Override
    public String toString() {
        return "AIAnalysisResult{" +
                "marketId=" + marketId.getValue() +
                ", analysisTime=" + analysisTime.getValue() +
                ", momentumScore=" + momentumScore.toString() +
                ", periodEnd=" + periodEnd.getValue() +
                ", periodStart=" + periodStart.getValue() +
                ", periodType=" + periodType.name() +
                ", priceTrend=" + priceTrend.name() +
                ", signal=" + signal.name() +
                ", summaryCommentEng=" + summaryCommentEng +
                ", summaryCommentKor=" + summaryCommentKor +
                '}';
    }
}
