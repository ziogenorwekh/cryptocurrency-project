package shop.shportfoilo.coupon.domain;

import shop.shportfoilo.coupon.domain.entity.Coupon;
import shop.shportfoilo.coupon.domain.valueobject.CouponCode;
import shop.shportfoilo.coupon.domain.valueobject.Discount;
import shop.shportfoilo.coupon.domain.valueobject.ExpiryDate;
import shop.shportfoilo.coupon.domain.valueobject.OwnerId;

public interface CouponDomainService {

    Coupon createCoupon(OwnerId owner, Discount discount, ExpiryDate expiryDate, CouponCode couponCode);

    void useCoupon(Coupon coupon);
}
