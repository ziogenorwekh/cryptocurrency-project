package shop.shportfolio.trading.infrastructure.database.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import shop.shportfolio.trading.infrastructure.database.jpa.entity.userbalance.CryptoBalanceEntity;

import java.util.Optional;
import java.util.UUID;

public interface CryptoBalanceJpaRepository extends JpaRepository<CryptoBalanceEntity, String> {


    @Query("select c from CryptoBalanceEntity c where c.userId = ?1 and c.marketId = ?2")
    Optional<CryptoBalanceEntity> findCryptoBalanceByUserIdAndMarketId(UUID userId, String marketId);
}
