package shop.shportfoilo.coupon.domain;

import shop.shportfoilo.coupon.domain.entity.Coupon;
import shop.shportfoilo.coupon.domain.entity.Payment;
import shop.shportfoilo.coupon.domain.valueobject.*;
import shop.shportfolio.common.domain.valueobject.CreatedAt;
import shop.shportfolio.common.domain.valueobject.OrderPrice;
import shop.shportfolio.common.domain.valueobject.UserId;

public interface CouponDomainService {

    Coupon createCoupon(OwnerId owner, FeeDiscount feeDiscount, ExpiryDate expiryDate, CouponCode couponCode);

    void useCoupon(Coupon coupon);

    void updateStatusIfCouponExpired(Coupon coupon);

    void cancel(Coupon coupon);

    void reactivate(Coupon coupon);

    Payment createPayment(UserId userId, PaymentKey paymentKey, OrderPrice totalAmount,
                          PaymentMethod paymentMethod, PaymentStatus status,
                          CreatedAt requestedAt, PaidAt paidAt,
                          Description description, String rawResponse);
}
