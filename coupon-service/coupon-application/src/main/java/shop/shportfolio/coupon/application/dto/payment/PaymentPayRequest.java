package shop.shportfolio.coupon.application.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentPayRequest {

    private final long amount;
    private final String orderId;
    private final String paymentKey;
}
