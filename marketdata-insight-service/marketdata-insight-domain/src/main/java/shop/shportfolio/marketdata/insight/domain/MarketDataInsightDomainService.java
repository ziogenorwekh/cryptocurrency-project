package shop.shportfolio.marketdata.insight.domain;

import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.marketdata.insight.domain.entity.AIAnalysisResult;
import shop.shportfolio.marketdata.insight.domain.entity.MarketItem;
import shop.shportfolio.marketdata.insight.domain.valueobject.*;

public interface MarketDataInsightDomainService {

    MarketItem createMarketItem(MarketId marketId, MarketStatus marketStatus,
                                MarketEnglishName marketEnglishName, MarketKoreanName marketKoreanName);

    AIAnalysisResult createAIAnalysisResult(AIAnalysisResultId aiAnalysisResultId, MarketId marketId,
                                            AnalysisTime analysisTime, PeriodEnd periodEnd,
                                            PeriodStart periodStart, MomentumScore momentumScore,
                                            PeriodType periodType, PriceTrend priceTrend, Signal signal,
                                            SummaryComment summaryComment);

    Boolean isMarketActive(MarketItem marketItem);
}
