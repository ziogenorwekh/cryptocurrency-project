package shop.shportfolio.user.domain.entity;

import lombok.Builder;
import lombok.Getter;
import shop.shportfolio.common.domain.entity.BaseEntity;
import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.user.domain.valueobject.TransactionHistoryId;
import shop.shportfolio.user.domain.valueobject.TransactionTime;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class TransactionHistory extends BaseEntity<TransactionHistoryId> {

    private UserId userId;
    private OrderId orderId;
    private MarketId marketId;
    private TransactionType transactionType;
    private OrderPrice orderPrice;
    private Quantity quantity;
    private TransactionTime transactionTime;

    private TransactionHistory(TransactionHistoryId transactionHistoryId, OrderId orderId,
                               UserId userId, MarketId marketId,
                               TransactionType transactionType, OrderPrice orderPrice,
                               Quantity quantity, TransactionTime transactionTime) {
        setId(transactionHistoryId);
        this.userId = userId;
        this.orderId = orderId;
        this.marketId = marketId;
        this.transactionType = transactionType;
        this.orderPrice = orderPrice;
        this.quantity = quantity;
        this.transactionTime = transactionTime;
    }

    @Builder
    public TransactionHistory(UUID transactionHistoryId, UUID userId, String orderId,
                              String marketId, TransactionType transactionType,
                              BigDecimal orderPrice, BigDecimal quantity,
                              LocalDateTime transactionTime) {
        setId(new TransactionHistoryId(transactionHistoryId));
        this.userId = new UserId(userId);
        this.orderId = new OrderId(orderId);
        this.marketId = new MarketId(marketId);
        this.transactionType = transactionType;
        this.orderPrice = new OrderPrice(orderPrice);
        this.quantity = new Quantity(quantity);
        this.transactionTime = new TransactionTime(transactionTime);
    }

    public static TransactionHistory createTransactionHistory(
            UserId userId, OrderId orderId, MarketId marketId,
            TransactionType transactionType, OrderPrice orderPrice,
            Quantity quantity, TransactionTime transactionTime) {
        return new TransactionHistory(new TransactionHistoryId(UUID.randomUUID()),
                orderId, userId, marketId,
                transactionType, orderPrice, quantity, transactionTime);
    }
}