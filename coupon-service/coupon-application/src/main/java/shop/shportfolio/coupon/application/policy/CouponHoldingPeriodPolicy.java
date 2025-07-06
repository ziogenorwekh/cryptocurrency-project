package shop.shportfolio.coupon.application.policy;

import shop.shportfoilo.coupon.domain.valueobject.ExpiryDate;

public interface CouponHoldingPeriodPolicy {

    ExpiryDate calculateExpiryDate();
}
