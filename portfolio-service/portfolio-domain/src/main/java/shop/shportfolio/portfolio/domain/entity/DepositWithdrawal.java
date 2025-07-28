package shop.shportfolio.portfolio.domain.entity;

import lombok.Builder;
import lombok.Getter;
import shop.shportfolio.common.domain.entity.BaseEntity;
import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.portfolio.domain.exception.PortfolioDomainException;
import shop.shportfolio.portfolio.domain.valueobject.RelatedWalletAddress;
import shop.shportfolio.portfolio.domain.valueobject.TransactionId;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Getter
public class DepositWithdrawal extends BaseEntity<TransactionId> {

    private final UserId userId;
    private final Money amount;
    private final TransactionType transactionType;
    private final TransactionTime transactionTime;
    private TransactionStatus transactionStatus;
    private final RelatedWalletAddress relatedWalletAddress;
    private final CreatedAt createdAt;
    private UpdatedAt updatedAt;

    @Builder
    private DepositWithdrawal(TransactionId transactionId, UserId userId,
                              Money amount, TransactionType transactionType,
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

    public static DepositWithdrawal createDepositWithdrawal(TransactionId transactionId, UserId userId,
                                                            Money amount, TransactionType transactionType,
                                                            TransactionTime transactionTime, TransactionStatus transactionStatus,
                                                            RelatedWalletAddress relatedWalletAddress,
                                                            CreatedAt createdAt, UpdatedAt updatedAt) {
        DepositWithdrawal withdrawal = DepositWithdrawal.builder()
                .transactionId(transactionId)
                .userId(userId)
                .amount(amount)
                .transactionType(transactionType)
                .transactionTime(transactionTime)
                .transactionStatus(transactionStatus)
                .relatedWalletAddress(relatedWalletAddress)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
        if (!relatedWalletAddress.isEmpty()) {
            withdrawal.validateInitialStatus();
        }
        return withdrawal;
    }

    public void markCompleted() {
        if (this.transactionStatus != TransactionStatus.PENDING) {
            throw new PortfolioDomainException("Only PENDING transactions can be completed");
        }
        this.transactionStatus = TransactionStatus.COMPLETED;
        this.updatedAt = new UpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
    }

    public void markFailed() {
        if (this.transactionStatus != TransactionStatus.PENDING) {
            throw new PortfolioDomainException("Only PENDING transactions can be failed");
        }
        this.transactionStatus = TransactionStatus.FAILED;
        this.updatedAt = new UpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
    }

    private void validateInitialStatus() {
        if (transactionStatus != TransactionStatus.PENDING) {
            throw new PortfolioDomainException("TransactionStatus must be PENDING at creation");
        }
    }
}
