package shop.shportfolio.trading.infrastructure.database.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import shop.shportfolio.trading.infrastructure.database.jpa.entity.userbalance.UserBalanceEntity;

import java.util.Optional;
import java.util.UUID;

public interface UserBalanceJpaRepository extends JpaRepository<UserBalanceEntity, UUID> {


    Optional<UserBalanceEntity> findUserBalanceByUserId(UUID userId);

    @Modifying
    @Query("delete from UserBalanceEntity u where u.userId = ?1")
    void deleteUserBalanceByUserId(UUID userId);
}
