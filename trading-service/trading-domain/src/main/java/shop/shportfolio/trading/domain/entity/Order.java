package shop.shportfolio.trading.domain.entity;

import shop.shportfolio.common.domain.entity.BaseEntity;
import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.trading.domain.valueobject.*;

public abstract class Order extends BaseEntity<OrderId> {

    private UserId userId;
    private MarketId marketId;
    private OrderSide orderSide; // buy, sell
    private Quantity quantity;
    private OrderPrice orderPrice;
    private OrderType orderType;
    private Quantity remainingQuantity;
    private CreatedAt createdAt;
    private OrderStatus orderStatus; // open, filled, cancelled
}
