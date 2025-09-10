package shop.shportfolio.coupon.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import shop.shportfoilo.coupon.domain.entity.Coupon;
import shop.shportfolio.common.domain.valueobject.UserId;
import shop.shportfolio.coupon.application.ports.input.kafka.CouponUserListener;
import shop.shportfolio.coupon.application.ports.output.repository.CouponRepositoryPort;

import java.util.List;

@Slf4j
@Component
public class CouponUserListenerImpl implements CouponUserListener {

    private final CouponRepositoryPort couponRepositoryPort;

    @Autowired
    public CouponUserListenerImpl(CouponRepositoryPort couponRepositoryPort) {
        this.couponRepositoryPort = couponRepositoryPort;
    }

    @Override
    @Transactional
    public void deleteCoupon(UserId userId) {
        log.info("delete coupon by user Id : {}", userId.getValue());
        List<Coupon> couponList = couponRepositoryPort.findByUserId(userId.getValue());
        couponList.forEach(coupon -> {
            couponRepositoryPort.removeCouponUsageByCouponIdAndUserId(coupon.getId().getValue(), userId.getValue());
        });
    }
}
