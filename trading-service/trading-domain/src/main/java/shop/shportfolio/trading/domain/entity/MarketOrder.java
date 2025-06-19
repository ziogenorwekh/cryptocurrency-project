package shop.shportfolio.trading.domain.entity;

import shop.shportfolio.common.domain.valueobject.MarketId;
import shop.shportfolio.common.domain.valueobject.UserId;
import shop.shportfolio.trading.domain.valueobject.OrderPrice;
import shop.shportfolio.trading.domain.valueobject.OrderSide;
import shop.shportfolio.trading.domain.valueobject.OrderType;
import shop.shportfolio.trading.domain.valueobject.Quantity;


// 시장가 주문
public class MarketOrder extends Order {
    public MarketOrder(UserId userId, MarketId marketId, OrderSide orderSide,
                       Quantity quantity, OrderPrice orderPrice, OrderType orderType) {
        super(userId, marketId, orderSide, quantity, orderPrice, orderType);
    }
}
