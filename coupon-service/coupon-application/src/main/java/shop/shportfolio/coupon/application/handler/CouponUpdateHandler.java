package shop.shportfolio.coupon.application.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfoilo.coupon.domain.CouponDomainService;
import shop.shportfoilo.coupon.domain.entity.Coupon;
import shop.shportfoilo.coupon.domain.entity.CouponUsage;
import shop.shportfoilo.coupon.domain.event.CouponUsedEvent;
import shop.shportfolio.common.domain.valueobject.UsageExpiryDate;
import shop.shportfolio.coupon.application.command.update.CouponUseUpdateCommand;
import shop.shportfolio.coupon.application.exception.CouponNotFoundException;
import shop.shportfolio.coupon.application.policy.CouponUsageDatePolicy;
import shop.shportfolio.coupon.application.ports.output.kafka.CouponUsedPublisher;
import shop.shportfolio.coupon.application.ports.output.repository.CouponRepositoryPort;

@Slf4j
@Component
public class CouponUpdateHandler {

    private final CouponRepositoryPort couponRepositoryPort;
    private final CouponDomainService couponDomainService;
    private final CouponUsedPublisher couponUsedPublisher;
    private final CouponUsageDatePolicy couponUsageDatePolicy;

    @Autowired
    public CouponUpdateHandler(CouponRepositoryPort couponRepositoryPort,
                               CouponDomainService couponDomainService,
                               CouponUsedPublisher couponUsedPublisher,
                               CouponUsageDatePolicy couponUsageDatePolicy) {
        this.couponRepositoryPort = couponRepositoryPort;
        this.couponDomainService = couponDomainService;
        this.couponUsedPublisher = couponUsedPublisher;
        this.couponUsageDatePolicy = couponUsageDatePolicy;
    }

    public CouponUsage useCoupon(CouponUseUpdateCommand command) {
        Coupon coupon = couponRepositoryPort.findByUserIdAndCouponId(command.getUserId(), command.getCouponId())
                .orElseThrow(() -> new CouponNotFoundException(String.format("coupon id %s not found",
                        command.getCouponId())));
        log.info("coupon use update command: {} , status : {}", coupon.getId().getValue(), coupon.getStatus().name());
        couponDomainService.useCoupon(coupon, command.getCouponCode());
        UsageExpiryDate usageExpiryDate = couponUsageDatePolicy.calculateExpiryDate();
        CouponUsage couponUsage = couponDomainService.createCouponUsage(coupon, usageExpiryDate);

        couponRepositoryPort.saveCouponUsage(couponUsage);
        couponRepositoryPort.save(coupon);
        CouponUsedEvent couponUsedEvent = couponDomainService.createEvent(coupon, couponUsage);
        couponUsedPublisher.publish(couponUsedEvent);
        return couponUsage;
    }

    public Coupon cancelCoupon(Coupon coupon) {
        couponDomainService.cancel(coupon);
        log.info("coupon cancel coupon id {} , status : {}", coupon.getId().getValue(), coupon.getStatus().name());
        return couponRepositoryPort.save(coupon);
    }
}
