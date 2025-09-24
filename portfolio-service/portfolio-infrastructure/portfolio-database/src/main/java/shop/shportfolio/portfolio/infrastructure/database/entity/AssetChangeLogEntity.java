package shop.shportfolio.portfolio.infrastructure.database.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.shportfolio.common.domain.valueobject.TransactionStatus;
import shop.shportfolio.portfolio.domain.valueobject.ChangeType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ASSET_CHANGE_LOG_ENTITY")
public class AssetChangeLogEntity {

    @Id
    @Column(name = "CHANGE_LOG_ID", nullable = false, updatable = false, columnDefinition = "BINARY(16)")
    private UUID changeLogId;

    @Column(name = "PORTFOLIO_ID", nullable = false, columnDefinition = "BINARY(16)")
    private UUID portfolioId;

    @Column(name = "USER_ID", nullable = false, columnDefinition = "BINARY(16)")
    private UUID userId;

    @Column(name = "MARKET_ID", nullable = false)
    private String marketId;

    @Column(name = "CHANGE_MONEY", precision = 19, scale = 4)
    private BigDecimal changeMoney;

    @Enumerated(EnumType.STRING)
    @Column(name = "CHANGE_TYPE", nullable = false)
    private ChangeType changeType;

    @Enumerated(EnumType.STRING)
    @Column(name = "TRANSACTION_STATUS", nullable = false, columnDefinition = "varchar(255) default 'COMPLETED'")
    private TransactionStatus transactionStatus = TransactionStatus.COMPLETED;

    @Column(name = "DESCRIPTION")
    private String description;

    @Temporal(value = TemporalType.TIMESTAMP)
    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;

}
