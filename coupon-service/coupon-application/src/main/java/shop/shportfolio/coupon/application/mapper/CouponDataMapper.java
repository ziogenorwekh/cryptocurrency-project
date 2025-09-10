package shop.shportfolio.coupon.application.mapper;

import org.springframework.stereotype.Component;
import shop.shportfoilo.coupon.domain.entity.Coupon;
import shop.shportfoilo.coupon.domain.entity.CouponUsage;
import shop.shportfoilo.coupon.domain.entity.Payment;
import shop.shportfolio.coupon.application.command.create.CouponCreateCommand;
import shop.shportfolio.coupon.application.command.create.CouponCreatedResponse;
import shop.shportfolio.coupon.application.command.track.CouponTrackQueryResponse;
import shop.shportfolio.coupon.application.command.track.CouponUsageTrackQueryResponse;
import shop.shportfolio.coupon.application.command.track.PaymentTrackQueryResponse;
import shop.shportfolio.coupon.application.command.update.CouponCancelUpdateResponse;
import shop.shportfolio.coupon.application.command.update.CouponUsedResponse;
import shop.shportfolio.common.domain.dto.payment.PaymentPayRequest;

@Component
public class CouponDataMapper {


    public CouponCreatedResponse couponToCouponCreatedResponse(Coupon coupon) {
        return new CouponCreatedResponse(coupon.getId().getValue(), coupon.getOwner().getValue(),
                coupon.getFeeDiscount().getValue(), coupon.getValidUntil().getValue(),
                coupon.getIssuedAt().getValue(), coupon.getCouponCode().getValue(), coupon.getStatus());
    }

    public CouponTrackQueryResponse couponToCouponListTrackQueryResponse(Coupon coupon) {
        return new CouponTrackQueryResponse(coupon.getId().getValue(), coupon.getOwner().getValue(),
                coupon.getFeeDiscount().getValue(), coupon.getValidUntil().getValue(), coupon.getIssuedAt().getValue(),
                coupon.getCouponCode().getValue(), coupon.getStatus());
    }

    public CouponUsedResponse couponToCouponUsedResponse(CouponUsage coupon) {
        return new CouponUsedResponse(coupon.getCouponId().getValue(), coupon.getUserId().getValue(),
                coupon.getIssuedAt().getValue(),coupon.getExpiryDate().getValue());
    }

    public PaymentPayRequest couponCreateCommandToPaymentRequest(CouponCreateCommand command) {
        return new PaymentPayRequest(Long.parseLong(command.getAmount()), command.getOrderId(), command.getPaymentKey());
    }


    public PaymentTrackQueryResponse paymentToPaymentTrackQueryResponse(Payment payment) {
        return new PaymentTrackQueryResponse(
                payment.getTotalAmount().getValue().longValue(),
                payment.getPaymentMethod(), payment.getStatus(), payment.getPaidAt().getValue(),
                payment.getDescription().getValue(), payment.getCancelReason() == null ? null :
                payment.getCancelReason().getValue(),
                payment.getCancelledAt() == null ? null : payment.getCancelledAt().getValue());
    }

    public CouponCancelUpdateResponse couponToCouponCancelUpdateResponse(Coupon coupon,Payment payment) {
        return new CouponCancelUpdateResponse(coupon.getId().getValue(), payment.getCancelReason().getValue(),
                coupon.getStatus(), payment.getCancelledAt().getValue());
    }

    public CouponUsageTrackQueryResponse couponToCouponUsageTrackQueryResponse(CouponUsage couponUsage) {
        return new CouponUsageTrackQueryResponse(couponUsage.getCouponId().getValue(), couponUsage.getId().getValue()
                , couponUsage.getUserId().getValue(), couponUsage.getExpiryDate().getValue(),
                couponUsage.getIssuedAt().getValue());
    }
}
