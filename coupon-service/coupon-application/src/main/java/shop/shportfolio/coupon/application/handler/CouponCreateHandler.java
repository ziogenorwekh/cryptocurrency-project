package shop.shportfolio.coupon.application.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import shop.shportfoilo.coupon.domain.CouponDomainService;
import shop.shportfoilo.coupon.domain.entity.Coupon;
import shop.shportfoilo.coupon.domain.valueobject.CouponCode;
import shop.shportfoilo.coupon.domain.valueobject.ValidUntil;
import shop.shportfolio.common.domain.valueobject.FeeDiscount;
import shop.shportfolio.common.domain.valueobject.UserId;
import shop.shportfolio.coupon.application.command.create.CouponCreateCommand;
import shop.shportfolio.coupon.application.policy.CouponDiscountPolicy;
import shop.shportfolio.coupon.application.policy.CouponHoldingPeriodPolicy;
import shop.shportfolio.coupon.application.ports.output.repository.CouponRepositoryPort;

@Slf4j
@Component
public class CouponCreateHandler {

    private final CouponDomainService couponDomainService;
    private final CouponRepositoryPort couponRepositoryPort;
    private final CouponDiscountPolicy couponDiscountPolicy;
    private final CouponHoldingPeriodPolicy couponHoldingPeriodPolicy;

    public CouponCreateHandler(CouponDomainService couponDomainService,
                               CouponRepositoryPort couponRepositoryPort,
                               CouponDiscountPolicy couponDiscountPolicy,
                               CouponHoldingPeriodPolicy couponHoldingPeriodPolicy) {
        this.couponDomainService = couponDomainService;
        this.couponRepositoryPort = couponRepositoryPort;
        this.couponDiscountPolicy = couponDiscountPolicy;
        this.couponHoldingPeriodPolicy = couponHoldingPeriodPolicy;
    }

    public Coupon createCoupon(CouponCreateCommand command) {
        // 각 Role별 할인율 조회
        FeeDiscount maxFeeDiscount = couponDiscountPolicy.calculatorDiscount(command.getRoles());
        ValidUntil validUntil = couponHoldingPeriodPolicy.calculateExpiryDate();
        Coupon coupon = couponDomainService.createCoupon(new UserId(command.getUserId()),
                maxFeeDiscount, validUntil, CouponCode.generate());
        // 쿠폰 생성
        log.info("Coupon created by Id: {}", coupon.getOwner().getValue());
        log.info("Coupon created by expiry date: {}:", validUntil.getValue());
        log.info("Coupon created by CouponCode: {}",coupon.getCouponCode().getValue());
        // 저장
        return couponRepositoryPort.save(coupon);
    }
}
