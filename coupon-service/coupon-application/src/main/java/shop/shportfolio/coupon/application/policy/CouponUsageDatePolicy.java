package shop.shportfolio.coupon.application.policy;

import shop.shportfolio.common.domain.valueobject.UsageExpiryDate;

public interface CouponUsageDatePolicy {

    UsageExpiryDate calculateExpiryDate();
}
