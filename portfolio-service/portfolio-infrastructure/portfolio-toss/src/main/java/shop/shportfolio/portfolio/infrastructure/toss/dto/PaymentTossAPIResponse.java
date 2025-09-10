package shop.shportfolio.portfolio.infrastructure.toss.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import shop.shportfolio.common.domain.valueobject.PaymentStatus;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentTossAPIResponse {
        private String paymentKey; // 결제의 키값
        private String orderId;               // 주문번호 (필수)
        private long totalAmount;             // 결제 금액
        private String method;         // 결제 수단 (Enum)
        private PaymentStatus status;       // 결제 처리 상태
        private OffsetDateTime requestedAt; // 결제가 일어난 날짜와 시간 정보입니다
        private OffsetDateTime approvedAt;
        private String orderName; // 결제 설명
        private String rawResponse;


}
