package shop.shportfoilo.coupon.domain;

import shop.shportfoilo.coupon.domain.entity.Coupon;
import shop.shportfoilo.coupon.domain.valueobject.CouponCode;
import shop.shportfoilo.coupon.domain.valueobject.FeeDiscount;
import shop.shportfoilo.coupon.domain.valueobject.ExpiryDate;
import shop.shportfoilo.coupon.domain.valueobject.OwnerId;

public interface CouponDomainService {

    Coupon createCoupon(OwnerId owner, FeeDiscount feeDiscount, ExpiryDate expiryDate, CouponCode couponCode);

    void useCoupon(Coupon coupon);

    void updateStatusIfCouponExpired(Coupon coupon);

    void cancel(Coupon coupon);

    void reactivate(Coupon coupon);
}
