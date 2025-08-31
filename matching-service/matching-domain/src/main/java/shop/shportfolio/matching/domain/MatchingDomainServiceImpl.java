package shop.shportfolio.matching.domain;

import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.matching.domain.entity.PredictedTrade;
import shop.shportfolio.matching.domain.event.PredictedTradeCreatedEvent;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class MatchingDomainServiceImpl implements MatchingDomainService {

    @Override
    public PredictedTradeCreatedEvent createPredictedTrade(MarketId marketId, UserId userId,
                                                           OrderId buyOrderId, OrderId sellOrderId,
                                                           OrderPrice orderPrice, Quantity quantity,
                                                           TransactionType transactionType) {
        PredictedTrade predictedTrade = PredictedTrade.createTrade(marketId, userId,
                buyOrderId, sellOrderId, orderPrice, quantity, transactionType);
        return new PredictedTradeCreatedEvent(predictedTrade, MessageType.CREATE, ZonedDateTime.now(ZoneOffset.UTC));
    }
}
