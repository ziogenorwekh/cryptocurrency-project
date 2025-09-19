package shop.shportfolio.portfolio.infrastructure.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import shop.shportfolio.portfolio.domain.valueobject.ChangeType;
import shop.shportfolio.portfolio.infrastructure.database.entity.AssetChangeLogEntity;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AssetChangeLogJpaRepository extends JpaRepository<AssetChangeLogEntity, UUID> {

    Optional<AssetChangeLogEntity> findAssetChangeLogEntityByPortfolioId(UUID portfolioId);

    List<AssetChangeLogEntity> findAssetChangeLogEntitiesByUserId(UUID userId);


    @Query("select a from AssetChangeLogEntity a where a.userId = ?1 and a.changeType in ?2 order by a.createdAt DESC")
    List<AssetChangeLogEntity> findAssetChangeLogEntitiesByUserIdAndChangeTypeInOrderByCreatedAtDesc(UUID userId, Collection<ChangeType> changeTypes);

    @Query("select a from AssetChangeLogEntity a where a.userId = ?1 and a.marketId = ?2 order by a.createdAt DESC")
    List<AssetChangeLogEntity> findAssetChangeLogEntitiesByUserIdAndMarketIdOrderByCreatedAtDesc(UUID userId, String marketId);

}
