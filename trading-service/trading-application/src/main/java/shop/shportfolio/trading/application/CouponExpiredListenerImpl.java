package shop.shportfolio.trading.application;

import org.springframework.stereotype.Component;
import shop.shportfolio.trading.application.dto.coupon.CouponKafkaResponse;
import shop.shportfolio.trading.application.handler.CouponInfoHandler;
import shop.shportfolio.trading.application.ports.input.kafka.CouponExpiredListener;

@Component
public class CouponExpiredListenerImpl implements CouponExpiredListener {

    private final CouponInfoHandler couponInfoHandler;

    public CouponExpiredListenerImpl(CouponInfoHandler couponInfoHandler) {
        this.couponInfoHandler = couponInfoHandler;
    }

    @Override
    public void deleteCoupon(CouponKafkaResponse couponKafkaResponse) {
        couponInfoHandler.deleteCoupon(couponKafkaResponse);
    }
}
