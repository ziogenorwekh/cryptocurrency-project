package shop.shportfolio.trading.application.ports.input.kafka;

import shop.shportfolio.trading.application.dto.coupon.CouponResponse;

public interface CouponAppliedListener {

    void saveCoupon(CouponResponse couponResponse);
}
