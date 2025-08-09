package shop.shportfolio.trading.infrastructure.database.jpa.entity.order;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import shop.shportfolio.trading.domain.valueobject.OrderStatus;
import shop.shportfolio.trading.domain.valueobject.OrderType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "DTYPE")
public abstract class OrderEntity {

    @Id
    @Column(unique = true, nullable = false)
    protected String orderId;

    @Column(name = "USER_ID", nullable = false)
    protected UUID userId;

    @Column(name = "MARKET_ID", nullable = false)
    protected String marketId;

    @Column(name = "ORDER_SIDE", nullable = false)
    protected String orderSide; // VO 대신 String 저장

    @Column(name = "QUANTITY",nullable = false, precision = 19, scale = 8)
    protected BigDecimal quantity;

    @Column(name = "ORDER_PRICE",precision = 19, scale = 8)
    protected BigDecimal price;

    @Column(name = "REMAINING_QUANTITY", precision = 19, scale = 8)
    protected BigDecimal remainingQuantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "ORDER_TYPE", nullable = false)
    protected OrderType orderType;

    @Enumerated(EnumType.STRING)
    @Column(name = "ORDER_STATUS", nullable = false)
    protected OrderStatus orderStatus;

    @CreationTimestamp
    @Column(name = "CREATED_AT", updatable = false, nullable = false)
    protected LocalDateTime createdAt;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        OrderEntity that = (OrderEntity) o;
        return Objects.equals(orderId, that.orderId) && Objects.equals(marketId, that.marketId) && Objects.equals(orderSide, that.orderSide) && Objects.equals(quantity, that.quantity) && Objects.equals(price, that.price) && Objects.equals(remainingQuantity, that.remainingQuantity) && orderType == that.orderType && orderStatus == that.orderStatus && Objects.equals(createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, marketId, orderSide, quantity, price, remainingQuantity, orderType, orderStatus, createdAt);
    }
}
