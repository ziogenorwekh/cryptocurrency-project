package shop.shportfolio.matching.domain.entity;


import lombok.Builder;
import lombok.Getter;
import shop.shportfolio.common.domain.entity.AggregateRoot;
import shop.shportfolio.common.domain.entity.ViewEntity;
import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.common.domain.valueobject.TradeId;
import shop.shportfolio.trading.domain.valueobject.OrderType;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

// 이정도 거래가 될 것이다 라고 예측하는 인메모리 엔티티
@Getter
public class PredictedTrade extends ViewEntity<TradeId> {

    private final UserId userId;
    private final MarketId marketId;
    private final OrderId buyOrderId;
    private final OrderId sellOrderId;
    private final OrderPrice orderPrice;
    private final Quantity quantity;
    private final CreatedAt createdAt;
    private final TransactionType transactionType;
    private final OrderType buyOrderType;
    private final OrderType sellOrderType;

    @Builder
    public PredictedTrade(TradeId tradeId, MarketId marketId, UserId userId, OrderId buyOrderId, OrderId sellOrderId,
                           OrderPrice orderPrice, Quantity quantity, TransactionType transactionType,
                           OrderType buyOrderType, OrderType sellOrderType) {
        setId(tradeId);
        this.userId = userId;
        this.marketId = marketId;
        this.buyOrderId = buyOrderId;
        this.sellOrderId = sellOrderId;
        this.orderPrice = orderPrice;
        this.quantity = quantity;
        this.createdAt = new CreatedAt(LocalDateTime.now(ZoneOffset.UTC));
        this.transactionType = transactionType;
        this.buyOrderType = buyOrderType;
        this.sellOrderType = sellOrderType;
    }


    public static PredictedTrade createTrade(MarketId marketId, UserId userId,
                                             OrderId buyOrderId, OrderId sellOrderId,
                                             OrderPrice orderPrice, Quantity quantity,
                                             TransactionType transactionType,
                                             OrderType buyOrderType, OrderType sellOrderType) {
        TradeId tradeId = new TradeId(UUID.randomUUID());
        PredictedTrade predictedTrade = new PredictedTrade(tradeId, marketId, userId, buyOrderId,
                sellOrderId, orderPrice, quantity, transactionType, buyOrderType, sellOrderType);
        return predictedTrade;
    }

    @Override
    public String toString() {
        return "Trade{" +
                "userId=" + userId.getValue() +
                ", marketId=" + marketId.getValue() +
                ", orderPrice=" + orderPrice.getValue() +
                ",\n quantity=" + quantity.getValue() +
                ", createdAt=" + createdAt.getValue() +
                ", transactionType=" + transactionType.name() +
                ", sellOrderId=" + sellOrderId.getValue() +
                ", buyOrderId=" + buyOrderId.getValue() +
                ", buyOrderType=" + buyOrderType.name() +
                ", sellOrderType=" + sellOrderType.name() +
                '}';
    }
}
