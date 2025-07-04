package shop.shportfolio.coupon.application.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfoilo.coupon.domain.CouponDomainService;
import shop.shportfoilo.coupon.domain.entity.Coupon;
import shop.shportfolio.coupon.application.command.update.CouponCancelUpdateCommand;
import shop.shportfolio.coupon.application.command.update.CouponUseUpdateCommand;
import shop.shportfolio.coupon.application.exception.CouponNotFoundException;
import shop.shportfolio.coupon.application.ports.output.repository.CouponRepositoryPort;

@Slf4j
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
        log.info("coupon use update command: {} , status : {}", coupon.getId().getValue(),coupon.getStatus().name());
        couponDomainService.useCoupon(coupon,command.getCouponCode());
        return couponRepositoryPort.save(coupon);
    }

    public Coupon cancelCoupon(Coupon coupon) {
        couponDomainService.cancel(coupon);
        log.info("coupon cancel coupon id {} , status : {}", coupon.getId().getValue(),coupon.getStatus().name());
        return couponRepositoryPort.save(coupon);
    }
}
