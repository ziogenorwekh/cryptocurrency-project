package shop.shportfolio.trading.domain;

import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.trading.domain.entity.*;
import shop.shportfolio.trading.domain.event.TradingRecordedEvent;
import shop.shportfolio.trading.domain.valueobject.*;

public interface TradingDomainService {


    void cancelOrder(Order order);

    LimitOrder createLimitOrder(UserId userId, MarketId marketId, OrderSide orderSide,
                                Quantity quantity, OrderPrice price, OrderType orderType);

    MarketOrder createMarketOrder(UserId userId, MarketId marketId, OrderSide orderSide,
                                  Quantity quantity, OrderType orderType);

    MarketItem createMarketItem(String marketId, MarketKoreanName marketKoreanName, MarketEnglishName marketEnglishName,
                                MarketWarning marketWarning, TickPrice tickPrice,MarketStatus marketStatus);

    ReservationOrder createReservationOrder(UserId userId, MarketId marketId,
                                            OrderSide orderSide,
                                            Quantity quantity, OrderType orderType,
                                            TriggerCondition triggerCondition,
                                            ScheduledTime scheduledTime, ExpireAt expireAt,
                                            IsRepeatable isRepeatable);

    TradingRecordedEvent createTrade(TradeId tradeId, UserId userId, OrderId orderId,
                                     OrderPrice orderPrice, Quantity quantity,
                                     TransactionType transactionType,FeeAmount feeAmount, FeeRate feeRate);

    Boolean canMatchPrice(Order order, TickPrice counterPrice);

    Quantity applyOrder(Order order, Quantity executedQty);

    Boolean canMatchWith(Order order, Order targetOrder);

    Boolean isPriceMatch(Order checkOrder, OrderPrice orderPrice);

    Boolean isSellOrder(Order order);

    Boolean isBuyOrder(Order order);


    OrderBook addOrderbyOrderBook(OrderBook orderBook, LimitOrder order);

    void applyExecutedTrade(OrderBook orderBook, Trade trade);

    CouponInfo createCouponInfo(CouponId couponId, UserId userId, FeeDiscount feeDiscount,
                                IssuedAt issuedAt, UsageExpiryDate usageExpiryDate);

    void orderAppliedPartialFilled(Order order);

    Boolean isReservationOrderExecutable(ReservationOrder reservationOrder,OrderPrice currentPrice);

}
