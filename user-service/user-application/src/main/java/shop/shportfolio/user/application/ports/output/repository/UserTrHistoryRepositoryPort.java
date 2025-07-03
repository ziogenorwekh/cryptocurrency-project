package shop.shportfolio.user.application.ports.output.repository;

import shop.shportfolio.user.domain.entity.TransactionHistory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserTrHistoryRepositoryPort {


    List<TransactionHistory> findUserTransactionHistoryByUserId(UUID userId);

    Optional<TransactionHistory> findUserTransactionHistoryByUserIdAndHistoryId(UUID userId,UUID historyId);

    TransactionHistory save(TransactionHistory transactionHistory);
}
