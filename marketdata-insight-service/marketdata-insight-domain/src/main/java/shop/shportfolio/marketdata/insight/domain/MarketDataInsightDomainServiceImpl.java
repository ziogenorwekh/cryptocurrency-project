package shop.shportfolio.marketdata.insight.domain;

import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.marketdata.insight.domain.entity.AIAnalysisResult;
import shop.shportfolio.marketdata.insight.domain.entity.MarketItem;
import shop.shportfolio.marketdata.insight.domain.valueobject.*;

public class MarketDataInsightDomainServiceImpl implements MarketDataInsightDomainService {

    @Override
    public MarketItem createMarketItem(MarketId marketId, MarketStatus marketStatus,
                                       MarketEnglishName marketEnglishName, MarketKoreanName marketKoreanName) {
        MarketItem marketItem = MarketItem.createMarketItem(marketId, marketStatus,
                marketEnglishName, marketKoreanName);
        return marketItem;
    }

    @Override
    public AIAnalysisResult createAIAnalysisResult(AIAnalysisResultId aiAnalysisResultId, MarketId marketId,
                                                   AnalysisTime analysisTime, PeriodEnd periodEnd,
                                                   PeriodStart periodStart, MomentumScore momentumScore,
                                                   PeriodType periodType, PriceTrend priceTrend, Signal signal,
                                                   SummaryComment summaryCommentEng,SummaryComment summaryCommentKor) {
        AIAnalysisResult aiAnalysisResult = AIAnalysisResult.createAIAnalysisResult(aiAnalysisResultId,
                marketId, analysisTime, periodEnd, periodStart,
                momentumScore, periodType, priceTrend, signal, summaryCommentEng, summaryCommentKor);
        return aiAnalysisResult;
    }

    @Override
    public Boolean isMarketActive(MarketItem marketItem) {
        return marketItem.isActive();
    }
}
