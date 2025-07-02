package shop.shportfolio.coupon.application.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfoilo.coupon.domain.CouponDomainService;
import shop.shportfoilo.coupon.domain.entity.Coupon;
import shop.shportfolio.coupon.application.command.update.CouponUseUpdateCommand;
import shop.shportfolio.coupon.application.exception.CouponNotFoundException;
import shop.shportfolio.coupon.application.ports.output.repository.CouponRepositoryAdapter;

@Component
public class CouponUpdateHandler {

    private final CouponRepositoryAdapter couponRepositoryAdapter;
    private final CouponDomainService couponDomainService;

    @Autowired
    public CouponUpdateHandler(CouponRepositoryAdapter couponRepositoryAdapter,
                               CouponDomainService couponDomainService) {
        this.couponRepositoryAdapter = couponRepositoryAdapter;
        this.couponDomainService = couponDomainService;
    }

    public Coupon useCoupon(CouponUseUpdateCommand command) {
        Coupon coupon = couponRepositoryAdapter.findByUserIdAndCouponId(command.getUserId(), command.getCouponId())
                .orElseThrow(() -> new CouponNotFoundException(String.format("coupon id %s not found",
                        command.getCouponId())));
        couponDomainService.useCoupon(coupon);
        return couponRepositoryAdapter.save(coupon);
    }
}
