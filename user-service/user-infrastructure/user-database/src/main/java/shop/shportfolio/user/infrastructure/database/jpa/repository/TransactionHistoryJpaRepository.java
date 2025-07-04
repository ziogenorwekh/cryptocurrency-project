package shop.shportfolio.user.infrastructure.database.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.shportfolio.user.infrastructure.database.jpa.entity.TransactionHistoryEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionHistoryJpaRepository extends JpaRepository<TransactionHistoryEntity, UUID> {

    List<TransactionHistoryEntity> findTransactionHistoryEntitiesByUserId(UUID userId);

    Optional<TransactionHistoryEntity> findTransactionHistoryEntityByUserIdAndTransactionId(
            UUID userId, UUID transactionId);

}
