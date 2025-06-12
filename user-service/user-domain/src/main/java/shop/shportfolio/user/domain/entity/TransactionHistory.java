package shop.shportfolio.user.domain.entity;

import lombok.Builder;
import lombok.Getter;
import shop.shportfolio.common.domain.entity.BaseEntity;
import shop.shportfolio.common.domain.valueobject.MarketId;
import shop.shportfolio.user.domain.valueobject.Amount;
import shop.shportfolio.user.domain.valueobject.TransactionHistoryId;
import shop.shportfolio.user.domain.valueobject.TransactionTime;
import shop.shportfolio.user.domain.valueobject.TransactionType;

@Getter
public class TransactionHistory extends BaseEntity<TransactionHistoryId> {

    private MarketId marketId;
    private TransactionType transactionType;
    private Amount amount;
    private TransactionTime transactionTime;

    public TransactionHistory() {

    }

    @Builder
    public TransactionHistory(MarketId marketId, TransactionType transactionType,
                              Amount amount, TransactionTime transactionTime) {
        this.marketId = marketId;
        this.transactionType = transactionType;
        this.amount = amount;
        this.transactionTime = transactionTime;
    }
}
