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
@Builder
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "DTYPE")
public abstract class OrderEntity {

    @Id
    @Column(unique = true, nullable = false)
    private String orderId;

    @Column(name = "USER_ID", nullable = false)
    private UUID userId;

    @Column(name = "MARKET_ID", nullable = false)
    private String marketId;

    @Column(name = "ORDER_SIDE", nullable = false)
    private String orderSide; // VO 대신 String 저장

    @Column(name = "QUANTITY",nullable = false, precision = 19, scale = 8)
    private BigDecimal quantity;

    @Column(name = "ORDER_PRICE",precision = 19, scale = 8)
    private BigDecimal price;

    @Column(name = "REMAINING_QUANTITY", precision = 19, scale = 8)
    private BigDecimal remainingQuantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "ORDER_TYPE", nullable = false)
    private OrderType orderType;

    @Enumerated(EnumType.STRING)
    @Column(name = "ORDER_STATUS", nullable = false)
    private OrderStatus orderStatus;

    @CreationTimestamp
    @Column(name = "CREATED_AT", updatable = false, nullable = false)
    private LocalDateTime createdAt;

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
