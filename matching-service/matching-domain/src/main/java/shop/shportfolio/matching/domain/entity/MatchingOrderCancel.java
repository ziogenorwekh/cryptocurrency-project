package shop.shportfolio.matching.domain.entity;

import lombok.Builder;
import lombok.Getter;
import shop.shportfolio.common.domain.entity.BaseEntity;
import shop.shportfolio.common.domain.valueobject.OrderId;
import shop.shportfolio.common.domain.valueobject.UserId;
import shop.shportfolio.trading.domain.valueobject.OrderStatus;
import shop.shportfolio.trading.domain.valueobject.OrderType;

@Getter
public class MatchingOrderCancel extends BaseEntity<OrderId> {
    private final UserId userId;
    private final OrderType orderType;
    private final OrderStatus orderStatus;

    @Builder
    public MatchingOrderCancel(OrderId orderId, UserId userId, OrderType orderType, OrderStatus orderStatus) {
        setId(orderId);
        this.userId = userId;
        this.orderType = orderType;
        this.orderStatus = orderStatus;
    }

    @Override
    public String toString() {
        return "MatchingOrderCancel{" +
                "orderId=" + getId().getValue() +
                "userId=" + userId.getValue() +
                ", orderType=" + orderType.name() +
                ", orderStatus=" + orderStatus.name() +
                '}';
    }
}
