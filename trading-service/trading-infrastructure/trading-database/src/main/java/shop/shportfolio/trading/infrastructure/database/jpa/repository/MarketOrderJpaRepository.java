package shop.shportfolio.trading.infrastructure.database.jpa.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shop.shportfolio.trading.infrastructure.database.jpa.entity.order.MarketOrderEntity;

import java.util.Optional;
import java.util.UUID;

public interface MarketOrderJpaRepository extends JpaRepository<MarketOrderEntity, String> {

    Optional<MarketOrderEntity> findMarketOrderEntityByOrderId(String orderId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT m FROM MarketOrderEntity m WHERE m.orderId = :orderId")
    Optional<MarketOrderEntity> findMarketOrderEntityByOrderIdForUpdate(@Param("orderId") String orderId);
}
