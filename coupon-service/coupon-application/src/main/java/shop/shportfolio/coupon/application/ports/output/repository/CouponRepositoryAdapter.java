package shop.shportfolio.coupon.application.ports.output.repository;

import shop.shportfoilo.coupon.domain.entity.Coupon;

public interface CouponRepositoryAdapter {

    Coupon save(Coupon coupon);
}
