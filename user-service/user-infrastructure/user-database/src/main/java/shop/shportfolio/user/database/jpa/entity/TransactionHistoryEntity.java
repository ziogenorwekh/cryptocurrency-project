package shop.shportfolio.user.database.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "TRANSACTION_HISTORY_ENTITY")
public class TransactionHistoryEntity {

    @Id
    @Column(name = "TRANSACTION_ID", nullable = false, updatable = false, columnDefinition = "BINARY(16)")
    private UUID transactionId;

    @Column(name = "USER_ID", nullable = false, columnDefinition = "BINARY(16)")
    private UUID userId;

    @Column(name = "MARKET_ID", nullable = false)
    private String marketId;

    @Column(name = "TRANSACTION_TYPE", nullable = false)
    private String transactionType;

    @Column(name = "AMOUNT", nullable = false, precision = 19, scale = 6)
    private BigDecimal amount;

    @Column(name = "TRANSACTION_TIME", nullable = false)
    private LocalDateTime transactionTime;
}
