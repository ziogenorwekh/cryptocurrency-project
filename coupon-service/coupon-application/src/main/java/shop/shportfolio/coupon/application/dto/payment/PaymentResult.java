package shop.shportfolio.coupon.application.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PaymentResult {

    private final boolean success;
    private final String paymentId;
    private final String status;
    private final String message;
    private final LocalDateTime approvedAt;
    private final long amount;          // 처리 금액
}