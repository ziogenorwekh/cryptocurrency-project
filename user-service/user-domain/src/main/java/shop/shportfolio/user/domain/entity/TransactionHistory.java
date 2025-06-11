package shop.shportfolio.user.domain.entity;

import shop.shportfolio.common.domain.entity.BaseEntity;
import shop.shportfolio.common.domain.valueobject.MarketId;
import shop.shportfolio.user.domain.valueobject.Amount;
import shop.shportfolio.user.domain.valueobject.TransactionHistoryId;
import shop.shportfolio.user.domain.valueobject.TransactionTime;
import shop.shportfolio.user.domain.valueobject.TransactionType;

public class TransactionHistory extends BaseEntity<TransactionHistoryId> {

    private MarketId marketId;
    private TransactionType transactionType;
    private Amount amount;
    private TransactionTime transactionTime;



}
