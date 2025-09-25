package shop.shportfolio.portfolio.infrastructure.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.shportfolio.portfolio.infrastructure.database.entity.DepositWithdrawalEntity;

import java.util.Optional;
import java.util.UUID;

public interface DepositWithdrawalJpaRepository extends JpaRepository<DepositWithdrawalEntity, UUID> {

    Optional<DepositWithdrawalEntity> findDepositWithdrawalEntityByUserIdAndTransactionId(UUID userId,
                                                                                          UUID transactionId);
}
