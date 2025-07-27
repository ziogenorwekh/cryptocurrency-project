package shop.shportfolio.coupon.infrastructure.database.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.shportfolio.common.domain.valueobject.PaymentMethod;
import shop.shportfolio.common.domain.valueobject.PaymentStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "PAYMENT_ENTITY")
public class PaymentEntity {

    @Id
    @Column(name = "PAYMENT_ID", nullable = false, updatable = false, columnDefinition = "BINARY(16)")
    private UUID paymentId;

    @Column(name = "USER_ID", nullable = false, columnDefinition = "BINARY(16)")
    private UUID userId;

    @Column(name = "COUPON_ID", nullable = false, columnDefinition = "BINARY(16)")
    private UUID couponId;

    @Column(name = "PAYMENT_KEY", nullable = false, length = 100)
    private String paymentKey;

    @Column(name = "TOTAL_AMOUNT", nullable = false)
    private Long totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "PAYMENT_METHOD", nullable = false, length = 30)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false, length = 30)
    private PaymentStatus status;

    @Column(name = "REQUESTED_AT", nullable = false)
    private LocalDateTime requestedAt;

    @Column(name = "PAID_AT", nullable = false)
    private LocalDateTime paidAt;

    @Column(name = "DESCRIPTION", length = 255)
    private String description;

    @Column(name = "RAW_RESPONSE", columnDefinition = "TEXT")
    private String rawResponse;

    @Column(name = "CANCEL_REASON", length = 255)
    private String cancelReason;

    @Column(name = "CANCELLED_AT")
    private LocalDateTime cancelledAt;
}
