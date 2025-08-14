package shop.shportfolio.marketdata.insight.application.ports.input;

import jakarta.validation.Valid;
import shop.shportfolio.marketdata.insight.application.command.request.AiAnalysisTrackQuery;
import shop.shportfolio.marketdata.insight.application.command.response.AiAnalysisTrackResponse;

public interface InsightApplicationService {
    AiAnalysisTrackResponse trackAiAnalysis(@Valid AiAnalysisTrackQuery aiAnalysisTrackQuery);
}
