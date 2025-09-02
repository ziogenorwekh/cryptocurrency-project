package shop.shportfolio.portfolio.infrastructure.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.shportfolio.portfolio.infrastructure.database.entity.AssetChangeLogEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AssetChangeLogJpaRepository extends JpaRepository<AssetChangeLogEntity, UUID> {

    Optional<AssetChangeLogEntity> findAssetChangeLogEntityByPortfolioId(UUID portfolioId);

    List<AssetChangeLogEntity> findAssetChangeLogEntitiesByUserId(UUID userId);
}
