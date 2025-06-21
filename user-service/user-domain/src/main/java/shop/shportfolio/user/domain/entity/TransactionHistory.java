package shop.shportfolio.user.domain.entity;

import lombok.Builder;
import lombok.Getter;
import shop.shportfolio.common.domain.entity.BaseEntity;
import shop.shportfolio.common.domain.valueobject.MarketId;
import shop.shportfolio.common.domain.valueobject.UserId;
import shop.shportfolio.user.domain.valueobject.Amount;
import shop.shportfolio.user.domain.valueobject.TransactionHistoryId;
import shop.shportfolio.user.domain.valueobject.TransactionTime;
import shop.shportfolio.user.domain.valueobject.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class TransactionHistory extends BaseEntity<TransactionHistoryId> {

    private UserId userId;
    private MarketId marketId;
    private TransactionType transactionType;
    private Amount amount;
    private TransactionTime transactionTime;

    public TransactionHistory(TransactionHistoryId transactionHistoryId, UserId userId, MarketId marketId, TransactionType transactionType,
                              Amount amount, TransactionTime transactionTime) {
        setId(transactionHistoryId);
        this.userId = userId;
        this.marketId = marketId;
        this.transactionType = transactionType;
        this.amount = amount;
        this.transactionTime = transactionTime;
    }

    @Builder
    public TransactionHistory(UUID transactionHistoryId, UUID userId, String marketId, TransactionType transactionType, BigDecimal amount,
                              LocalDateTime transactionTime) {
        setId(new TransactionHistoryId(transactionHistoryId));
        this.userId = new UserId(userId);
        this.marketId = new  MarketId(marketId);
        this.transactionType = transactionType;
        this.amount = new  Amount(amount);
        this.transactionTime = new TransactionTime(transactionTime);
    }

    public static TransactionHistory createTransactionHistory(TransactionHistoryId transactionHistoryId, UserId userId,
                                                              MarketId marketId, TransactionType transactionType,
                                                              Amount amount, TransactionTime transactionTime) {
        return new TransactionHistory(transactionHistoryId, userId, marketId, transactionType, amount, transactionTime);
    }
}
