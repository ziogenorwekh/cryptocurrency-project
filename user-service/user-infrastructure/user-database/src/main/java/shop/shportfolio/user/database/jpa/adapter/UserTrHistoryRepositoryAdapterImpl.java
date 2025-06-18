package shop.shportfolio.user.database.jpa.adapter;

import org.springframework.stereotype.Repository;
import shop.shportfolio.user.application.ports.output.repository.UserTrHistoryRepositoryAdapter;
import shop.shportfolio.user.database.jpa.repository.TransactionHistoryJpaRepository;
import shop.shportfolio.user.domain.entity.TransactionHistory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UserTrHistoryRepositoryAdapterImpl implements UserTrHistoryRepositoryAdapter {

    private final TransactionHistoryJpaRepository transactionHistoryJpaRepository;

    public UserTrHistoryRepositoryAdapterImpl(TransactionHistoryJpaRepository transactionHistoryJpaRepository) {
        this.transactionHistoryJpaRepository = transactionHistoryJpaRepository;
    }

    @Override
    public List<TransactionHistory> findUserTransactionHistoryByUserId(UUID userId) {
        return List.of();
    }

    @Override
    public Optional<TransactionHistory> findUserTransactionHistoryByUserIdAndHistoryId(UUID userId, UUID historyId) {
        return Optional.empty();
    }

    @Override
    public TransactionHistory save(TransactionHistory transactionHistory) {
        return null;
    }
}
