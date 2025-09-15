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

    public AIAnalysisResultEntity aiAnalysisResultToAIAnalysisResultEntity(AIAnalysisResult aiAnalysisResult,
                                                                           MarketItemEntity entity) {
        return AIAnalysisResultEntity.builder()
                .aiAnalysisResultId(aiAnalysisResult.getId().getValue())
                .marketItemEntity(entity)
                .analysisTime(aiAnalysisResult.getAnalysisTime().getValue()) // OffsetDateTime 그대로
                .momentumScore(aiAnalysisResult.getMomentumScore().getValue())
                .periodStart(aiAnalysisResult.getPeriodStart().getValue())
                .periodEnd(aiAnalysisResult.getPeriodEnd().getValue())
                .periodType(aiAnalysisResult.getPeriodType())
                .priceTrend(aiAnalysisResult.getPriceTrend())
                .signal(aiAnalysisResult.getSignal())
                .summaryCommentEng(aiAnalysisResult.getSummaryCommentEng().getValue())
                .summaryCommentKor(aiAnalysisResult.getSummaryCommentKor().getValue())
                .build();
    }

    public AIAnalysisResult aiAnalysisResultEntityToAIAnalysisResult(AIAnalysisResultEntity entity) {
        return AIAnalysisResult.builder()
                .aiAnalysisResultId(new AIAnalysisResultId(entity.getAiAnalysisResultId()))
                .marketId(new MarketId(entity.getMarketItemEntity().getMarketId()))
                .analysisTime(new AnalysisTime(entity.getAnalysisTime()))
                .periodEnd(new PeriodEnd(entity.getPeriodEnd()))
                .periodStart(new PeriodStart(entity.getPeriodStart()))
                .momentumScore(new MomentumScore(entity.getMomentumScore()))
                .periodType(entity.getPeriodType())
                .priceTrend(entity.getPriceTrend())
                .signal(entity.getSignal())
                .summaryCommentEng(new SummaryComment(entity.getSummaryCommentEng()))
                .summaryCommentKor(new SummaryComment(entity.getSummaryCommentKor()))
                .build();
    }
}
