package shop.shportfolio.portfolio.infrastructure.database.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import shop.shportfolio.portfolio.infrastructure.database.entity.CurrencyBalanceEntity;

import java.util.Optional;
import java.util.UUID;

public interface CurrencyBalanceJpaRepository extends JpaRepository<CurrencyBalanceEntity, UUID> {

    Optional<CurrencyBalanceEntity> findCurrencyBalanceEntityByUserId(UUID userId);
    Optional<CurrencyBalanceEntity> findCurrencyBalanceEntityByPortfolioIdAndUserId(UUID portfolioId,UUID userId);
}
