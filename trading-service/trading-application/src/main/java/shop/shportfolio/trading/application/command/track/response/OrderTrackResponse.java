package shop.shportfolio.trading.application.command.track.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import shop.shportfolio.trading.domain.valueobject.OrderSide;
import shop.shportfolio.trading.domain.valueobject.OrderStatus;
import shop.shportfolio.trading.domain.valueobject.OrderType;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class OrderTrackResponse {

    private final UUID userId;
    private final String orderId;
    private final String marketId;
    private final OrderSide orderSide;
    private final OrderStatus orderStatus;
    private String quantity;
    private String remainingQuantity;
    private String orderPrice;
    private final OrderType orderType;

    @Override
    public String toString() {
        return "OrderTrackResponse{" +
                "userId=" + userId +
                ", orderId='" + orderId + '\'' +
                ", marketId='" + marketId + '\'' +
                ", orderSide=" + orderSide +
                ", orderStatus=" + orderStatus +
                ", quantity=" + quantity +
                ", remainingQuantity=" + remainingQuantity +
                ", orderPrice=" + orderPrice +
                ", orderType=" + orderType +
                '}';
    }
}
