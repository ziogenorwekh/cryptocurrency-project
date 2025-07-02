package shop.shportfolio.coupon.application.ports.output.payment;

import shop.shportfolio.coupon.application.dto.PaymentCommand;
import shop.shportfolio.coupon.application.dto.PaymentResult;
import shop.shportfolio.coupon.application.dto.RefundCommand;

public interface PaymentPort {

    PaymentResult refund(RefundCommand command);

    PaymentResult pay(PaymentCommand command);

}
