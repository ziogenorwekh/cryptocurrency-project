package shop.shportfolio.trading.application.dto.order;

import lombok.Builder;
import lombok.Getter;
import shop.shportfolio.trading.domain.valueobject.OrderStatus;

import java.util.UUID;

@Getter
public class CancelOrderDto {

    private final String orderId;
    private final UUID userId;
    private final OrderStatus orderStatus;

    @Builder
    public CancelOrderDto(String orderId, UUID userId, OrderStatus orderStatus) {
        this.orderId = orderId;
        this.userId = userId;
        this.orderStatus = orderStatus;
    }
}
