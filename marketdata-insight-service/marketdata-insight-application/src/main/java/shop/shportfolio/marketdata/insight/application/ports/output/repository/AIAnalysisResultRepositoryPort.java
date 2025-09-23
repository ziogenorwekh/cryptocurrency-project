package shop.shportfolio.marketdata.insight.application.ports.output.repository;

import shop.shportfolio.marketdata.insight.domain.entity.AIAnalysisResult;
import shop.shportfolio.marketdata.insight.domain.valueobject.PeriodType;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Optional;

public interface AIAnalysisResultRepositoryPort {

    AIAnalysisResult saveAIAnalysisResult(AIAnalysisResult aiAnalysisResult);

    Optional<AIAnalysisResult> findAIAnalysisResult(String marketId, String periodType,
                                                    OffsetDateTime periodStart, OffsetDateTime periodEnd);

    Optional<AIAnalysisResult> findLastAnalysis(String market, PeriodType periodType);
}
