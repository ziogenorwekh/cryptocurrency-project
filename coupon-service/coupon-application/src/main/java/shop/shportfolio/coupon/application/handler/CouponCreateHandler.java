package shop.shportfolio.coupon.application.handler;

import shop.shportfoilo.coupon.domain.CouponDomainService;
import shop.shportfoilo.coupon.domain.entity.Coupon;
import shop.shportfolio.coupon.application.command.create.CouponCreateCommand;
import shop.shportfolio.coupon.application.policy.CouponDiscountPolicy;
import shop.shportfolio.coupon.application.ports.output.repository.CouponRepositoryAdapter;

public class CouponCreateHandler {

    private final CouponDomainService couponDomainService;
    private final CouponRepositoryAdapter couponRepositoryAdapter;
    private final CouponDiscountPolicy couponDiscountPolicy;

    public CouponCreateHandler(CouponDomainService couponDomainService, CouponRepositoryAdapter couponRepositoryAdapter,
                               CouponDiscountPolicy couponDiscountPolicy) {
        this.couponDomainService = couponDomainService;
        this.couponRepositoryAdapter = couponRepositoryAdapter;
        this.couponDiscountPolicy = couponDiscountPolicy;
    }


    public Coupon createCoupon(CouponCreateCommand command) {
        // 가장 높은 등급을 기준으로 쿠폰을 생성해야 한다.
        command.getRoles().forEach(couponDiscountPolicy::calculatorDiscount);
        return null;
    }
}
