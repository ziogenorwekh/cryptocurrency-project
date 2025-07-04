package shop.shportfolio.coupon.application.ports.output.payment;

import shop.shportfolio.coupon.application.dto.payment.PaymentPayRequest;
import shop.shportfolio.coupon.application.dto.payment.PaymentRefundRequest;
import shop.shportfolio.coupon.application.dto.payment.PaymentResponse;

public interface PaymentTossAPIPort {

    PaymentResponse refund(PaymentRefundRequest refundRequest);

    PaymentResponse pay(PaymentPayRequest paymentPayRequest);

}
