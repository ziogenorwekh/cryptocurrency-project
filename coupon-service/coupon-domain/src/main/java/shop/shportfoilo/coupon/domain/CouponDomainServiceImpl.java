package shop.shportfoilo.coupon.domain;

import shop.shportfoilo.coupon.domain.entity.Coupon;
import shop.shportfoilo.coupon.domain.valueobject.CouponCode;
import shop.shportfoilo.coupon.domain.valueobject.FeeDiscount;
import shop.shportfoilo.coupon.domain.valueobject.ExpiryDate;
import shop.shportfoilo.coupon.domain.valueobject.OwnerId;

public class CouponDomainServiceImpl implements CouponDomainService {

    @Override
    public Coupon createCoupon(OwnerId owner, FeeDiscount feeDiscount, ExpiryDate expiryDate, CouponCode couponCode) {
        return Coupon.createCoupon(owner, feeDiscount, expiryDate, couponCode);
    }

    @Override
    public void useCoupon(Coupon coupon) {
        coupon.useCoupon();
    }

    @Override
    public void updateStatusIfCouponExpired(Coupon coupon) {
        coupon.updateStatusIfCouponExpired();
    }

    @Override
    public void cancel(Coupon coupon) {
        coupon.cancel();
    }

    @Override
    public void reactivate(Coupon coupon) {
        coupon.reactivate();
    }
}
