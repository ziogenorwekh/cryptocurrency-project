package shop.shportfolio.trading.infrastructure.database.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.shportfolio.trading.infrastructure.database.jpa.entity.order.LimitOrderEntity;

import java.util.UUID;

public interface LimitOrderJpaRepository extends JpaRepository<LimitOrderEntity, UUID> {
}
