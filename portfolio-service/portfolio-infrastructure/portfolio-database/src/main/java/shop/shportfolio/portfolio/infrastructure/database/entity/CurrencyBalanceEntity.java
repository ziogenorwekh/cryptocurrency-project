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
@DiscriminatorValue("CURRENCY")
@Table(name = "CURRENCY_BALANCE_ENTITY")
public class CurrencyBalanceEntity extends BalanceEntity {

    @Column(name = "USER_ID", unique = true, nullable = false, updatable = false, columnDefinition = "BINARY(16)")
    private UUID userId;
    @Column(name = "MONEY", precision = 19, scale = 4)
    private BigDecimal money;


    public CurrencyBalanceEntity(UUID balanceId, UUID portfolioId, String marketId,
                                 LocalDateTime updatedAt, UUID userId, BigDecimal money) {
        super(balanceId, portfolioId, marketId, updatedAt);
        this.userId = userId;
        this.money = money;
    }

    public CurrencyBalanceEntity() {
        super();
    }
}
