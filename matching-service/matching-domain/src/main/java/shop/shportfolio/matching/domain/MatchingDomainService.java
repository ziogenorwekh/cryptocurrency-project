package shop.shportfolio.matching.domain;

import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.matching.domain.event.MatchingOrderCancelDeletedEvent;
import shop.shportfolio.matching.domain.event.PredictedTradeCreatedEvent;
import shop.shportfolio.trading.domain.entity.Order;
import shop.shportfolio.trading.domain.valueobject.OrderStatus;
import shop.shportfolio.trading.domain.valueobject.OrderType;

import java.util.UUID;

public interface MatchingDomainService {

    PredictedTradeCreatedEvent createPredictedTrade(MarketId marketId , UserId userId,
                                                    Order matchOrder1, Order matchOrder2,
                                                    OrderPrice orderPrice, Quantity quantity,
                                                    TransactionType transactionType);


    MatchingOrderCancelDeletedEvent successfulDeleteOrder(Order order);

    MatchingOrderCancelDeletedEvent failedDeleteOrder(String orderId, UUID userId
            , OrderType orderType, OrderStatus orderStatus);
}
