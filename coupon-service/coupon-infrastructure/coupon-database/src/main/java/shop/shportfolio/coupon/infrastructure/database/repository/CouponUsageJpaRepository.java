package shop.shportfolio.coupon.infrastructure.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.shportfolio.coupon.infrastructure.database.entity.CouponUsageEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CouponUsageJpaRepository extends JpaRepository<CouponUsageEntity, UUID> {

    Optional<CouponUsageEntity> findCouponUsageEntityByUserIdAndCouponId(UUID userId, UUID couponId);

    List<CouponUsageEntity> findCouponUsageEntitiesByUsageExpiryDateEquals(LocalDate today);

    void removeCouponUsageEntityByCouponEntity_CouponIdAndUserId(UUID couponId, UUID userId);
}
