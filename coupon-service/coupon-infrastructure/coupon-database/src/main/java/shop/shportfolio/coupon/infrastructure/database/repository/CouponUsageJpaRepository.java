package shop.shportfolio.coupon.infrastructure.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.shportfolio.coupon.infrastructure.database.entity.CouponUsageEntity;

import java.util.Optional;
import java.util.UUID;

public interface CouponUsageJpaRepository extends JpaRepository<CouponUsageEntity, UUID> {

    Optional<CouponUsageEntity> findCouponUsageEntityByUserIdAndCouponId(UUID userId, UUID couponId);
}
