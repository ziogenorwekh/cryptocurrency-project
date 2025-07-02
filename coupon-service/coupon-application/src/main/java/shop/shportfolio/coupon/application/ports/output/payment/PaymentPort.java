package shop.shportfolio.coupon.application.ports.output.payment;

import shop.shportfolio.coupon.application.dto.payment.PaymentCommand;
import shop.shportfolio.coupon.application.dto.payment.PaymentResult;
import shop.shportfolio.coupon.application.dto.payment.RefundCommand;

public interface PaymentPort {

    PaymentResult refund(RefundCommand command);

    PaymentResult pay(PaymentCommand command);

}
