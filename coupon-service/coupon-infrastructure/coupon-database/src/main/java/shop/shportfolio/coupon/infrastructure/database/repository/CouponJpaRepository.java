package shop.shportfolio.coupon.infrastructure.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.shportfolio.coupon.infrastructure.database.entity.CouponEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CouponJpaRepository extends JpaRepository<CouponEntity, UUID> {


    List<CouponEntity> findCouponEntityByUserId(UUID userId);

    Optional<CouponEntity> findCouponEntityByUserIdAndCouponId(UUID userId, UUID couponId);
}
