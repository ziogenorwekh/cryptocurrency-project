package shop.shportfolio.portfolio.infrastructure.database.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@DiscriminatorValue("CRYPTO")
@Table(name = "CRYPTO_BALANCE_ENTITY")
public class CryptoBalanceEntity extends BalanceEntity {

    @Column(name = "QUANTITY",precision = 19, scale = 4)
    private BigDecimal quantity;
    @Column(name = "PURCHASE_PRICE",precision = 19, scale = 4)
    private BigDecimal purchasePrice;

    public CryptoBalanceEntity(UUID balanceId, UUID portfolioId,
                               String marketId, LocalDateTime updatedAt,
                               BigDecimal quantity, BigDecimal purchasePrice) {
        super(balanceId, portfolioId, marketId, updatedAt);
        this.quantity = quantity;
        this.purchasePrice = purchasePrice;
    }

    public CryptoBalanceEntity() {
        super();
    }
}
