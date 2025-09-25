package shop.shportfolio.portfolio.domain;

import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.portfolio.domain.entity.DepositWithdrawal;
import shop.shportfolio.portfolio.domain.event.DepositCreatedEvent;
import shop.shportfolio.portfolio.domain.event.WithdrawalCreatedEvent;
import shop.shportfolio.portfolio.domain.valueobject.RelatedWalletAddress;
import shop.shportfolio.common.domain.valueobject.TransactionId;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class DepositWithdrawalDomainServiceImpl implements DepositWithdrawalDomainService {

    @Override
    public DepositCreatedEvent createDeposit(TransactionId transactionId, UserId userId,
                                             Money amount, TransactionType transactionType,
                                             TransactionTime transactionTime,
                                             TransactionStatus transactionStatus,
                                             RelatedWalletAddress relatedWalletAddress,
                                             CreatedAt createdAt,
                                             UpdatedAt updatedAt) {
        DepositWithdrawal depositWithdrawal = DepositWithdrawal.createDeposit(transactionId, userId,
                amount, transactionType, transactionTime, transactionStatus,
                relatedWalletAddress, createdAt, updatedAt);
        return new DepositCreatedEvent(depositWithdrawal, MessageType.CREATE, ZonedDateTime.now(ZoneOffset.UTC));
    }

    @Override
    public WithdrawalCreatedEvent createWithdrawal(TransactionId transactionId, UserId userId,
                                                   Money amount, TransactionType transactionType,
                                                   TransactionTime transactionTime,
                                                   TransactionStatus transactionStatus,
                                                   RelatedWalletAddress relatedWalletAddress,
                                                   CreatedAt createdAt, UpdatedAt updatedAt) {
        return new WithdrawalCreatedEvent(DepositWithdrawal.createWithdrawal(transactionId, userId,
                amount, transactionType, transactionTime, transactionStatus,
                relatedWalletAddress, createdAt, updatedAt), MessageType.CREATE, ZonedDateTime.now(ZoneOffset.UTC));
    }

//    @Override
//    public WithdrawalCreatedEvent updateWithdrawal(DepositWithdrawal depositWithdrawal) {
////        depositWithdrawal.markCompleted();
//        return new WithdrawalCreatedEvent(depositWithdrawal,MessageType.UPDATE, ZonedDateTime.now(ZoneOffset.UTC));
//    }

    @Override
    public DepositCreatedEvent markCompleted(DepositWithdrawal depositWithdrawal) {
        depositWithdrawal.markCompleted();
        return new DepositCreatedEvent(depositWithdrawal, MessageType.UPDATE, ZonedDateTime.now(ZoneOffset.UTC));
    }

    @Override
    public void markFailed(DepositWithdrawal depositWithdrawal) {
        depositWithdrawal.markFailed();
    }
}
