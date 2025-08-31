package shop.shportfolio.matching.domain.entity;


import lombok.Builder;
import lombok.Getter;
import shop.shportfolio.common.domain.entity.AggregateRoot;
import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.common.domain.valueobject.TradeId;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

// 이정도 거래가 될 것이다 라고 예측하는 인메모리 엔티티
@Getter
public class PredictedTrade extends AggregateRoot<TradeId> {

    private UserId userId;
    private MarketId marketId;
    private OrderId buyOrderId;
    private OrderId sellOrderId;
    private OrderPrice orderPrice;
    private Quantity quantity;
    private CreatedAt createdAt;
    private TransactionType transactionType;

    private PredictedTrade(TradeId tradeId, MarketId marketId, UserId userId, OrderId buyOrderId, OrderId sellOrderId,
                           OrderPrice orderPrice, Quantity quantity, TransactionType transactionType) {
        setId(tradeId);
        this.userId = userId;
        this.marketId = marketId;
        this.buyOrderId = buyOrderId;
        this.sellOrderId = sellOrderId;
        this.orderPrice = orderPrice;
        this.quantity = quantity;
        this.createdAt = new CreatedAt(LocalDateTime.now(ZoneOffset.UTC));
        this.transactionType = transactionType;
    }

    @Builder
    public PredictedTrade(TradeId tradeId, MarketId marketId, UserId userId, OrderId buyOrderId, OrderId sellOrderId,
                          OrderPrice orderPrice, Quantity quantity, TransactionType transactionType, CreatedAt createdAt) {
        setId(tradeId);
        this.userId = userId;
        this.marketId = marketId;
        this.buyOrderId = buyOrderId;
        this.sellOrderId = sellOrderId;
        this.orderPrice = orderPrice;
        this.quantity = quantity;
        this.createdAt = createdAt;
        this.transactionType = transactionType;
    }

    public static PredictedTrade createTrade(MarketId marketId, UserId userId,
                                             OrderId buyOrderId, OrderId sellOrderId,
                                             OrderPrice orderPrice, Quantity quantity,
                                             TransactionType transactionType) {
        TradeId tradeId = new TradeId(UUID.randomUUID());
        PredictedTrade predictedTrade = new PredictedTrade(tradeId, marketId, userId, buyOrderId,
                sellOrderId, orderPrice, quantity, transactionType);
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
                '}';
    }
}
