package shop.shportfolio.trading.application.command.track.response;

import lombok.Getter;
import shop.shportfolio.trading.domain.valueobject.OrderSide;
import shop.shportfolio.trading.domain.valueobject.OrderStatus;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
public class LimitOrderTrackResponse {


    private final UUID userId;
    private final String marketId;
    private final OrderSide orderSide;
    private final OrderStatus orderStatus;
    private final BigDecimal remainingQuantity;
    private final BigDecimal orderPrice;

    public LimitOrderTrackResponse(UUID userId, String marketId, OrderSide orderSide, OrderStatus orderStatus, BigDecimal remainingQuantity, BigDecimal orderPrice) {
        this.userId = userId;
        this.marketId = marketId;
        this.orderSide = orderSide;
        this.orderStatus = orderStatus;
        this.remainingQuantity = remainingQuantity;
        this.orderPrice = orderPrice;
    }
}
