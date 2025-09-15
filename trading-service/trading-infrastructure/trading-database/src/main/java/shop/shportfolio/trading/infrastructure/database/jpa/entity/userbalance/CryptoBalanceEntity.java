package shop.shportfolio.trading.infrastructure.database.jpa.entity.userbalance;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "CRYPTO_BALANCE")
@Getter
@EqualsAndHashCode
@NoArgsConstructor
public class CryptoBalanceEntity {

    @Id
    @Column(name = "BALANCE_ID" ,nullable = false)
    private String balanceId;

    @Column(name = "MARKET_ID", nullable = false)
    private String marketId;

    @Column(name = "USER_ID", columnDefinition = "BINARY(16)", nullable = false)
    private UUID userId;

    @Column(name = "PURCHASE_PRICE", precision = 19, scale = 8, nullable = false)
    private BigDecimal purchasePrice;

    @Column(name = "QUANTITY", precision = 19, scale = 8, nullable = false)
    private BigDecimal quantity;

    @Builder
    public CryptoBalanceEntity(String balanceId, String marketId, UUID userId,
                               BigDecimal purchasePrice, BigDecimal quantity) {
        this.balanceId = balanceId;
        this.marketId = marketId;
        this.userId = userId;
        this.purchasePrice = purchasePrice;
        this.quantity = quantity;
    }
}
