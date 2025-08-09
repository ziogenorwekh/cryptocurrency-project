package shop.shportfolio.trading.infrastructure.database.jpa.entity.coupon;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "COUPON_INFO")
@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
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


    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID couponId;
        private UUID userId;
        private Integer feeDiscount;
        private LocalDate issuedAt;
        private LocalDate usageExpiryDate;

        public Builder couponId(UUID couponId) {
            this.couponId = couponId;
            return this;
        }
        public Builder userId(UUID userId) {
            this.userId = userId;
            return this;
        }
        public Builder feeDiscount(Integer feeDiscount) {
            this.feeDiscount = feeDiscount;
            return this;
        }
        public Builder issuedAt(LocalDate issuedAt) {
            this.issuedAt = issuedAt;
            return this;
        }
        public Builder usageExpiryDate(LocalDate usageExpiryDate) {
            this.usageExpiryDate = usageExpiryDate;
            return this;
        }
        public CouponInfoEntity build() {
            return new CouponInfoEntity(couponId, userId,
                    feeDiscount, issuedAt, usageExpiryDate);
        }
    }
}