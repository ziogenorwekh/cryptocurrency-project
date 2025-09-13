package shop.shportfolio.portfolio.infrastructure.database.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "DTYPE")
public abstract class BalanceEntity {

    @Id
    @Column(name = "BALANCE_ID", unique = true, nullable = false, updatable = false, columnDefinition = "BINARY(16)")
    private UUID balanceId;
    @Column(name = "PORTFOLIO_ID",columnDefinition = "BINARY(16)")
    private UUID portfolioId;
    @Column(name = "MARKET_ID")
    private String marketId;
    @Temporal(value = TemporalType.TIMESTAMP)
    private LocalDateTime updatedAt;

    public BalanceEntity(UUID balanceId, UUID portfolioId, String marketId, LocalDateTime updatedAt) {
        this.balanceId = balanceId;
        this.portfolioId = portfolioId;
        this.marketId = marketId;
        this.updatedAt = updatedAt;
    }
}
