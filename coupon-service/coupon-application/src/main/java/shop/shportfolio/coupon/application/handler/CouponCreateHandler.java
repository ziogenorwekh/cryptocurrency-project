package shop.shportfolio.coupon.application.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import shop.shportfoilo.coupon.domain.CouponDomainService;
import shop.shportfoilo.coupon.domain.entity.Coupon;
import shop.shportfoilo.coupon.domain.valueobject.CouponCode;
import shop.shportfoilo.coupon.domain.valueobject.ExpiryDate;
import shop.shportfoilo.coupon.domain.valueobject.FeeDiscount;
import shop.shportfoilo.coupon.domain.valueobject.OwnerId;
import shop.shportfolio.common.domain.valueobject.UserId;
import shop.shportfolio.coupon.application.command.create.CouponCreateCommand;
import shop.shportfolio.coupon.application.policy.CouponDiscountPolicy;
import shop.shportfolio.coupon.application.policy.ExpireAtPolicy;
import shop.shportfolio.coupon.application.ports.output.repository.CouponRepositoryPort;

@Slf4j
@Component
public class CouponCreateHandler {

    private final CouponDomainService couponDomainService;
    private final CouponRepositoryPort couponRepositoryPort;
    private final CouponDiscountPolicy couponDiscountPolicy;
    private final @Qualifier("roleBasedExpireAtPolicy") ExpireAtPolicy expireAtPolicy;

    public CouponCreateHandler(CouponDomainService couponDomainService,
                               CouponRepositoryPort couponRepositoryPort,
                               CouponDiscountPolicy couponDiscountPolicy,
                               @Qualifier("roleBasedExpireAtPolicy") ExpireAtPolicy expireAtPolicy) {
        this.couponDomainService = couponDomainService;
        this.couponRepositoryPort = couponRepositoryPort;
        this.couponDiscountPolicy = couponDiscountPolicy;
        this.expireAtPolicy = expireAtPolicy;
    }


    public Coupon createCoupon(CouponCreateCommand command) {
        // 각 Role별 할인율 조회
        FeeDiscount maxFeeDiscount = couponDiscountPolicy.calculatorDiscount(command.getRoles());
        ExpiryDate expiryDate = expireAtPolicy.calculate(command.getRoles());
        Coupon coupon = couponDomainService.createCoupon(new UserId(command.getUserId()),
                maxFeeDiscount, expiryDate, CouponCode.generate());
        // 쿠폰 생성
        log.info("Coupon created by Id: {}", coupon.getOwner().getValue());
        log.info("Coupon created by expiry date: {}:", expiryDate.getValue());
        log.info("Coupon created by CouponCode: {}",coupon.getCouponCode().getValue());
        // 저장
        return couponRepositoryPort.save(coupon);
    }
}
