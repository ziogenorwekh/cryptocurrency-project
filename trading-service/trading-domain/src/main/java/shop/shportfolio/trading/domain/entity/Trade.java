package shop.shportfolio.trading.domain.entity;


import lombok.Getter;
import shop.shportfolio.common.domain.entity.BaseEntity;
import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.trading.domain.valueobject.*;

// 주문이 성공하면 기록되는 거래내역 엔티티
@Getter
public class Trade extends BaseEntity<TradeId> {

    private UserId userId;
    private OrderId buyOrderId;
    private OrderId sellOrderId;
    private OrderPrice orderPrice;
    private Quantity quantity;
    private CreatedAt createdAt;
    private TransactionType transactionType;

    public Trade(TradeId tradeId, UserId userId, OrderId buyOrderId, OrderId sellOrderId,
                 OrderPrice orderPrice, Quantity quantity, CreatedAt createdAt, TransactionType transactionType) {
        setId(tradeId);
        this.userId = userId;
        this.buyOrderId = buyOrderId;
        this.sellOrderId = sellOrderId;
        this.orderPrice = orderPrice;
        this.quantity = quantity;
        this.createdAt = createdAt;
        this.transactionType = transactionType;
    }

    public static Trade createMarketTrade(TradeId tradeId, UserId userId, OrderId buyOrderId,
                                          OrderPrice orderPrice, Quantity quantity, CreatedAt createdAt,TransactionType transactionType) {
        return new Trade(tradeId, userId, buyOrderId, OrderId.anonymous(), orderPrice, quantity, createdAt,transactionType);
    }

    public static Trade createLimitTrade(TradeId tradeId, UserId userId, OrderId buyOrderId, OrderId sellOrderId,
                                         OrderPrice orderPrice, Quantity quantity, CreatedAt createdAt,TransactionType transactionType) {
        return new Trade(tradeId, userId, buyOrderId, sellOrderId, orderPrice, quantity, createdAt,transactionType);
    }

    public static Trade createReservationTrade(TradeId tradeId, UserId userId, OrderId buyOrderId, OrderId sellOrderId,
                                         OrderPrice orderPrice, Quantity quantity, CreatedAt createdAt,TransactionType transactionType) {
        return new Trade(tradeId, userId, buyOrderId, sellOrderId, orderPrice, quantity, createdAt,transactionType);
    }
}
