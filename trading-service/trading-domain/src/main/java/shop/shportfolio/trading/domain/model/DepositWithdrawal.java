package shop.shportfolio.trading.domain.model;

import lombok.Builder;
import lombok.Getter;
import shop.shportfolio.common.domain.entity.ViewEntity;
import shop.shportfolio.common.domain.valueobject.*;

import java.time.ZoneOffset;

@Getter
public class DepositWithdrawal extends ViewEntity<TransactionId> {
    private final UserId userId;
    private final Money amount;
    private final TransactionType transactionType;
    private final TransactionTime transactionTime;

    @Builder
    public DepositWithdrawal(TransactionId transactionId, UserId userId, Money amount, TransactionType transactionType,
                             TransactionTime transactionTime) {
        setId(transactionId);
        this.userId = userId;
        this.amount = amount;
        this.transactionType = transactionType;
        this.transactionTime = transactionTime;
    }


    public static DepositWithdrawal createDepositWithdrawal(TransactionId transactionId,
                                                            UserId userId,
                                                            Money amount,
                                                            TransactionType transactionType) {
        return DepositWithdrawal.builder()
                .transactionId(transactionId)
                .userId(userId)
                .amount(amount)
                .transactionType(transactionType)
                .transactionTime(TransactionTime.now())
                .build();
    }

}
