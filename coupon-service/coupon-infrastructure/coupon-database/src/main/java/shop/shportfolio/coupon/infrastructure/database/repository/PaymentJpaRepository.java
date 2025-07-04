package shop.shportfolio.coupon.infrastructure.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.shportfolio.coupon.infrastructure.database.entity.PaymentEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentJpaRepository extends JpaRepository<PaymentEntity, UUID> {

    List<PaymentEntity> findPaymentEntityByUserId(UUID userId);

    Optional<PaymentEntity> findPaymentEntityByUserIdAndPaymentId(UUID userId, UUID paymentId);

    Optional<PaymentEntity> findPaymentEntityByUserIdAndCouponId(UUID userId, UUID couponId);
}
