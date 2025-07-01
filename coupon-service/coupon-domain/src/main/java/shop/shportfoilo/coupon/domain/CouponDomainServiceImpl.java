package shop.shportfoilo.coupon.domain;

import shop.shportfoilo.coupon.domain.entity.Coupon;
import shop.shportfoilo.coupon.domain.valueobject.CouponCode;
import shop.shportfoilo.coupon.domain.valueobject.Discount;
import shop.shportfoilo.coupon.domain.valueobject.ExpiryDate;
import shop.shportfoilo.coupon.domain.valueobject.OwnerId;

public class CouponDomainServiceImpl implements CouponDomainService {

    @Override
    public Coupon createCoupon(OwnerId owner, Discount discount, ExpiryDate expiryDate, CouponCode couponCode) {
        return Coupon.createCoupon(owner, discount, expiryDate, couponCode);
    }

    @Override
    public void useCoupon(Coupon coupon) {
        coupon.useCoupon();
    }
}
