package shop.shportfolio.coupon.infrastructure.database.mapper;

import org.springframework.stereotype.Component;
import shop.shportfoilo.coupon.domain.entity.Coupon;
import shop.shportfoilo.coupon.domain.entity.CouponUsage;
import shop.shportfoilo.coupon.domain.entity.Payment;
import shop.shportfoilo.coupon.domain.valueobject.*;
import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.coupon.infrastructure.database.entity.CouponEntity;
import shop.shportfolio.coupon.infrastructure.database.entity.CouponUsageEntity;
import shop.shportfolio.coupon.infrastructure.database.entity.PaymentEntity;

import java.math.BigDecimal;

@Component
public class CouponDataAccessMapper {

    public CouponEntity couponToCouponEntity(Coupon coupon) {
        return CouponEntity.builder()
                .status(coupon.getStatus())
                .couponCode(coupon.getCouponCode().getValue())
                .couponId(coupon.getId().getValue())
                .validUntil(coupon.getValidUntil().getValue())
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
                .validUntil(new ValidUntil(couponEntity.getValidUntil()))
                .couponId(new CouponId(couponEntity.getCouponId()))
                .build();
    }

    public CouponUsage couponUsageEntityToCouponUsage(CouponUsageEntity couponUsageEntity) {
        return CouponUsage.builder()
                .couponUsageId(new CouponUsageId(couponUsageEntity.getCouponUsageId()))
                .couponId(new CouponId(couponUsageEntity.getCouponEntity().getCouponId()))
                .userId(new UserId(couponUsageEntity.getUserId()))
                .issuedAt(new IssuedAt(couponUsageEntity.getIssuedAt()))
                .expiryDate(new UsageExpiryDate(couponUsageEntity.getUsageExpiryDate()))
                .build();
    }

    public CouponUsageEntity couponUsageToCouponUsageEntity(CouponUsage couponUsage,CouponEntity couponEntity) {
        return CouponUsageEntity.builder()
                .couponUsageId(couponUsage.getId().getValue())
                .couponEntity(couponEntity)
                .issuedAt(couponUsage.getIssuedAt().getValue())
                .usageExpiryDate(couponUsage.getExpiryDate().getValue())
                .userId(couponUsage.getUserId().getValue())
                .build();
    }

    public Payment paymentEntityToPayment(PaymentEntity paymentEntity) {
        if (paymentEntity == null) return null;

        return Payment.builder()
                .paymentId(paymentEntity.getPaymentId() != null ? new PaymentId(paymentEntity.getPaymentId()) : null)
                .userId(paymentEntity.getUserId() != null ? new UserId(paymentEntity.getUserId()) : null)
                .couponId(paymentEntity.getCouponId() != null ? new CouponId(paymentEntity.getCouponId()) : null)
                .paymentKey(paymentEntity.getPaymentKey() != null ? new PaymentKey(paymentEntity.getPaymentKey()) : null)
                .totalAmount(paymentEntity.getTotalAmount() != null ? new OrderPrice(BigDecimal.valueOf(paymentEntity.getTotalAmount())) : null)
                .paymentMethod(paymentEntity.getPaymentMethod())
                .requestedAt(paymentEntity.getRequestedAt() != null ? new CreatedAt(paymentEntity.getRequestedAt()) : null)
                .paidAt(paymentEntity.getPaidAt() != null ? new PaidAt(paymentEntity.getPaidAt()) : null)
                .description(paymentEntity.getDescription() != null ? new Description(paymentEntity.getDescription()) : null)
                .rawResponse(paymentEntity.getRawResponse())
                .status(paymentEntity.getStatus())
                .cancelReason(paymentEntity.getCancelReason() != null ? new CancelReason(paymentEntity.getCancelReason()) : null)
                .cancelledAt(paymentEntity.getCancelledAt() != null ? new CancelledAt(paymentEntity.getCancelledAt()) : null)
                .build();
    }

    public PaymentEntity paymentToPaymentEntity(Payment payment) {
        if (payment == null) return null;

        return PaymentEntity.builder()
                .paymentId(payment.getId() != null ? payment.getId().getValue() : null)
                .userId(payment.getUserId() != null ? payment.getUserId().getValue() : null)
                .couponId(payment.getCouponId() != null ? payment.getCouponId().getValue() : null)
                .paymentKey(payment.getPaymentKey() != null ? payment.getPaymentKey().getValue() : null)
                .totalAmount(payment.getTotalAmount() != null ? payment.getTotalAmount().getValue().longValue() : null)
                .paymentMethod(payment.getPaymentMethod())
                .requestedAt(payment.getRequestedAt() != null ? payment.getRequestedAt().getValue() : null)
                .paidAt(payment.getPaidAt() != null ? payment.getPaidAt().getValue() : null)
                .description(payment.getDescription() != null ? payment.getDescription().getValue() : null)
                .rawResponse(payment.getRawResponse())
                .status(payment.getStatus())
                .cancelReason(payment.getCancelReason() != null ? payment.getCancelReason().getValue() : null)
                .cancelledAt(payment.getCancelledAt() != null ? payment.getCancelledAt().getValue() : null)
                .build();
    }
}
