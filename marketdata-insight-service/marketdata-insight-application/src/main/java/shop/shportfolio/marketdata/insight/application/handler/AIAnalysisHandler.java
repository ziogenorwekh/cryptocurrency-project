package shop.shportfolio.marketdata.insight.application.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.valueobject.MarketId;
import shop.shportfolio.marketdata.insight.application.command.request.AiAnalysisTrackQuery;
import shop.shportfolio.marketdata.insight.application.dto.ai.AiAnalysisResponseDto;
import shop.shportfolio.marketdata.insight.application.exception.AiAnalyzeNotFoundException;
import shop.shportfolio.marketdata.insight.application.ports.output.repository.AIAnalysisResultRepositoryPort;
import shop.shportfolio.marketdata.insight.domain.MarketDataInsightDomainService;
import shop.shportfolio.marketdata.insight.domain.entity.AIAnalysisResult;
import shop.shportfolio.marketdata.insight.domain.valueobject.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
@Slf4j
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

    public AIAnalysisResult trackAiAnalysis(String marketId, PeriodType periodType) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime periodStart;
        LocalDateTime periodEnd = now;
        switch (periodType) {
            case THIRTY_MINUTES -> periodStart = now.minusMinutes(30);
            case ONE_HOUR -> periodStart = now.minusHours(1);
            case ONE_DAY -> periodStart = now.toLocalDate().atStartOfDay();
            case ONE_WEEK -> periodStart = now.minusWeeks(1).toLocalDate().atStartOfDay();
            case ONE_MONTH -> periodStart = now.minusMonths(1).toLocalDate().atStartOfDay();
            default -> throw new IllegalArgumentException("Unsupported PeriodType: " + periodType);
        }
        log.info("periodStart={}", periodStart);
        log.info("periodEnd={}", periodEnd);
        return repositoryPort.findAIAnalysisResult(marketId, periodType.name(), periodStart, periodEnd)
                .orElseThrow(() -> new AiAnalyzeNotFoundException("No AI Analysis Result found for marketId: " + marketId));
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
