package shop.shportfolio.trading.application.ports.input.kafka;

import shop.shportfolio.trading.application.dto.coupon.CouponKafkaResponse;

public interface CouponCreatedListener {

    void saveCoupon(CouponKafkaResponse couponKafkaResponse);
}
