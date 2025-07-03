package shop.shportfolio.coupon.application.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfoilo.coupon.domain.CouponDomainService;
import shop.shportfoilo.coupon.domain.entity.Coupon;
import shop.shportfolio.coupon.application.command.update.CouponUseUpdateCommand;
import shop.shportfolio.coupon.application.exception.CouponNotFoundException;
import shop.shportfolio.coupon.application.ports.output.repository.CouponRepositoryPort;

@Component
public class CouponUpdateHandler {

    private final CouponRepositoryPort couponRepositoryPort;
    private final CouponDomainService couponDomainService;

    @Autowired
    public CouponUpdateHandler(CouponRepositoryPort couponRepositoryPort,
                               CouponDomainService couponDomainService) {
        this.couponRepositoryPort = couponRepositoryPort;
        this.couponDomainService = couponDomainService;
    }

    public Coupon useCoupon(CouponUseUpdateCommand command) {
        Coupon coupon = couponRepositoryPort.findByUserIdAndCouponId(command.getUserId(), command.getCouponId())
                .orElseThrow(() -> new CouponNotFoundException(String.format("coupon id %s not found",
                        command.getCouponId())));
        couponDomainService.useCoupon(coupon);
        return couponRepositoryPort.save(coupon);
    }
}
