package shop.shportfolio.matching.domain;

import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.matching.domain.event.PredictedTradeCreatedEvent;
import shop.shportfolio.trading.domain.entity.Order;

public interface MatchingDomainService {

    PredictedTradeCreatedEvent createPredictedTrade(MarketId marketId , UserId userId,
                                                    Order matchOrder1, Order matchOrder2,
                                                    OrderPrice orderPrice, Quantity quantity,
                                                    TransactionType transactionType);
}
