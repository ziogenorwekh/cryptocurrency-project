package shop.shportfolio.trading.infrastructure.database.jpa.entity.userbalance;

import jakarta.persistence.*;
import lombok.*;
import shop.shportfolio.common.domain.valueobject.AssetCode;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "USER_BALANCE")
@Getter
@AllArgsConstructor
@EqualsAndHashCode
@NoArgsConstructor
public class UserBalanceEntity {

    @Id
    @Column(name = "USER_BALANCE_ID", columnDefinition = "BINARY(16)", nullable = false, unique = true)
    private UUID userBalanceId;

    @Column(name = "USER_ID", columnDefinition = "BINARY(16)", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "ASSET_CODE", nullable = false)
    private AssetCode assetCode;

    @Column(name = "MONEY", precision = 19, scale = 8, nullable = false)
    private BigDecimal money;

    @OneToMany(mappedBy = "userBalance", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.LAZY)
    private List<LockBalanceEntity> lockBalances = new ArrayList<>();

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID userBalanceId;
        private UUID userId;
        private AssetCode assetCode;
        private BigDecimal money;
        private List<LockBalanceEntity> lockBalances;
        public Builder userBalanceId(UUID userBalanceId) {
            this.userBalanceId = userBalanceId;
            return this;
        }
        public Builder userId(UUID userId) {
            this.userId = userId;
            return this;
        }
        public Builder assetCode(AssetCode assetCode) {
            this.assetCode = assetCode;
            return this;
        }
        public Builder money(BigDecimal money) {
            this.money = money;
            return this;
        }
        public Builder lockBalances(List<LockBalanceEntity> lockBalances) {
            this.lockBalances = lockBalances;
            return this;
        }
        public UserBalanceEntity build() {
            return new UserBalanceEntity(userBalanceId, userId, assetCode, money, lockBalances);
        }
    }
}
