package shop.shportfolio.coupon.infrastructure.database.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@Builder
@Table(name = "COUPON_USAGE_ENTITY")
@NoArgsConstructor
@AllArgsConstructor
public class CouponUsageEntity {

    @Id
    @Column(name = "COUPON_USAGE_ID", nullable = false, updatable = false, columnDefinition = "BINARY(16)")
    private UUID couponUsageId;

    @Column(name = "USER_ID", nullable = false, updatable = false, columnDefinition = "BINARY(16)")
    private UUID userId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COUPON_ID", nullable = false, unique = true)
    private CouponEntity couponEntity;

    @Column(name = "ISSUED_AT", nullable = false)
    private LocalDate issuedAt;

    @Column(name = "USAGE_EXPIRY_DATE", nullable = false)
    private LocalDate usageExpiryDate;
}
