package shop.shportfolio.coupon.application.ports.input.kafka;

import shop.shportfolio.common.domain.valueobject.UserId;

public interface CouponUserListener {

    void deleteCoupon(UserId userId);
}
