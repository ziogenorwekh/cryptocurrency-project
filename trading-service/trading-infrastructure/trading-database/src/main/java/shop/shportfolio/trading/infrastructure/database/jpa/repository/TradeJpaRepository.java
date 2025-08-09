package shop.shportfolio.trading.infrastructure.database.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.shportfolio.trading.infrastructure.database.jpa.entity.trade.TradeEntity;

import java.util.UUID;

public interface TradeJpaRepository extends JpaRepository<TradeEntity, UUID> {
}
