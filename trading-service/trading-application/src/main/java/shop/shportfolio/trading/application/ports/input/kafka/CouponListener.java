package shop.shportfolio.trading.application.ports.input.kafka;

import shop.shportfolio.trading.application.dto.coupon.CouponKafkaResponse;

public interface CouponListener {

    void saveCoupon(CouponKafkaResponse couponKafkaResponse);

    void deleteCoupon(CouponKafkaResponse couponKafkaResponse);
}
