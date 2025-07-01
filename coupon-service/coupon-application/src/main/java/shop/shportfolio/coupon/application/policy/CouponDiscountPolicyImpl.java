package shop.shportfolio.coupon.application.policy;

import shop.shportfoilo.coupon.domain.exception.CouponDomainException;
import shop.shportfoilo.coupon.domain.valueobject.Discount;
import shop.shportfolio.common.domain.valueobject.RoleType;

public class CouponDiscountPolicyImpl implements CouponDiscountPolicy {
    @Override
    public Discount calculatorDiscount(RoleType role) {
        switch (role) {
            case USER -> {
                return Discount.ofUser();
            }
            case GOLD -> {
                return Discount.ofGold();
            }
            case SILVER -> {
                return Discount.ofSilver();
            }
            case VIP -> {
                return Discount.ofVip();
            }
            default -> {
                throw new CouponDomainException("RoleType not supported");
            }
        }
    }
}
