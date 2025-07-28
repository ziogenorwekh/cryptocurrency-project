package shop.shportfolio.portfolio.domain;

import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.portfolio.domain.entity.DepositWithdrawal;
import shop.shportfolio.portfolio.domain.event.DepositCreatedEvent;
import shop.shportfolio.portfolio.domain.event.WithdrawalCreatedEvent;
import shop.shportfolio.portfolio.domain.valueobject.RelatedWalletAddress;
import shop.shportfolio.portfolio.domain.valueobject.TransactionId;

public interface DepositWithdrawalDomainService {

    DepositWithdrawal createDepositWithdrawalWithVirtualAccount(TransactionId transactionId,
                                              UserId userId,
                                              Money amount,
                                              TransactionType transactionType,
                                              TransactionTime transactionTime,
                                              TransactionStatus transactionStatus,
                                              RelatedWalletAddress relatedWalletAddress,
                                              CreatedAt createdAt,
                                              UpdatedAt updatedAt);

    DepositCreatedEvent createDeposit(TransactionId transactionId, UserId userId,
                                      Money amount, TransactionType transactionType,
                                      TransactionTime transactionTime,
                                      TransactionStatus transactionStatus,
                                      RelatedWalletAddress relatedWalletAddress,
                                      CreatedAt createdAt,
                                      UpdatedAt updatedAt);

    DepositWithdrawal createWithdrawal(TransactionId transactionId, UserId userId,
                                            Money amount, TransactionType transactionType,
                                            TransactionTime transactionTime,
                                            TransactionStatus transactionStatus,
                                            RelatedWalletAddress relatedWalletAddress,
                                            CreatedAt createdAt,
                                            UpdatedAt updatedAt);

    WithdrawalCreatedEvent updateWithdrawal(DepositWithdrawal depositWithdrawal);


    DepositCreatedEvent markCompleted(DepositWithdrawal depositWithdrawal);

    void markFailed(DepositWithdrawal depositWithdrawal);
}
