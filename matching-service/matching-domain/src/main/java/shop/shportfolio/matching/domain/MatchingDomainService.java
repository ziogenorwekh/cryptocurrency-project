package shop.shportfolio.matching.domain;

import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.matching.domain.event.PredictedTradeCreatedEvent;

public interface MatchingDomainService {

    PredictedTradeCreatedEvent createPredictedTrade(MarketId marketId , UserId userId,
                                                    OrderId buyOrderId, OrderId sellOrderId,
                                                    OrderPrice orderPrice, Quantity quantity,
                                                    TransactionType transactionType);
}
