package shop.shportfolio.marketdata.insight.infrastructure.database.jpa.mapper;

import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.valueobject.MarketEnglishName;
import shop.shportfolio.common.domain.valueobject.MarketId;
import shop.shportfolio.common.domain.valueobject.MarketKoreanName;
import shop.shportfolio.marketdata.insight.domain.entity.AIAnalysisResult;
import shop.shportfolio.marketdata.insight.domain.entity.MarketItem;
import shop.shportfolio.marketdata.insight.domain.valueobject.*;
import shop.shportfolio.marketdata.insight.infrastructure.database.jpa.entity.AIAnalysisResultEntity;
import shop.shportfolio.marketdata.insight.infrastructure.database.jpa.entity.MarketItemEntity;

@Component
public class MarketDataInsightDataAccessMapper {

    public MarketItem marketItemEntityToMarketItem(MarketItemEntity marketItemEntity) {
        return MarketItem.builder()
                .marketId(new MarketId(marketItemEntity.getMarketId()))
                .marketKoreanName(new MarketKoreanName(marketItemEntity.getMarketKoreanName()))
                .marketEnglishName(new MarketEnglishName(marketItemEntity.getMarketEnglishName()))
                .marketStatus(marketItemEntity.getMarketStatus())
                .build();
    }

    public MarketItemEntity marketItemToMarketItemEntity(MarketItem marketItem) {
        return MarketItemEntity.builder()
                .marketId(marketItem.getId().getValue())
                .marketKoreanName(marketItem.getMarketKoreanName().getValue())
                .marketEnglishName(marketItem.getMarketEnglishName().getValue())
                .marketStatus(marketItem.getMarketStatus())
                .build();
    }

    public AIAnalysisResult aiAnalysisResultEntityToAIAnalysisResult(AIAnalysisResultEntity analysisResultEntity) {
        return AIAnalysisResult.builder()
                .aiAnalysisResultId(new AIAnalysisResultId(analysisResultEntity.getAiAnalysisResultId()))
                .marketId(new MarketId(analysisResultEntity.getMarketItemEntity().getMarketId()))
                .analysisTime(new AnalysisTime(analysisResultEntity.getAnalysisTime()))
                .periodEnd(new PeriodEnd(analysisResultEntity.getPeriodEnd()))
                .periodStart(new PeriodStart(analysisResultEntity.getPeriodStart()))
                .momentumScore(new MomentumScore(analysisResultEntity.getMomentumScore()))
                .periodType(analysisResultEntity.getPeriodType())
                .priceTrend(analysisResultEntity.getPriceTrend())
                .signal(analysisResultEntity.getSignal())
                .summaryComment(new SummaryComment(analysisResultEntity.getSummaryComment()))
                .build();
    }

    public AIAnalysisResultEntity aiAnalysisResultToAIAnalysisResultEntity(AIAnalysisResult aiAnalysisResult) {
        return AIAnalysisResultEntity.builder()


                .build();
    }
}
