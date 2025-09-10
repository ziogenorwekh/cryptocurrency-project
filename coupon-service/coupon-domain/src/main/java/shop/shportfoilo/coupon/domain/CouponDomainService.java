package shop.shportfoilo.coupon.domain;

import shop.shportfoilo.coupon.domain.entity.Coupon;
import shop.shportfoilo.coupon.domain.entity.CouponUsage;
import shop.shportfoilo.coupon.domain.entity.Payment;
import shop.shportfoilo.coupon.domain.event.CouponExpiredEvent;
import shop.shportfoilo.coupon.domain.event.CouponUsedEvent;
import shop.shportfoilo.coupon.domain.valueobject.*;
import shop.shportfolio.common.domain.valueobject.*;

public interface CouponDomainService {

    Coupon createCoupon(UserId owner, FeeDiscount feeDiscount, ValidUntil validUntil, CouponCode couponCode);

    void useCoupon(Coupon coupon, String couponCode);

    CouponExpiredEvent updateStatusIfCouponExpired(Coupon coupon);

    void cancel(Coupon coupon);

    void reactivate(Coupon coupon);

    CouponUsage createCouponUsage(Coupon coupon, UsageExpiryDate expiryDate);

    Payment createPayment(UserId userId, CouponId couponId, PaymentKey paymentKey, OrderPrice totalAmount,
                          PaymentMethod paymentMethod, PaymentStatus status,
                          Description description, String rawResponse);

    Payment refundPayment(Payment payment, String reason);


    CouponUsedEvent createEvent(Coupon coupon, CouponUsage couponUsage);
}
