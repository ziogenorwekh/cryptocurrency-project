package shop.shportfolio.user.database.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.shportfolio.common.domain.valueobject.TransactionType;

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

    @Column(name = "ORDER_ID", nullable = false)
    private String orderId;

    @Column(name = "USER_ID", nullable = false, columnDefinition = "BINARY(16)")
    private UUID userId;

    @Column(name = "MARKET_ID", nullable = false)
    private String marketId;

    @Column(name = "TRANSACTION_TYPE", nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @Column(name = "ORDER_PRICE", nullable = false, precision = 19, scale = 6)
    private BigDecimal orderPrice;

    @Column(name = "QUANTITY", nullable = false, precision = 19, scale = 6)
    private BigDecimal quantity;

    @Column(name = "TRANSACTION_TIME", nullable = false)
    private LocalDateTime transactionTime;
}