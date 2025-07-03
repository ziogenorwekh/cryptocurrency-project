package shop.shportfolio.coupon.application.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
// 수정할 거 임시로 저장
public class Payment {

    private String userId;          // 결제 요청자 ID (필요시)
    private String paymentKey; // 결제의 키값
    private PaymentType type;             // 결제 타입 (Enum)
    private String orderId;               // 주문번호 (필수)
    private String orderName;             // 구매 상품명
    private String mId;                   // 상점 ID
    private String currency;              // 통화 단위 ("KRW")
    private PaymentMethod method;         // 결제 수단 (Enum)
    private long totalAmount;             // 결제 금액
    private PaymentStatus status;       // 결제 처리 상태
    private LocalDateTime requestedAt; // 결제가 일어난 날짜와 시간 정보입니다
    private LocalDateTime approvedAt; // 결제 승인이 일어난 날짜와 시간 정보
    private String lastTransactionKey; // 마지막 거래의 키값입니다. 한 결제 건의 승인 거래와 취소 거래를 구분하는 데 사용
    private String paymentMethod;   // 결제 수단 (예: 카드, 페이팔 등)
    private String description;           // 결제 설명
}
