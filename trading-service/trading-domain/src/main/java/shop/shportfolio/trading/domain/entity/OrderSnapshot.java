package shop.shportfolio.trading.domain.entity;

import lombok.Getter;
import shop.shportfolio.common.domain.entity.BaseEntity;
import shop.shportfolio.common.domain.valueobject.CreatedAt;
import shop.shportfolio.common.domain.valueobject.OrderId;
import shop.shportfolio.common.domain.valueobject.Quantity;

@Getter
public class OrderSnapshot extends BaseEntity<OrderId> {

    private final Quantity RemainingQuantity;
    private final CreatedAt createdAt;

    public OrderSnapshot(OrderId orderId,Quantity remainingQuantity, CreatedAt createdAt) {
        setId(orderId);
        RemainingQuantity = remainingQuantity;
        this.createdAt = createdAt;
    }
}
