package shop.shportfoilo.coupon.domain;

import shop.shportfoilo.coupon.domain.entity.Coupon;
import shop.shportfoilo.coupon.domain.entity.Payment;
import shop.shportfoilo.coupon.domain.valueobject.*;
import shop.shportfolio.common.domain.valueobject.CreatedAt;
import shop.shportfolio.common.domain.valueobject.OrderPrice;
import shop.shportfolio.common.domain.valueobject.UserId;

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

    @Override
    public Payment createPayment(UserId userId, PaymentKey paymentKey, OrderPrice totalAmount,
                                 PaymentMethod paymentMethod, PaymentStatus status, CreatedAt requestedAt,
                                 PaidAt paidAt, Description description, String rawResponse) {
        return Payment.createPayment(userId, paymentKey, totalAmount,
                paymentMethod, status, requestedAt,
                paidAt, description, rawResponse);
    }
}
