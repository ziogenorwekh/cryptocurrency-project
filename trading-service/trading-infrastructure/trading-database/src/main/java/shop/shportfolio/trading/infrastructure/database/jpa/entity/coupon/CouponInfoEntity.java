package shop.shportfolio.trading.infrastructure.database.jpa.entity.coupon;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "COUPON_INFO")
@Getter
@EqualsAndHashCode
@NoArgsConstructor
public class CouponInfoEntity {

    @Id
    @Column(name = "COUPON_ID", columnDefinition = "BINARY(16)", nullable = false, unique = true)
    private UUID couponId;

    @Column(name = "USER_ID", columnDefinition = "BINARY(16)", nullable = false)
    private UUID userId;

    @Column(name = "FEE_DISCOUNT", nullable = false)
    private Integer feeDiscount;

    @Column(name = "ISSUED_AT", nullable = false)
    private LocalDate issuedAt;

    @Column(name = "USAGE_EXPIRY_DATE", nullable = false)
    private LocalDate usageExpiryDate;

}