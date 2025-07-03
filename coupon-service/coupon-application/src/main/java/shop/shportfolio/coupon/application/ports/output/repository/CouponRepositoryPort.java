package shop.shportfolio.coupon.application.ports.output.repository;

import shop.shportfoilo.coupon.domain.entity.Coupon;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CouponRepositoryPort {

    Coupon save(Coupon coupon);
    List<Coupon> findByUserId(UUID userId);
    Optional<Coupon> findByUserIdAndCouponId(UUID userId, UUID couponId);
}
