package shop.shportfolio.common.domain.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentRefundRequest {
    private final String paymentKey;
    private final String cancelReason;
}
