package shop.shportfolio.marketdata.insight.application.ports.input.usecase.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.marketdata.insight.application.command.request.AiAnalysisTrackQuery;
import shop.shportfolio.marketdata.insight.application.handler.AIAnalysisHandler;
import shop.shportfolio.marketdata.insight.application.ports.input.usecase.AIAnalysisTrackUseCase;
import shop.shportfolio.marketdata.insight.domain.entity.AIAnalysisResult;

@Component
public class AIAnalysisTrackUseCaseImpl implements AIAnalysisTrackUseCase {

    private final AIAnalysisHandler aiAnalysisHandler;

    @Autowired
    public AIAnalysisTrackUseCaseImpl(AIAnalysisHandler aiAnalysisHandler) {
        this.aiAnalysisHandler = aiAnalysisHandler;
    }

    @Override
    public AIAnalysisResult trackAiAnalysis(AiAnalysisTrackQuery query) {
        return aiAnalysisHandler.trackAiAnalysis(query.getMarketId(), query.getPeriodType());

    }
}
