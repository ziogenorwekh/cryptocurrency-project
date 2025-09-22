package shop.shportfolio.marketdata.insight.application.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.valueobject.MarketId;
import shop.shportfolio.marketdata.insight.application.dto.ai.AiAnalysisResponseDto;
import shop.shportfolio.marketdata.insight.application.exception.AiAnalyzeNotFoundException;
import shop.shportfolio.marketdata.insight.application.ports.output.repository.AIAnalysisResultRepositoryPort;
import shop.shportfolio.marketdata.insight.domain.MarketDataInsightDomainService;
import shop.shportfolio.marketdata.insight.domain.entity.AIAnalysisResult;
import shop.shportfolio.marketdata.insight.domain.valueobject.*;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
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

    // UTC 기준으로 periodStart/periodEnd 계산
    // 최신 분석 결과 가져오기
    public AIAnalysisResult trackAiAnalysis(String marketId, PeriodType periodType) {
        return repositoryPort.findLastAnalysis(marketId, periodType.name())
                .orElseThrow(() -> new AiAnalyzeNotFoundException(
                        "No AI Analysis Result found for marketId: " + marketId + ", periodType: " + periodType));
    }

    // DTO → 도메인 생성
    public void createAIAnalysisResult(AiAnalysisResponseDto dto) {
        if (dto == null
                || "UNKNOWN".equals(dto.getMarketId())
                || dto.getAnalysisTime() == null
                || dto.getPeriodType() == null
                || dto.getPriceTrend() == null
                || dto.getSignal() == null) {
            log.warn("[AI] Invalid DTO, skipping save: {}", dto);
            return;
        }

        AIAnalysisResultId analysisResultId = new AIAnalysisResultId(UUID.randomUUID());
        AIAnalysisResult aiAnalysisResult = marketDataInsightService.createAIAnalysisResult(
                analysisResultId,
                new MarketId(dto.getMarketId()),
                new AnalysisTime(dto.getAnalysisTime()),        // OffsetDateTime 그대로
                new PeriodEnd(dto.getAnalysisTime()),          // 분석 종료 시간
                new PeriodStart(dto.getPeriodStart()),         // periodStart UTC
                new MomentumScore(dto.getMomentumScore()),
                dto.getPeriodType(),
                dto.getPriceTrend(),
                dto.getSignal(),
                new SummaryComment(dto.getSummaryCommentEng()),
                new SummaryComment(dto.getSummaryCommentKor())
        );

        log.info("[AI] Save result: {}", aiAnalysisResult);
        AIAnalysisResult saved = repositoryPort.saveAIAnalysisResult(aiAnalysisResult);
        log.info("[AI] Saved result: {}", saved);
    }
}
