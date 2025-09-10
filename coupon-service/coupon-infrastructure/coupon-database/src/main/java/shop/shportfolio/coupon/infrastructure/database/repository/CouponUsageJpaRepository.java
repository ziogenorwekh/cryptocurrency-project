package shop.shportfolio.coupon.infrastructure.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import shop.shportfolio.coupon.infrastructure.database.entity.CouponEntity;
import shop.shportfolio.coupon.infrastructure.database.entity.CouponUsageEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CouponUsageJpaRepository extends JpaRepository<CouponUsageEntity, UUID> {

    Optional<CouponUsageEntity> findCouponUsageEntityByUserIdAndCouponEntity_CouponId(UUID userId, UUID couponId);

    @Query("select c from CouponUsageEntity c where c.usageExpiryDate <= ?1")
    List<CouponUsageEntity> findCouponUsageEntitiesByUsageExpiryDateLessThanEqual(LocalDate today);

    @Query("select c.couponEntity from CouponUsageEntity c where c.usageExpiryDate <= ?1")
    List<CouponEntity> findExpiredCoupons(LocalDate today);

    void removeCouponUsageEntityByCouponEntity_CouponIdAndUserId(UUID couponId, UUID userId);


}
