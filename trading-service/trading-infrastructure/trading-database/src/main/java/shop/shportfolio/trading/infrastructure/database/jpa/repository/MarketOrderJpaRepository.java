package shop.shportfolio.trading.infrastructure.database.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.shportfolio.trading.infrastructure.database.jpa.entity.order.MarketOrderEntity;

import java.util.UUID;

public interface MarketOrderJpaRepository extends JpaRepository<MarketOrderEntity, String> {
}
