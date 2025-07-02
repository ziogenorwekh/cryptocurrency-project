package shop.shportfolio.coupon.application.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefundCommand {
    private String transactionId;   // 원 결제 거래 ID (환불 대상)
    private long refundAmount;      // 환불 금액
    private String reason;           // 환불 사유
}
