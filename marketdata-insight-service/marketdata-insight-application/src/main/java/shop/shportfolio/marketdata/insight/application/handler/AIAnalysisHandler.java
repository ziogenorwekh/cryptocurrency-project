package shop.shportfolio.marketdata.insight.application.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.valueobject.MarketId;
import shop.shportfolio.marketdata.insight.application.command.request.AiAnalysisTrackQuery;
import shop.shportfolio.marketdata.insight.application.dto.ai.AiAnalysisResponseDto;
import shop.shportfolio.marketdata.insight.application.ports.output.repository.AIAnalysisResultRepositoryPort;
import shop.shportfolio.marketdata.insight.domain.MarketDataInsightDomainService;
import shop.shportfolio.marketdata.insight.domain.entity.AIAnalysisResult;
import shop.shportfolio.marketdata.insight.domain.valueobject.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Component
public class AIAnalysisHandler {

    private final AIAnalysisResultRepositoryPort repositoryPort;
    private final MarketDataInsightDomainService marketDataInsightService;

    @Autowired
    public AIAnalysisHandler(AIAnalysisResultRepositoryPort repositoryPort,
                             MarketDataInsightDomainService marketDataInsightService) {
        this.repositoryPort = repositoryPort;
        this.marketDataInsightService = marketDataInsightService;
    }

    public Optional<AIAnalysisResult> trackAiAnalysis(String marketId, LocalDateTime periodStart,
                                                      LocalDateTime periodEnd, PeriodType periodType) {
        return repositoryPort.findAIAnalysisResult(marketId, periodType.name(), periodStart, periodEnd);
    }

    public AIAnalysisResult createAIAnalysisResult(AiAnalysisResponseDto dto) {
        AIAnalysisResultId analysisResultId = new AIAnalysisResultId(UUID.randomUUID());
        AIAnalysisResult aiAnalysisResult = marketDataInsightService.createAIAnalysisResult(analysisResultId,
                new MarketId(dto.getMarketId()), new AnalysisTime(dto.getAnalysisTime()),
                new PeriodEnd(dto.getAnalysisTime()), new PeriodStart(dto.getPeriodStart()),
                new MomentumScore(dto.getMomentumScore()), dto.getPeriodType(),
                dto.getPriceTrend(), dto.getSignal(), new SummaryComment(dto.getSummaryComment()));
        return repositoryPort.saveAIAnalysisResult(aiAnalysisResult);
    }
}
