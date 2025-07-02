package shop.shportfolio.coupon.application.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCommand {

    private String userId;          // 결제 요청자 ID (필요시)
    private String paymentMethod;   // 결제 수단 (예: 카드, 페이팔 등)
    private String orderId;               // 주문번호 (필수)
    private String orderName;             // 구매 상품명
    private String mId;                   // 상점 ID
    private String currency;              // 통화 단위 ("KRW")
    private PaymentType type;             // 결제 타입 (Enum)
    private PaymentMethod method;         // 결제 수단 (Enum)
    private long totalAmount;             // 결제 금액
    private String description;           // 결제 설명
}
