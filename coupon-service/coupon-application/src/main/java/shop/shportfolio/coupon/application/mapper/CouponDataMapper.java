package shop.shportfolio.coupon.application.mapper;

import org.springframework.stereotype.Component;
import shop.shportfoilo.coupon.domain.entity.Coupon;
import shop.shportfoilo.coupon.domain.entity.Payment;
import shop.shportfolio.coupon.application.command.create.CouponCreateCommand;
import shop.shportfolio.coupon.application.command.create.CouponCreatedResponse;
import shop.shportfolio.coupon.application.command.track.CouponTrackQueryResponse;
import shop.shportfolio.coupon.application.command.track.PaymentTrackQueryResponse;
import shop.shportfolio.coupon.application.command.update.CouponCancelUpdateResponse;
import shop.shportfolio.coupon.application.command.update.CouponUseUpdateResponse;
import shop.shportfolio.coupon.application.dto.payment.PaymentPayRequest;

@Component
public class CouponDataMapper {


    public CouponCreatedResponse couponToCouponCreatedResponse(Coupon coupon) {
        return new CouponCreatedResponse(coupon.getId().getValue(), coupon.getOwner().getValue(),
                coupon.getFeeDiscount().getValue(), coupon.getExpiryDate().getValue(),
                coupon.getIssuedAt().getValue(), coupon.getCouponCode().getValue(), coupon.getStatus());
    }

    public CouponTrackQueryResponse couponToCouponListTrackQueryResponse(Coupon coupon) {
        return new CouponTrackQueryResponse(coupon.getId().getValue(), coupon.getOwner().getValue(),
                coupon.getFeeDiscount().getValue(), coupon.getExpiryDate().getValue(), coupon.getIssuedAt().getValue(),
                coupon.getCouponCode().getValue(), coupon.getStatus());
    }

    public CouponUseUpdateResponse couponToCouponUpdateResponse(Coupon coupon) {
        return new CouponUseUpdateResponse(coupon.getId().getValue(),coupon.getOwner().getValue(),
                coupon.getFeeDiscount().getValue(),coupon.getCouponCode().getValue(), coupon.getStatus());
    }

    public PaymentPayRequest couponCreateCommandToPaymentRequest(CouponCreateCommand command) {
        return new PaymentPayRequest(command.getAmount(), command.getOrderId(), command.getPaymentKey());
    }


    public PaymentTrackQueryResponse paymentToPaymentTrackQueryResponse(Payment payment) {
        return new PaymentTrackQueryResponse(payment.getUserId().getValue(), payment.getId().getValue(),
                payment.getPaymentKey().getValue(), payment.getTotalAmount().getValue().longValue(),
                payment.getPaymentMethod(), payment.getStatus(), payment.getPaidAt().getValue(),
                payment.getDescription().getValue());
    }

    public CouponCancelUpdateResponse couponToCouponCancelUpdateResponse(Coupon coupon,Payment payment) {
        return new CouponCancelUpdateResponse(coupon.getId().getValue(), payment.getCancelReason().getValue(),
                coupon.getStatus(), payment.getCancelledAt().getValue());
    }
}
