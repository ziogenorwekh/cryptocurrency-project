package shop.shportfolio.trading.infrastructure.database.jpa.entity.trade;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.shportfolio.common.domain.valueobject.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "TRADE")
@Getter
@EqualsAndHashCode
@NoArgsConstructor
public class TradeEntity {

    @Id
    @Column(name = "TRADE_ID", columnDefinition = "BINARY(16)", nullable = false, unique = true)
    private UUID tradeId;

    @Column(name = "USER_ID", columnDefinition = "BINARY(16)", nullable = false)
    private UUID userId;

    @Column(name = "BUY_ORDER_ID", nullable = false)
    private String buyOrderId;

    @Column(name = "SELL_ORDER_ID", nullable = false)
    private String sellOrderId;

    @Column(name = "ORDER_PRICE", precision = 19, scale = 8, nullable = false)
    private BigDecimal orderPrice;

    @Column(name = "QUANTITY", precision = 19, scale = 8, nullable = false)
    private BigDecimal quantity;

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "TRANSACTION_TYPE", nullable = false)
    private TransactionType transactionType;

    @Column(name = "FEE_RATE", precision = 10, scale = 8, nullable = false)
    private BigDecimal feeRate;

    @Column(name = "FEE_AMOUNT", precision = 19, scale = 8, nullable = false)
    private BigDecimal feeAmount;

}
