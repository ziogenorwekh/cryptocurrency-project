package shop.shportfolio.coupon.infrastructure.database.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.shportfoilo.coupon.domain.valueobject.CouponStatus;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@Table(name = "COUPON_ENTITY")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponEntity {
    @Id
    @Column(name = "COUPON_ID", nullable = false, updatable = false, columnDefinition = "BINARY(16)")
    private UUID couponId;

    @Column(name = "USER_ID", nullable = false, updatable = false, columnDefinition = "BINARY(16)")
    private UUID userId;

    @Column(name = "FEE_DISCOUNT", nullable = false)
    private Integer feeDiscount;

    @Column(name = "EXPIRY_DATE", nullable = false)
    private LocalDate expiryDate;

    @Column(name = "ISSUED_AT", nullable = false)
    private LocalDate issuedAt;

    @Column(name = "COUPON_CODE", nullable = false, unique = true, length = 50)
    private String couponCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false, length = 20)
    private CouponStatus status;

}
