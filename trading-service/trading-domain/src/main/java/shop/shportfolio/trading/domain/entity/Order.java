package shop.shportfolio.trading.domain.entity;

import shop.shportfolio.common.domain.entity.BaseEntity;
import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.trading.domain.valueobject.OrderSide;
import shop.shportfolio.trading.domain.valueobject.OrderStatus;

public abstract class Order extends BaseEntity<OrderId> {

    private UserId userId;
    private MarketId marketId;
    private OrderSide orderSide;
    private Quantity quantity;
    private CreatedAt createdAt;
    private OrderStatus orderStatus;
}
