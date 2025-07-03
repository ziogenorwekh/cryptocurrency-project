package shop.shportfolio.coupon.application.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentRequest {

    private final long amount;
    private final String orderId;
    private final String paymentKey;
}
