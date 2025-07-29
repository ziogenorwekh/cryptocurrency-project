package shop.shportfolio.trading.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.trading.application.dto.coupon.CouponKafkaResponse;
import shop.shportfolio.trading.application.handler.CouponInfoHandler;
import shop.shportfolio.trading.application.ports.input.kafka.CouponCreatedListener;
import shop.shportfolio.trading.domain.entity.CouponInfo;

@Slf4j
@Component
public class CouponCreatedListenerImpl implements CouponCreatedListener {

    private final CouponInfoHandler couponInfoHandler;


    @Autowired
    public CouponCreatedListenerImpl(CouponInfoHandler couponInfoHandler) {
        this.couponInfoHandler = couponInfoHandler;
    }

    @Override
    public void saveCoupon(CouponKafkaResponse couponKafkaResponse) {
        CouponInfo couponInfo = couponInfoHandler.saveCouponInfo(couponKafkaResponse);
        log.info("CouponAppliedListener saveCouponInfo");
        log.info("couponId : {}, userId : {}", couponInfo.getId().getValue(),
                couponInfo.getUserId().getValue());
    }
}
