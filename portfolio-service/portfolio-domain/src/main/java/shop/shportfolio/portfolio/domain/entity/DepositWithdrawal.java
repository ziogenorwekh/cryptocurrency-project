package shop.shportfolio.portfolio.domain.entity;

import lombok.Getter;
import shop.shportfolio.common.domain.entity.BaseEntity;
import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.portfolio.domain.valueobject.Amount;
import shop.shportfolio.portfolio.domain.valueobject.RelatedWalletAddress;
import shop.shportfolio.portfolio.domain.valueobject.TransactionId;

@Getter
public class DepositWithdrawal extends BaseEntity<TransactionId> {

    private final UserId userId;
    private final Amount amount;
    private final TransactionType transactionType;
    private final TransactionTime transactionTime;
    private final TransactionStatus transactionStatus;
    private final RelatedWalletAddress relatedWalletAddress;
    private final CreatedAt createdAt;
    private UpdatedAt updatedAt;

    public DepositWithdrawal(TransactionId transactionId, UserId userId,
                             Amount amount, TransactionType transactionType,
                             TransactionTime transactionTime, TransactionStatus transactionStatus,
                             RelatedWalletAddress relatedWalletAddress,
                             CreatedAt createdAt, UpdatedAt updatedAt) {
        setId(transactionId);
        this.userId = userId;
        this.amount = amount;
        this.transactionType = transactionType;
        this.transactionTime = transactionTime;
        this.transactionStatus = transactionStatus;
        this.relatedWalletAddress = relatedWalletAddress;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
