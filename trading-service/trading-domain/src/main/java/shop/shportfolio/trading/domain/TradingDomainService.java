package shop.shportfolio.trading.domain;

import shop.shportfolio.common.domain.valueobject.MarketId;
import shop.shportfolio.common.domain.valueobject.UserId;
import shop.shportfolio.trading.domain.entity.Order;
import shop.shportfolio.trading.domain.valueobject.*;

public interface TradingDomainService {


    void cancelOrder(Order order);

    Order createOrder(UserId userId, MarketId marketId, OrderSide orderSide,
                      Quantity quantity, OrderPrice price, OrderType orderType);


    void applyTrade(Order order, Quantity executedQty);
}
