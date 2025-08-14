package shop.shportfolio.marketdata.insight.application.ports.input;

import shop.shportfolio.marketdata.insight.application.command.request.AiAnalysisTrackQuery;
import shop.shportfolio.marketdata.insight.domain.entity.AIAnalysisResult;

public interface AIAnalysisUseCase {

    AIAnalysisResult trackAiAnalysis(AiAnalysisTrackQuery query);
}
