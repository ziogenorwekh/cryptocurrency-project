package shop.shportfolio.trading.infrastructure.database.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.shportfolio.trading.infrastructure.database.jpa.entity.userbalance.UserBalanceEntity;

import java.util.Optional;
import java.util.UUID;

public interface UserBalanceJpaRepository extends JpaRepository<UserBalanceEntity, UUID> {

    Optional<UserBalanceEntity> findUserBalanceByUserId(UUID userId);
}
