package shop.shportfolio.portfolio.infrastructure.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.shportfolio.portfolio.infrastructure.database.entity.PaymentEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentJpaRepository extends JpaRepository<PaymentEntity, UUID> {

    List<PaymentEntity> findPaymentEntitiesByUserId(UUID userId);

    Optional<PaymentEntity> findPaymentEntityByUserIdAndPaymentId(UUID userId, UUID paymentId);
}
