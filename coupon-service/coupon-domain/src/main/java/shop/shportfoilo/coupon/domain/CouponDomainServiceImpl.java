package shop.shportfoilo.coupon.domain;

import shop.shportfoilo.coupon.domain.entity.Coupon;
import shop.shportfoilo.coupon.domain.entity.Payment;
import shop.shportfoilo.coupon.domain.valueobject.*;
import shop.shportfolio.common.domain.valueobject.CouponId;
import shop.shportfolio.common.domain.valueobject.CreatedAt;
import shop.shportfolio.common.domain.valueobject.OrderPrice;
import shop.shportfolio.common.domain.valueobject.UserId;

public class CouponDomainServiceImpl implements CouponDomainService {

    @Override
    public Coupon createCoupon(UserId owner, FeeDiscount feeDiscount, ExpiryDate expiryDate, CouponCode couponCode) {
        return Coupon.createCoupon(owner, feeDiscount, expiryDate, couponCode);
    }

    @Override
    public void useCoupon(Coupon coupon,String code) {
        coupon.useCoupon(code);
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
    public Payment createPayment(UserId userId, CouponId couponId, PaymentKey paymentKey, OrderPrice totalAmount,
                                 PaymentMethod paymentMethod, PaymentStatus status, Description description, String rawResponse) {
        return Payment.createPayment(userId, couponId, paymentKey, totalAmount,
                paymentMethod, status, description, rawResponse);
    }

    @Override
    public Payment refundPayment(Payment payment, String reason) {
        payment.cancel(reason);
        return payment;
    }
}
