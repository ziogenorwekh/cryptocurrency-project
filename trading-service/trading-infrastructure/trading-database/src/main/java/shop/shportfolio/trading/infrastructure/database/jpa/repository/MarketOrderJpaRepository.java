package shop.shportfolio.trading.infrastructure.database.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import shop.shportfolio.trading.infrastructure.database.jpa.entity.order.MarketOrderEntity;

import java.util.Optional;
import java.util.UUID;

public interface MarketOrderJpaRepository extends JpaRepository<MarketOrderEntity, String> {

    @Query("select m from MarketOrderEntity m where m.orderId = ?1")
    Optional<MarketOrderEntity> findMarketOrderEntityByOrderId(String orderId);
}
