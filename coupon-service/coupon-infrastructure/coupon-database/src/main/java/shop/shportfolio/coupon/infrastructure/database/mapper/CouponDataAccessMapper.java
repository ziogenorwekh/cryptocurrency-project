package shop.shportfolio.coupon.infrastructure.database.mapper;

import org.springframework.stereotype.Component;
import shop.shportfoilo.coupon.domain.entity.Coupon;
import shop.shportfoilo.coupon.domain.entity.Payment;
import shop.shportfoilo.coupon.domain.valueobject.*;
import shop.shportfolio.common.domain.valueobject.CouponId;
import shop.shportfolio.common.domain.valueobject.CreatedAt;
import shop.shportfolio.common.domain.valueobject.OrderPrice;
import shop.shportfolio.common.domain.valueobject.UserId;
import shop.shportfolio.coupon.infrastructure.database.entity.CouponEntity;
import shop.shportfolio.coupon.infrastructure.database.entity.PaymentEntity;

import java.math.BigDecimal;

@Component
public class CouponDataAccessMapper {

    public CouponEntity couponToCouponEntity(Coupon coupon) {
        return CouponEntity.builder()
                .status(coupon.getStatus())
                .couponCode(coupon.getCouponCode().getValue())
                .couponId(coupon.getId().getValue())
                .expiryDate(coupon.getExpiryDate().getValue())
                .issuedAt(coupon.getIssuedAt().getValue())
                .feeDiscount(coupon.getFeeDiscount().getValue())
                .userId(coupon.getOwner().getValue())
                .build();
    }

    public Coupon couponEntityToCoupon(CouponEntity couponEntity) {
        return Coupon.builder()
                .issuedAt(new IssuedAt(couponEntity.getIssuedAt()))
                .couponCode(new CouponCode(couponEntity.getCouponCode()))
                .status(couponEntity.getStatus())
                .owner(new UserId(couponEntity.getUserId()))
                .feeDiscount(new FeeDiscount(couponEntity.getFeeDiscount()))
                .expiryDate(new ExpiryDate(couponEntity.getExpiryDate()))
                .couponId(new CouponId(couponEntity.getCouponId()))
                .build();
    }

    public Payment paymentEntityToPayment(PaymentEntity paymentEntity) {
        return Payment.builder()
                .paymentId(new PaymentId(paymentEntity.getPaymentId()))
                .userId(new UserId(paymentEntity.getUserId()))
                .couponId(new CouponId(paymentEntity.getCouponId()))
                .paymentKey(new PaymentKey(paymentEntity.getPaymentKey()))
                .totalAmount(new OrderPrice(BigDecimal.valueOf(paymentEntity.getTotalAmount())))
                .paymentMethod(paymentEntity.getPaymentMethod())
                .requestedAt(new CreatedAt(paymentEntity.getRequestedAt()))
                .paidAt(new PaidAt(paymentEntity.getPaidAt()))
                .description(new Description(paymentEntity.getDescription()))
                .rawResponse(paymentEntity.getRawResponse())
                .status(paymentEntity.getStatus())
                .cancelReason(paymentEntity.getCancelReason() == null ? null :
                        new CancelReason(paymentEntity.getCancelReason()))
                .cancelledAt(paymentEntity.getCancelledAt() == null ? null :
                        new CancelledAt(paymentEntity.getCancelledAt()))
                .build();
    }

    public PaymentEntity paymentToPaymentEntity(Payment payment) {
        return PaymentEntity.builder()
                .paymentId(payment.getId().getValue())
                .userId(payment.getUserId().getValue())
                .couponId(payment.getCouponId().getValue())
                .paymentKey(payment.getPaymentKey().getValue())
                .totalAmount(payment.getTotalAmount().getValue().longValue())
                .paymentMethod(payment.getPaymentMethod())
                .requestedAt(payment.getRequestedAt().getValue())
                .paidAt(payment.getPaidAt().getValue())
                .description(payment.getDescription().getValue())
                .rawResponse(payment.getRawResponse())
                .status(payment.getStatus())
                .cancelReason(payment.getCancelReason().getValue() == null
                        ? null : payment.getCancelReason().getValue())
                .cancelledAt(payment.getCancelledAt().getValue() == null
                        ? null : payment.getCancelledAt().getValue())
                .build();
    }
}
