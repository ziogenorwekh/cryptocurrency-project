package shop.shportfolio.trading.domain.entity.trade;


import lombok.Builder;
import lombok.Getter;
import shop.shportfolio.common.domain.entity.AggregateRoot;
import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.trading.domain.exception.TradingDomainException;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

// 주문이 성공하면 기록되는 거래내역 엔티티
@Getter

public class Trade extends AggregateRoot<TradeId> {

    private UserId userId;
    private MarketId marketId;
    private OrderId buyOrderId;
    private OrderId sellOrderId;
    private OrderPrice orderPrice;
    private Quantity quantity;
    private CreatedAt createdAt;
    private TransactionType transactionType;
    private FeeAmount feeAmount;
    private FeeRate feeRate;

    private Trade(TradeId tradeId,MarketId marketId, UserId userId, OrderId buyOrderId, OrderId sellOrderId,
                  OrderPrice orderPrice, Quantity quantity, TransactionType transactionType,
                  FeeAmount feeAmount, FeeRate feeRate) {
        setId(tradeId);
        this.userId = userId;
        this.marketId = marketId;
        this.buyOrderId = buyOrderId;
        this.sellOrderId = sellOrderId;
        this.orderPrice = orderPrice;
        this.quantity = quantity;
        this.createdAt = new CreatedAt(LocalDateTime.now(ZoneOffset.UTC));
        this.transactionType = transactionType;
        this.feeAmount = feeAmount;
        this.feeRate = feeRate;
    }

    @Builder
    public Trade(TradeId tradeId,MarketId marketId, UserId userId, OrderId buyOrderId, OrderId sellOrderId,
                 OrderPrice orderPrice, Quantity quantity, TransactionType transactionType, CreatedAt createdAt,
                 FeeAmount feeAmount, FeeRate feeRate) {
        setId(tradeId);
        this.userId = userId;
        this.marketId = marketId;
        this.buyOrderId = buyOrderId;
        this.sellOrderId = sellOrderId;
        this.orderPrice = orderPrice;
        this.quantity = quantity;
        this.createdAt = createdAt;
        this.transactionType = transactionType;
        this.feeAmount = feeAmount;
        this.feeRate = feeRate;
    }

    public static Trade createTrade(TradeId tradeId, MarketId marketId ,UserId userId, OrderId orderId,
                                    OrderPrice orderPrice, Quantity quantity,
                                    TransactionType transactionType, FeeAmount feeAmount, FeeRate feeRate) {
        if (transactionType.equals(TransactionType.TRADE_BUY)) {
            return new Trade(tradeId,marketId, userId, orderId, OrderId.anonymous(),
                    orderPrice, quantity, transactionType, feeAmount, feeRate);
        }
        if (transactionType.equals(TransactionType.TRADE_SELL)) {
            return new Trade(tradeId, marketId, userId, OrderId.anonymous(), orderId,
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

    @Override
    public String toString() {
        return "Trade{" +
                "userId=" + userId.getValue() +
                ", marketId=" + marketId.getValue() +
                ", orderPrice=" + orderPrice.getValue() +
                ",\n quantity=" + quantity.getValue() +
                ", createdAt=" + createdAt.getValue() +
                ", transactionType=" + transactionType.name() +
                ", feeAmount=" + feeAmount.getValue() +
                ",\n feeRate=" + feeRate.getRate() +
                ", sellOrderId=" + sellOrderId.getValue() +
                ", buyOrderId=" + buyOrderId.getValue() +
                '}';
    }
}
