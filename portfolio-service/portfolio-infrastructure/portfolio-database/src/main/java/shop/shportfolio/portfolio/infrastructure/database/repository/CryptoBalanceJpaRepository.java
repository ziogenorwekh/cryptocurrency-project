package shop.shportfolio.portfolio.infrastructure.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import shop.shportfolio.portfolio.infrastructure.database.entity.CryptoBalanceEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CryptoBalanceJpaRepository extends JpaRepository<CryptoBalanceEntity, UUID> {

    @Query("select c from CryptoBalanceEntity c where c.portfolioId = ?1 and c.marketId = ?2")
    Optional<CryptoBalanceEntity> findCryptoBalanceEntityByPortfolioIdAndMarketId(UUID portfolioId, String marketId);

    List<CryptoBalanceEntity> findCryptoBalanceEntitiesByPortfolioId(UUID portfolioId);
}
