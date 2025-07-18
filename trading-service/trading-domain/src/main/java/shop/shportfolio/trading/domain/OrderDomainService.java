package shop.shportfolio.trading.domain;

import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.trading.domain.entity.*;
import shop.shportfolio.trading.domain.entity.orderbook.OrderBook;
import shop.shportfolio.trading.domain.valueobject.*;

public interface OrderDomainService {


    void cancelOrder(Order order);

    LimitOrder createLimitOrder(UserId userId, MarketId marketId, OrderSide orderSide,
                                Quantity quantity, OrderPrice price, OrderType orderType);

    MarketOrder createMarketOrder(UserId userId, MarketId marketId, OrderSide orderSide,
                                  Quantity quantity, OrderType orderType);

    ReservationOrder createReservationOrder(UserId userId, MarketId marketId,
                                            OrderSide orderSide,
                                            Quantity quantity, OrderType orderType,
                                            TriggerCondition triggerCondition,
                                            ScheduledTime scheduledTime, ExpireAt expireAt,
                                            IsRepeatable isRepeatable);

    Boolean canMatchPrice(Order order, TickPrice counterPrice);

    Quantity applyOrder(Order order, Quantity executedQty);

    Boolean canMatchWith(Order order, Order targetOrder);

    Boolean isPriceMatch(Order checkOrder, OrderPrice orderPrice);

    Boolean isSellOrder(Order order);

    Boolean isBuyOrder(Order order);


    OrderBook addOrderbyOrderBook(OrderBook orderBook, LimitOrder order);

    void orderAppliedPartialFilled(Order order);

    Boolean isReservationOrderExecutable(ReservationOrder reservationOrder,OrderPrice currentPrice);

}
