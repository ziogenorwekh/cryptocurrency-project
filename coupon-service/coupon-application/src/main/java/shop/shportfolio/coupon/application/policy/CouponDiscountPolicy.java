package shop.shportfolio.coupon.application.policy;

import shop.shportfoilo.coupon.domain.valueobject.Discount;
import shop.shportfolio.common.domain.valueobject.RoleType;

public interface CouponDiscountPolicy {

    Discount calculatorDiscount(RoleType role);
}
