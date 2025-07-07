package shop.shportfolio.trading.application.ports.input.kafka;

import shop.shportfolio.trading.application.dto.coupon.CouponKafkaResponse;

public interface CouponAppliedListener {

    void saveCoupon(CouponKafkaResponse couponKafkaResponse);
}
