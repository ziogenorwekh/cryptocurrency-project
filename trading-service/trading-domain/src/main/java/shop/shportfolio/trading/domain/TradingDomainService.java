package shop.shportfolio.trading.domain;

import shop.shportfolio.common.domain.valueobject.MarketId;
import shop.shportfolio.common.domain.valueobject.UserId;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.MarketOrder;
import shop.shportfolio.trading.domain.entity.Order;
import shop.shportfolio.trading.domain.entity.ReservationOrder;
import shop.shportfolio.trading.domain.valueobject.*;

public interface TradingDomainService {


    void cancelOrder(Order order);

    LimitOrder createLimitOrder(UserId userId, MarketId marketId, OrderSide orderSide,
                                Quantity quantity, OrderPrice price, OrderType orderType);

    MarketOrder createMarketOrder(UserId userId, MarketId marketId, OrderSide orderSide,
                                  Quantity quantity, OrderPrice price, OrderType orderType);

    ReservationOrder createReservationOrder(UserId userId, MarketId marketId, OrderSide orderSide,
                                            Quantity quantity, OrderPrice orderPrice, OrderType orderType,
                                            TriggerCondition triggerCondition, ScheduledTime scheduledTime, ExpireAt expireAt,
                                            IsRepeatable isRepeatable);

    void applyTrade(Order order, Quantity executedQty);


    Boolean canMatchWith(Order order, Order targetOrder);

    Boolean isPriceMatch(Order checkOrder, OrderPrice orderPrice);

    Boolean isSellOrder(Order order);

    Boolean isBuyOrder(Order order);
}
