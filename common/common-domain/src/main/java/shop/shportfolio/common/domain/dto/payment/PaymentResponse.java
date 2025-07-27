package shop.shportfolio.common.domain.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import shop.shportfolio.common.domain.valueobject.PaymentMethod;
import shop.shportfolio.common.domain.valueobject.PaymentStatus;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
// 수정할 거 임시로 저장
public class PaymentResponse {

    private String paymentKey; // 결제의 키값
    private String orderId;               // 주문번호 (필수)
    private long totalAmount;             // 결제 금액
    private PaymentMethod method;         // 결제 수단 (Enum)
    private PaymentStatus status;       // 결제 처리 상태
    private LocalDateTime requestedAt; // 결제가 일어난 날짜와 시간 정보입니다
    private LocalDateTime paidAt;
    private String description; // 결제 설명
    private String rawResponse;

    @Override
    public String toString() {
        return "PaymentResponse{" +
                ", paymentKey='" + paymentKey + '\'' +
                ", orderId='" + orderId + '\'' +
                ", totalAmount=" + totalAmount +
                ", method=" + method +
                ", status=" + status +
                ", requestedAt=" + requestedAt +
                ", paidAt=" + paidAt +
                ", description='" + description + '\'' +
                ", rawResponse='" + rawResponse + '\'' +
                '}';
    }
}
