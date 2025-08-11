package shop.shportfolio.trading.infrastructure.database.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import shop.shportfolio.trading.infrastructure.database.jpa.entity.trade.TradeEntity;

import java.util.List;
import java.util.UUID;

public interface TradeJpaRepository extends JpaRepository<TradeEntity, UUID> {

    @Query("select t from TradeEntity t where t.marketId = ?1")
    List<TradeEntity> findTradeEntitiesByMarketId(String marketId);
}
