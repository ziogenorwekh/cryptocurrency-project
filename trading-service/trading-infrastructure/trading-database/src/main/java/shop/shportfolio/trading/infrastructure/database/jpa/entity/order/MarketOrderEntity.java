package shop.shportfolio.trading.infrastructure.database.jpa.entity.order;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.shportfolio.trading.domain.valueobject.OrderStatus;
import shop.shportfolio.trading.domain.valueobject.OrderType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Table(name = "MARKET_ORDER")
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("market")
@PrimaryKeyJoinColumn(name = "MARKET_ORDER_ID")
public class MarketOrderEntity extends OrderEntity {

    @Column(name = "REMAINING_PRICE", nullable = false, precision = 19, scale = 8)
    private BigDecimal remainingPrice;

    public MarketOrderEntity(String orderId, UUID userId, String marketId, String orderSide, BigDecimal quantity,
                             BigDecimal price, BigDecimal remainingQuantity, OrderType orderType,
                             OrderStatus orderStatus, LocalDateTime createdAt, BigDecimal remainingPrice) {
        this.orderId = orderId;
        this.userId = userId;
        this.marketId = marketId;
        this.orderSide = orderSide;
        this.quantity = quantity;
        this.price = price;
        this.remainingQuantity = remainingQuantity;
        this.orderType = orderType;
        this.orderStatus = orderStatus;
        this.createdAt = createdAt;
        this.remainingPrice = remainingPrice;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String orderId;
        private UUID userId;
        private String marketId;
        private String orderSide;
        private BigDecimal quantity;
        private BigDecimal price;
        private BigDecimal remainingQuantity;
        private OrderType orderType;
        private OrderStatus orderStatus;
        private LocalDateTime createdAt;
        private BigDecimal remainingPrice;

        public Builder orderId(String orderId) {
            this.orderId = orderId;
            return this;
        }

        public Builder userId(UUID userId) {
            this.userId = userId;
            return this;
        }

        public Builder marketId(String marketId) {
            this.marketId = marketId;
            return this;
        }

        public Builder orderSide(String orderSide) {
            this.orderSide = orderSide;
            return this;
        }

        public Builder quantity(BigDecimal quantity) {
            this.quantity = quantity;
            return this;
        }

        public Builder price(BigDecimal price) {
            this.price = price;
            return this;
        }

        public Builder remainingQuantity(BigDecimal remainingQuantity) {
            this.remainingQuantity = remainingQuantity;
            return this;
        }

        public Builder orderType(OrderType orderType) {
            this.orderType = orderType;
            return this;
        }

        public Builder orderStatus(OrderStatus orderStatus) {
            this.orderStatus = orderStatus;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder remainingPrice(BigDecimal remainingPrice) {
            this.remainingPrice = remainingPrice;
            return this;
        }

        public MarketOrderEntity build() {
            return new MarketOrderEntity(orderId, userId, marketId, orderSide, quantity, price, remainingQuantity, orderType, orderStatus, createdAt, remainingPrice);
        }
    }
}




