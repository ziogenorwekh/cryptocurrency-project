package shop.shportfolio.user.database.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.shportfolio.user.database.jpa.entity.TransactionHistoryEntity;

import java.util.UUID;

public interface TransactionHistoryJpaRepository extends JpaRepository<TransactionHistoryEntity, UUID> {

}
