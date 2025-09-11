package shop.shportfolio.marketdata.insight.application.ports.output.repository;

import shop.shportfolio.marketdata.insight.domain.entity.AIAnalysisResult;

import java.time.LocalDateTime;
import java.util.Optional;

public interface AIAnalysisResultRepositoryPort {

    AIAnalysisResult saveAIAnalysisResult(AIAnalysisResult aiAnalysisResult);

    Optional<AIAnalysisResult> findAIAnalysisResult(String marketId, String periodType,
                                                    LocalDateTime periodStart, LocalDateTime periodEnd);

    Optional<AIAnalysisResult> findLastAnalysis(String market, String periodType);
}
