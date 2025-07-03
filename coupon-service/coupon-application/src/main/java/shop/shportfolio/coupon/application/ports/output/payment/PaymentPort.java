package shop.shportfolio.coupon.application.ports.output.payment;

import shop.shportfolio.coupon.application.dto.payment.Payment;

public interface PaymentPort {

    Payment refund();

    Payment pay();

}
