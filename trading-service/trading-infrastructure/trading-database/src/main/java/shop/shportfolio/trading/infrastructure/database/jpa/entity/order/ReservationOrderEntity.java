package shop.shportfolio.trading.infrastructure.database.jpa.entity.order;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.shportfolio.trading.infrastructure.database.jpa.entity.order.valuetype.TriggerCondition;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "RESERVATION_ORDER")
@DiscriminatorValue("reservation")
@PrimaryKeyJoinColumn(name = "RESERVATION_ORDER_ID")
public class ReservationOrderEntity extends OrderEntity {

    @Embedded
    private TriggerCondition triggerCondition;

    @Column(name = "IS_REPEATABLE", nullable = false)
    private Boolean isRepeatable;

    @Column(name = "SCHEDULED_TIME", nullable = false)
    private LocalDateTime scheduledTime;

    @Column(name = "EXPIRE_AT", nullable = false)
    private LocalDateTime expireAt;

    public ReservationOrderEntity(String orderId,
                                  java.util.UUID userId,
                                  String marketId,
                                  String orderSide,
                                  java.math.BigDecimal quantity,
                                  java.math.BigDecimal price,
                                  java.math.BigDecimal remainingQuantity,
                                  shop.shportfolio.trading.domain.valueobject.OrderType orderType,
                                  shop.shportfolio.trading.domain.valueobject.OrderStatus orderStatus,
                                  java.time.LocalDateTime createdAt,
                                  TriggerCondition triggerCondition,
                                  Boolean isRepeatable,
                                  LocalDateTime scheduledTime,
                                  LocalDateTime expireAt) {
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
        this.triggerCondition = triggerCondition;
        this.isRepeatable = isRepeatable;
        this.scheduledTime = scheduledTime;
        this.expireAt = expireAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String orderId;
        private java.util.UUID userId;
        private String marketId;
        private String orderSide;
        private java.math.BigDecimal quantity;
        private java.math.BigDecimal price;
        private java.math.BigDecimal remainingQuantity;
        private shop.shportfolio.trading.domain.valueobject.OrderType orderType;
        private shop.shportfolio.trading.domain.valueobject.OrderStatus orderStatus;
        private java.time.LocalDateTime createdAt;

        private TriggerCondition triggerCondition;
        private Boolean isRepeatable;
        private LocalDateTime scheduledTime;
        private LocalDateTime expireAt;

        public Builder orderId(String orderId) {
            this.orderId = orderId;
            return this;
        }

        public Builder userId(java.util.UUID userId) {
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

        public Builder quantity(java.math.BigDecimal quantity) {
            this.quantity = quantity;
            return this;
        }

        public Builder price(java.math.BigDecimal price) {
            this.price = price;
            return this;
        }

        public Builder remainingQuantity(java.math.BigDecimal remainingQuantity) {
            this.remainingQuantity = remainingQuantity;
            return this;
        }

        public Builder orderType(shop.shportfolio.trading.domain.valueobject.OrderType orderType) {
            this.orderType = orderType;
            return this;
        }

        public Builder orderStatus(shop.shportfolio.trading.domain.valueobject.OrderStatus orderStatus) {
            this.orderStatus = orderStatus;
            return this;
        }

        public Builder createdAt(java.time.LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder triggerCondition(TriggerCondition triggerCondition) {
            this.triggerCondition = triggerCondition;
            return this;
        }

        public Builder isRepeatable(Boolean isRepeatable) {
            this.isRepeatable = isRepeatable;
            return this;
        }

        public Builder scheduledTime(LocalDateTime scheduledTime) {
            this.scheduledTime = scheduledTime;
            return this;
        }

        public Builder expireAt(LocalDateTime expireAt) {
            this.expireAt = expireAt;
            return this;
        }

        public ReservationOrderEntity build() {
            return new ReservationOrderEntity(orderId, userId, marketId, orderSide, quantity, price, remainingQuantity,
                    orderType, orderStatus, createdAt, triggerCondition, isRepeatable, scheduledTime, expireAt);
        }
    }
}
