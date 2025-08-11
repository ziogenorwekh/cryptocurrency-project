package shop.shportfolio.trading.infrastructure.database.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import shop.shportfolio.trading.infrastructure.database.jpa.entity.order.LimitOrderEntity;

import java.util.Optional;
import java.util.UUID;

public interface LimitOrderJpaRepository extends JpaRepository<LimitOrderEntity, String> {

    Optional<LimitOrderEntity> findLimitOrderEntityByOrderIdAndUserId(String orderId, UUID userId);
}
