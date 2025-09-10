package shop.shportfolio.coupon.application.policy;

import shop.shportfoilo.coupon.domain.valueobject.ValidUntil;

public interface CouponHoldingPeriodPolicy {

    ValidUntil calculateExpiryDate();
}
