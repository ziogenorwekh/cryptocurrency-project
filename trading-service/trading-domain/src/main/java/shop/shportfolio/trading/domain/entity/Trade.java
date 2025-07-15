package shop.shportfolio.trading.domain.entity;


import lombok.Getter;
import shop.shportfolio.common.domain.entity.BaseEntity;
import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.trading.domain.exception.TradingDomainException;
import shop.shportfolio.trading.domain.valueobject.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

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
    private FeeAmount feeAmount;
    private FeeRate feeRate;

    private Trade(TradeId tradeId, UserId userId, OrderId buyOrderId, OrderId sellOrderId,
                  OrderPrice orderPrice, Quantity quantity, TransactionType transactionType,
                  FeeAmount feeAmount, FeeRate feeRate) {
        setId(tradeId);
        this.userId = userId;
        this.buyOrderId = buyOrderId;
        this.sellOrderId = sellOrderId;
        this.orderPrice = orderPrice;
        this.quantity = quantity;
        this.createdAt = new CreatedAt(LocalDateTime.now(ZoneOffset.UTC));
        this.transactionType = transactionType;
        this.feeAmount = feeAmount;
        this.feeRate = feeRate;
    }

    public Trade(TradeId tradeId, UserId userId, OrderId buyOrderId, OrderId sellOrderId,
                 OrderPrice orderPrice, CreatedAt createdAt, Quantity quantity, TransactionType transactionType) {
        setId(tradeId);
        this.userId = userId;
        this.buyOrderId = buyOrderId;
        this.sellOrderId = sellOrderId;
        this.orderPrice = orderPrice;
        this.quantity = quantity;
        this.createdAt = createdAt;
        this.transactionType = transactionType;
    }

    public static Trade createTrade(TradeId tradeId, UserId userId, OrderId orderId,
                                    OrderPrice orderPrice, Quantity quantity,
                                    TransactionType transactionType, FeeAmount feeAmount, FeeRate feeRate) {
        if (transactionType.equals(TransactionType.TRADE_BUY)) {
            return new Trade(tradeId, userId, orderId, OrderId.anonymous(),
                    orderPrice, quantity, transactionType, feeAmount, feeRate);
        }
        if (transactionType.equals(TransactionType.TRADE_SELL)) {
            return new Trade(tradeId, userId, OrderId.anonymous(), orderId,
                    orderPrice, quantity, transactionType, feeAmount, feeRate);
        }
        throw new TradingDomainException("Invalid transaction type");
    }

    public Boolean isSellTrade() {
        return this.transactionType.equals(TransactionType.TRADE_SELL);
    }

    public Boolean isBuyTrade() {
        return this.transactionType.equals(TransactionType.TRADE_BUY);
    }
}
