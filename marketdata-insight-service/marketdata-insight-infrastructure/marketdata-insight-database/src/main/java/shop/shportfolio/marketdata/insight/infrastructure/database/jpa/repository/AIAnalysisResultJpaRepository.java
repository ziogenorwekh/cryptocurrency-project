package shop.shportfolio.marketdata.insight.infrastructure.database.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.shportfolio.marketdata.insight.infrastructure.database.jpa.entity.AIAnalysisResultEntity;

import java.util.UUID;

public interface AIAnalysisResultJpaRepository extends JpaRepository<AIAnalysisResultEntity, UUID> {
}
