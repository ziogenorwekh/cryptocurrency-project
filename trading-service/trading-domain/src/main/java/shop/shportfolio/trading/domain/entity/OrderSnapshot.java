package shop.shportfolio.trading.domain.entity;

import shop.shportfolio.common.domain.valueobject.MarketId;
import shop.shportfolio.common.domain.valueobject.OrderPrice;
import shop.shportfolio.common.domain.valueobject.Quantity;
import shop.shportfolio.trading.domain.valueobject.OrderSide;
import shop.shportfolio.trading.domain.valueobject.OrderStatus;
import shop.shportfolio.trading.domain.valueobject.OrderType;

public class OrderSnapshot extends Order {

    private final OrderPrice orderPrice;

    private OrderSnapshot(OrderPrice orderPrice, MarketId marketId, Quantity quantity, OrderSide side) {
        super(null, marketId, side, quantity, null);
        this.orderPrice = orderPrice;
    }

    public static OrderSnapshot create(OrderPrice orderPrice,
                                       MarketId marketId, Quantity quantity, OrderSide side) {
        return new OrderSnapshot(orderPrice, marketId, quantity, side);
    }



    @Override
    public void validatePlaceable() {

    }

    @Override
    public Boolean isPriceMatch(OrderPrice targetPrice) {
        return null;
    }
}
