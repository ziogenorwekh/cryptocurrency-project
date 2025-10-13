package shop.shportfolio.marketdata.insight.application.ports.input.usecase;

import jakarta.validation.Valid;
import shop.shportfolio.marketdata.insight.application.command.request.AiAnalysisTrackQuery;
import shop.shportfolio.marketdata.insight.application.command.response.AiAnalysisTrackResponse;

public interface InsightApplicationService {
    /***
     * AI 분석 요청
     * @param aiAnalysisTrackQuery
     * @return
     */
    AiAnalysisTrackResponse trackAiAnalysis(@Valid AiAnalysisTrackQuery aiAnalysisTrackQuery);
}
