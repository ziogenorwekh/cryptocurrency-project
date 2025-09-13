package shop.shportfolio.matching.application.dto.order;

import lombok.Getter;
import shop.shportfolio.trading.domain.valueobject.OrderStatus;
import shop.shportfolio.trading.domain.valueobject.OrderType;

import java.util.UUID;

@Getter
public class OrderCancelKafkaResponse {
    private final String orderId;
    private final UUID userId;
    private final OrderType orderType;
    private final OrderStatus orderStatus;

    public OrderCancelKafkaResponse(String orderId, UUID userId,
                                    OrderType orderType, OrderStatus orderStatus) {
        this.orderId = orderId;
        this.userId = userId;
        this.orderType = orderType;
        this.orderStatus = orderStatus;
    }
}
