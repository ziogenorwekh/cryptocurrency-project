package shop.shportfolio.trading.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.trading.application.dto.coupon.CouponResponse;
import shop.shportfolio.trading.application.handler.create.CouponInfoCreateHandler;
import shop.shportfolio.trading.application.ports.input.kafka.CouponAppliedListener;
import shop.shportfolio.trading.domain.entity.CouponInfo;

@Slf4j
@Component
public class CouponAppliedListenerImpl implements CouponAppliedListener {

    private final CouponInfoCreateHandler couponInfoCreateHandler;

    @Autowired
    public CouponAppliedListenerImpl(CouponInfoCreateHandler couponInfoCreateHandler) {
        this.couponInfoCreateHandler = couponInfoCreateHandler;
    }

    @Override
    public void saveCoupon(CouponResponse couponResponse) {
        CouponInfo couponInfo = couponInfoCreateHandler.saveCouponInfo(couponResponse);
        log.info("CouponAppliedListener saveCouponInfo");
        log.info("couponId : {}, userId : {}", couponInfo.getId().getValue(),
                couponInfo.getUserId().getValue());
    }
}
