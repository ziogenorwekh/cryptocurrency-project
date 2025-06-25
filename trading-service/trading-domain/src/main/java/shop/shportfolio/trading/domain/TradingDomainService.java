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

    MarketItem createMarketItem(String marketId, MarketKoreanName marketKoreanName
            , MarketEnglishName marketEnglishName, MarketWarning marketWarning);

    ReservationOrder createReservationOrder(UserId userId, MarketId marketId, OrderSide orderSide,
                                            Quantity quantity, OrderPrice orderPrice, OrderType orderType,
                                            TriggerCondition triggerCondition, ScheduledTime scheduledTime, ExpireAt expireAt,
                                            IsRepeatable isRepeatable);

    TradingRecordedEvent createMarketTrade(TradeId tradeId, UserId userId, OrderId buyOrderId,
                                           OrderPrice orderPrice, Quantity quantity, CreatedAt createdAt,
                                           TransactionType transactionType);

    Boolean applyTrade(Order order, Quantity executedQty);

    Boolean canMatchWith(Order order, Order targetOrder);

    Boolean isPriceMatch(Order checkOrder, OrderPrice orderPrice);

    Boolean isSellOrder(Order order);

    Boolean isBuyOrder(Order order);
}
