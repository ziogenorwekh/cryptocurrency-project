package shop.shportfolio.coupon.application.ports.output.payment;

import shop.shportfolio.coupon.application.dto.payment.PaymentRequest;
import shop.shportfolio.coupon.application.dto.payment.PaymentResponse;

public interface PaymentTossAPIPort {

    PaymentResponse refund();

    PaymentResponse pay(PaymentRequest paymentRequest);

}
