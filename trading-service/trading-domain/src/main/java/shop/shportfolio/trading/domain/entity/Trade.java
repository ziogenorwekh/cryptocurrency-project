package shop.shportfolio.trading.domain.entity;


import lombok.Getter;
import shop.shportfolio.common.domain.entity.BaseEntity;
import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.trading.domain.exception.TradingDomainException;
import shop.shportfolio.trading.domain.valueobject.*;

import java.time.LocalDateTime;

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

    private Trade(TradeId tradeId, UserId userId, OrderId buyOrderId, OrderId sellOrderId,
                 OrderPrice orderPrice, Quantity quantity, TransactionType transactionType) {
        setId(tradeId);
        this.userId = userId;
        this.buyOrderId = buyOrderId;
        this.sellOrderId = sellOrderId;
        this.orderPrice = orderPrice;
        this.quantity = quantity;
        this.createdAt = new CreatedAt(LocalDateTime.now());
        this.transactionType = transactionType;
    }

    public Trade(TradeId tradeId, UserId userId, OrderId buyOrderId, OrderId sellOrderId,
                  OrderPrice orderPrice, CreatedAt createdAt , Quantity quantity, TransactionType transactionType) {
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
                                    OrderPrice orderPrice, Quantity quantity, TransactionType transactionType) {
        if (transactionType.equals(TransactionType.TRADE_BUY)) {
            return new Trade(tradeId, userId, orderId, OrderId.anonymous(), orderPrice, quantity, transactionType);

        }
        if (transactionType.equals(TransactionType.TRADE_SELL)) {
            return new Trade(tradeId, userId, OrderId.anonymous(), orderId, orderPrice, quantity, transactionType);
        }
        throw new TradingDomainException("Invalid transaction type");
    }

    public Boolean isSellTrade() {
        return this.transactionType.equals(TransactionType.TRADE_SELL);
    }
}
