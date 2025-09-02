package shop.shportfolio.portfolio.infrastructure.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import shop.shportfolio.portfolio.infrastructure.database.entity.CurrencyBalanceEntity;

import java.util.Optional;
import java.util.UUID;

public interface CurrencyBalanceJpaRepository extends JpaRepository<CurrencyBalanceEntity, UUID> {

    Optional<CurrencyBalanceEntity> findCurrencyBalanceEntityByUserId(UUID userId);
    @Query("select c from CurrencyBalanceEntity c where c.portfolioId = ?1")
    Optional<CurrencyBalanceEntity> findCurrencyBalanceEntityByPortfolioId(UUID portfolioId);
}
