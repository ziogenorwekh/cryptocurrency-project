package shop.shportfolio.trading.domain.entity;

import shop.shportfolio.common.domain.valueobject.MarketId;
import shop.shportfolio.common.domain.valueobject.UserId;
import shop.shportfolio.trading.domain.valueobject.*;

// 지정가 주문
public class LimitOrder extends Order {

    private LimitPrice limitPrice;

    public LimitOrder(UserId userId, MarketId marketId, OrderSide orderSide,
                      Quantity quantity, OrderPrice orderPrice, OrderType orderType) {
        super(userId, marketId, orderSide, quantity, orderPrice, orderType);
    }
}
