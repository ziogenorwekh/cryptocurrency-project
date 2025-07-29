package shop.shportfolio.trading.application.ports.input.kafka;

import shop.shportfolio.trading.application.dto.coupon.CouponKafkaResponse;

public interface CouponExpiredListener {

    void deleteCoupon(CouponKafkaResponse couponKafkaResponse);
}
