package shop.shportfolio.marketdata.insight.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import shop.shportfolio.marketdata.insight.application.command.request.AiAnalysisTrackQuery;
import shop.shportfolio.marketdata.insight.application.command.response.AiAnalysisTrackResponse;
import shop.shportfolio.marketdata.insight.application.mapper.MarketDataDtoMapper;
import shop.shportfolio.marketdata.insight.application.ports.input.AIAnalysisUseCase;
import shop.shportfolio.marketdata.insight.application.ports.input.InsightApplicationService;
import shop.shportfolio.marketdata.insight.domain.entity.AIAnalysisResult;

@Slf4j
@Service
@Validated
public class InsightApplicationServiceImpl implements InsightApplicationService {

    private final MarketDataDtoMapper marketDataDtoMapper;
    private final AIAnalysisUseCase aiAnalysisUseCase;

    @Autowired
    public InsightApplicationServiceImpl(MarketDataDtoMapper marketDataDtoMapper,
                                         AIAnalysisUseCase aiAnalysisUseCase) {
        this.marketDataDtoMapper = marketDataDtoMapper;
        this.aiAnalysisUseCase = aiAnalysisUseCase;
    }

    @Override
    public AiAnalysisTrackResponse trackAiAnalysis(AiAnalysisTrackQuery aiAnalysisTrackQuery) {
        AIAnalysisResult aiAnalysisResult = aiAnalysisUseCase.trackAiAnalysis(aiAnalysisTrackQuery);
        return marketDataDtoMapper.aiAnalysisResultToAiAnalysisTrackResponse(aiAnalysisResult);
    }
}
