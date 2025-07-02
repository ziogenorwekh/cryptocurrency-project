package shop.shportfolio.coupon.application.policy;

import shop.shportfoilo.coupon.domain.valueobject.FeeDiscount;
import shop.shportfolio.common.domain.valueobject.RoleType;

import java.util.List;

public interface CouponDiscountPolicy {

    FeeDiscount calculatorDiscount(List<RoleType> roles);
}
