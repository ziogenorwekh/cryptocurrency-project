package shop.shportfolio.portfolio.infrastructure.database.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.shportfolio.common.domain.valueobject.PaymentMethod;
import shop.shportfolio.common.domain.valueobject.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "PAYMENT_ENTITY")
public class PaymentEntity {

    @Id
    @Column(name = "PAYMENT_ID", unique = true, nullable = false, updatable = false, columnDefinition = "BINARY(16)")
    private UUID paymentId;

    @Column(name = "USER_ID", nullable = false, columnDefinition = "BINARY(16)")
    private UUID userId;

    @Column(name = "PAYMENT_KEY", nullable = false)
    private String paymentKey;

    @Column(name = "TOTAL_AMOUNT", precision = 19, scale = 4)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "PAYMENT_METHOD")
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    private PaymentStatus status;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "RAW_RESPONSE")
    private String rawResponse;

    @Column(name = "CANCEL_REASON")
    private String cancelReason;

    @Column(name = "CANCELLED_AT")
    private LocalDateTime cancelledAt;
}
