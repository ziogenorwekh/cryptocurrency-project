package shop.shportfolio.marketdata.insight.application;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import shop.shportfolio.marketdata.insight.application.command.request.AiAnalysisTrackQuery;
import shop.shportfolio.marketdata.insight.application.command.response.AiAnalysisTrackResponse;
import shop.shportfolio.marketdata.insight.application.mapper.MarketDataDtoMapper;
import shop.shportfolio.marketdata.insight.application.ports.input.usecase.AIAnalysisTrackUseCase;
import shop.shportfolio.marketdata.insight.application.ports.input.usecase.InsightApplicationService;
import shop.shportfolio.marketdata.insight.domain.entity.AIAnalysisResult;

@Slf4j
@Service
@Validated
public class InsightApplicationServiceImpl implements InsightApplicationService {

    private final MarketDataDtoMapper marketDataDtoMapper;
    private final AIAnalysisTrackUseCase aiAnalysisTrackUseCase;

    @Autowired
    public InsightApplicationServiceImpl(MarketDataDtoMapper marketDataDtoMapper,
                                         AIAnalysisTrackUseCase aiAnalysisTrackUseCase) {
        this.marketDataDtoMapper = marketDataDtoMapper;
        this.aiAnalysisTrackUseCase = aiAnalysisTrackUseCase;
    }

    @Override
    @Transactional(readOnly = true)
    public AiAnalysisTrackResponse trackAiAnalysis(@Valid AiAnalysisTrackQuery aiAnalysisTrackQuery) {
        AIAnalysisResult aiAnalysisResult = aiAnalysisTrackUseCase.trackAiAnalysis(aiAnalysisTrackQuery);
        return marketDataDtoMapper.aiAnalysisResultToAiAnalysisTrackResponse(aiAnalysisResult);
    }
}
