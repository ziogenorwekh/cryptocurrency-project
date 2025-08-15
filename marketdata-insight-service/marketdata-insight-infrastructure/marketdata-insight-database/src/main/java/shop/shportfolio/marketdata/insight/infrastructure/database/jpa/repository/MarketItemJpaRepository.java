package shop.shportfolio.marketdata.insight.infrastructure.database.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.shportfolio.marketdata.insight.infrastructure.database.jpa.entity.MarketItemEntity;

import java.util.Optional;

public interface MarketItemJpaRepository extends JpaRepository<MarketItemEntity, String> {

    Optional<MarketItemEntity> findMarketItemEntityByMarketId(String marketId);
}
