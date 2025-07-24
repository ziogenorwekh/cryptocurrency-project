package shop.shportfolio.portfolio.domain;

import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.portfolio.domain.entity.DepositWithdrawal;
import shop.shportfolio.portfolio.domain.valueobject.RelatedWalletAddress;
import shop.shportfolio.portfolio.domain.valueobject.TransactionId;

public class DepositWithdrawalDomainServiceImpl implements DepositWithdrawalDomainService {

    @Override
    public DepositWithdrawal createDepositWithdrawal(TransactionId transactionId, UserId userId,
                                                     Money amount, TransactionType transactionType,
                                                     TransactionTime transactionTime,
                                                     TransactionStatus transactionStatus,
                                                     RelatedWalletAddress relatedWalletAddress, CreatedAt
                                                             createdAt, UpdatedAt updatedAt) {
        return DepositWithdrawal.createDepositWithdrawal(transactionId, userId, amount, transactionType,
                transactionTime, transactionStatus, relatedWalletAddress, createdAt, updatedAt);
    }

    @Override
    public void markCompleted(DepositWithdrawal depositWithdrawal) {
        depositWithdrawal.markCompleted();
    }

    @Override
    public void markFailed(DepositWithdrawal depositWithdrawal) {
        depositWithdrawal.markFailed();
    }
}
