package shop.shportfolio.matching.domain;

import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.matching.domain.entity.MatchingOrderCancel;
import shop.shportfolio.matching.domain.entity.PredictedTrade;
import shop.shportfolio.matching.domain.event.MatchingOrderCancelDeletedEvent;
import shop.shportfolio.matching.domain.event.PredictedTradeCreatedEvent;
import shop.shportfolio.trading.domain.entity.Order;
import shop.shportfolio.trading.domain.valueobject.OrderStatus;
import shop.shportfolio.trading.domain.valueobject.OrderType;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.UUID;

public class MatchingDomainServiceImpl implements MatchingDomainService {

    @Override
    public PredictedTradeCreatedEvent createPredictedTrade(MarketId marketId, UserId userId,
                                                           Order matchOrder1, Order matchOrder2,
                                                           OrderPrice orderPrice, Quantity quantity,
                                                           TransactionType transactionType) {

        OrderId buyOrderId = matchOrder1.isBuyOrder() ? matchOrder1.getId() : matchOrder2.getId();
        OrderId sellOrderId = matchOrder1.isBuyOrder() ? matchOrder2.getId() : matchOrder1.getId();
        OrderType buyOrderType = matchOrder1.isBuyOrder() ? matchOrder1.getOrderType() : matchOrder2.getOrderType();
        OrderType sellOrderType = matchOrder1.isBuyOrder() ? matchOrder2.getOrderType() : matchOrder1.getOrderType();
        PredictedTrade predictedTrade = PredictedTrade.createTrade(marketId, userId,
                buyOrderId, sellOrderId, orderPrice, quantity, transactionType, buyOrderType, sellOrderType);
        return new PredictedTradeCreatedEvent(predictedTrade, MessageType.CREATE, ZonedDateTime.now(ZoneOffset.UTC));
    }

    @Override
    public MatchingOrderCancelDeletedEvent successfulDeleteOrder(Order order) {
        MatchingOrderCancel matchingOrderCancel = MatchingOrderCancel.builder()
                .orderId(order.getId())
                .userId(order.getUserId())
                .orderType(order.getOrderType())
                .orderStatus(order.getOrderStatus())
                .build();
        return new MatchingOrderCancelDeletedEvent(matchingOrderCancel,MessageType.DELETE
                ,ZonedDateTime.now(ZoneOffset.UTC));
    }

    @Override
    public MatchingOrderCancelDeletedEvent failedDeleteOrder(String orderId, UUID userId
    ,OrderType orderType, OrderStatus orderStatus) {
        MatchingOrderCancel matchingOrderCancel = MatchingOrderCancel.builder()
                .orderId(new OrderId(orderId))
                .userId(new UserId(userId))
                .orderType(orderType)
                .orderStatus(orderStatus)
                .build();
        return new MatchingOrderCancelDeletedEvent(matchingOrderCancel,MessageType.FAIL
                ,ZonedDateTime.now(ZoneOffset.UTC));
    }
}
