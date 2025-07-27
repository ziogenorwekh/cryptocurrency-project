package shop.shportfolio.portfolio.application.port.output.payment;

import shop.shportfolio.common.domain.dto.payment.PaymentPayRequest;
import shop.shportfolio.common.domain.dto.payment.PaymentRefundRequest;
import shop.shportfolio.common.domain.dto.payment.PaymentResponse;

public interface PaymentTossAPIPort {

    PaymentResponse refund(PaymentRefundRequest refundRequest);

    PaymentResponse pay(PaymentPayRequest paymentPayRequest);

}
