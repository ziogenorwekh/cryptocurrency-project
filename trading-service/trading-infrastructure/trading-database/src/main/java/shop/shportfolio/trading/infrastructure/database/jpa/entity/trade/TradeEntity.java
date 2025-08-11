package shop.shportfolio.trading.infrastructure.database.jpa.entity.trade;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import shop.shportfolio.common.domain.valueobject.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "TRADE")
@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class TradeEntity {

    @Id
    @Column(name = "TRADE_ID", columnDefinition = "BINARY(16)", nullable = false, unique = true)
    private UUID tradeId;

    @Column(name = "MARKET_ID",nullable = false)
    private String marketId;

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

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID tradeId;
        private String marketId;
        private UUID userId;
        private String buyOrderId;
        private String sellOrderId;
        private BigDecimal orderPrice;
        private BigDecimal quantity;
        private LocalDateTime createdAt;
        private TransactionType transactionType;
        private BigDecimal feeRate;
        private BigDecimal feeAmount;

        public Builder tradeId(UUID tradeId) {
            this.tradeId = tradeId;
            return this;
        }

        public Builder marketId(String marketId) {
            this.marketId = marketId;
            return this;
        }
        public Builder userId(UUID userId) {
            this.userId = userId;
            return this;
        }
        public Builder buyOrderId(String buyOrderId) {
            this.buyOrderId = buyOrderId;
            return this;
        }
        public Builder sellOrderId(String sellOrderId) {
            this.sellOrderId = sellOrderId;
            return this;
        }
        public Builder orderPrice(BigDecimal orderPrice) {
            this.orderPrice = orderPrice;
            return this;
        }
        public Builder quantity(BigDecimal quantity) {
            this.quantity = quantity;
            return this;
        }
        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }
        public Builder transactionType(TransactionType transactionType) {
            this.transactionType = transactionType;
            return this;
        }
        public Builder feeRate(BigDecimal feeRate) {
            this.feeRate = feeRate;
            return this;
        }
        public Builder feeAmount(BigDecimal feeAmount) {
            this.feeAmount = feeAmount;
            return this;
        }
        public TradeEntity build() {
            return new TradeEntity(tradeId, marketId, userId, buyOrderId, sellOrderId, orderPrice,
                    quantity, createdAt, transactionType, feeRate, feeAmount);
        }
    }
}
