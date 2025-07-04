package shop.shportfoilo.coupon.domain;

import shop.shportfoilo.coupon.domain.entity.Coupon;
import shop.shportfoilo.coupon.domain.entity.Payment;
import shop.shportfoilo.coupon.domain.valueobject.*;
import shop.shportfolio.common.domain.valueobject.CouponId;
import shop.shportfolio.common.domain.valueobject.CreatedAt;
import shop.shportfolio.common.domain.valueobject.OrderPrice;
import shop.shportfolio.common.domain.valueobject.UserId;

public interface CouponDomainService {

    Coupon createCoupon(UserId owner, FeeDiscount feeDiscount, ExpiryDate expiryDate, CouponCode couponCode);

    void useCoupon(Coupon coupon,String couponCode);

    void updateStatusIfCouponExpired(Coupon coupon);

    void cancel(Coupon coupon);

    void reactivate(Coupon coupon);

    Payment createPayment(UserId userId, CouponId couponId, PaymentKey paymentKey, OrderPrice totalAmount,
                          PaymentMethod paymentMethod, PaymentStatus status,
                          Description description, String rawResponse);

    Payment refundPayment(Payment payment,String reason);


}
