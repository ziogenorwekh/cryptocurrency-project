package shop.shportfolio.user.database.jpa.adapter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import shop.shportfolio.user.application.ports.output.repository.UserTrHistoryRepositoryPort;
import shop.shportfolio.user.database.jpa.entity.TransactionHistoryEntity;
import shop.shportfolio.user.database.jpa.mapper.UserDataAccessMapper;
import shop.shportfolio.user.database.jpa.repository.TransactionHistoryJpaRepository;
import shop.shportfolio.user.domain.entity.TransactionHistory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class UserTrHistoryRepositoryPortImpl implements UserTrHistoryRepositoryPort {

    private final TransactionHistoryJpaRepository transactionHistoryJpaRepository;
    private final UserDataAccessMapper userDataAccessMapper;

    @Autowired
    public UserTrHistoryRepositoryPortImpl(TransactionHistoryJpaRepository transactionHistoryJpaRepository,
                                           UserDataAccessMapper userDataAccessMapper) {
        this.transactionHistoryJpaRepository = transactionHistoryJpaRepository;
        this.userDataAccessMapper = userDataAccessMapper;
    }

    @Override
    public List<TransactionHistory> findUserTransactionHistoryByUserId(UUID userId) {
        return transactionHistoryJpaRepository.findTransactionHistoryEntitiesByUserId(userId)
                .stream().map(userDataAccessMapper::transactionHistoryEntityToTransactionHistory)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<TransactionHistory> findUserTransactionHistoryByUserIdAndHistoryId(UUID userId, UUID historyId) {
        return transactionHistoryJpaRepository.findTransactionHistoryEntityByUserIdAndTransactionId(userId, historyId)
                .map(userDataAccessMapper::transactionHistoryEntityToTransactionHistory);
    }

    @Override
    public TransactionHistory save(TransactionHistory transactionHistory) {
        TransactionHistoryEntity transactionHistoryEntity = userDataAccessMapper.
                transactionHistoryToTransactionHistoryEntity(transactionHistory);
        TransactionHistoryEntity saved = transactionHistoryJpaRepository.save(transactionHistoryEntity);
        return userDataAccessMapper.transactionHistoryEntityToTransactionHistory(saved);
    }

}
