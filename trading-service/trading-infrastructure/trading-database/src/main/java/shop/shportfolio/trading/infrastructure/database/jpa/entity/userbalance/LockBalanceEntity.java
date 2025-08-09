package shop.shportfolio.trading.infrastructure.database.jpa.entity.userbalance;

import jakarta.persistence.*;
import lombok.*;
import shop.shportfolio.trading.domain.valueobject.LockStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "LOCK_BALANCE")
@Getter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class LockBalanceEntity {

    @Id
    @Column(name = "ORDER_ID", nullable = false)
    private String orderId;

    @Column(name = "USER_ID", columnDefinition = "BINARY(16)", nullable = false)
    private UUID userId;

    @Column(name = "LOCKED_AMOUNT", precision = 19, scale = 8, nullable = false)
    private BigDecimal lockedAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "LOCK_STATUS", nullable = false)
    private LockStatus lockStatus;

    @Column(name = "LOCKED_AT", nullable = false)
    private LocalDateTime lockedAt;


    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String orderId;
        private UUID userId;
        private BigDecimal lockedAmount;
        private LockStatus lockStatus;
        private LocalDateTime lockedAt;
        public Builder orderId(String orderId) {
            this.orderId = orderId;
            return this;
        }
        public Builder userId(UUID userId) {
            this.userId = userId;
            return this;
        }
        public Builder lockedAmount(BigDecimal lockedAmount) {
            this.lockedAmount = lockedAmount;
            return this;
        }
        public Builder lockStatus(LockStatus lockStatus) {
            this.lockStatus = lockStatus;
            return this;
        }
        public Builder lockedAt(LocalDateTime lockedAt) {
            this.lockedAt = lockedAt;
            return this;
        }
        public LockBalanceEntity build() {
            return new LockBalanceEntity(orderId, userId, lockedAmount, lockStatus, lockedAt);
        }
    }
}