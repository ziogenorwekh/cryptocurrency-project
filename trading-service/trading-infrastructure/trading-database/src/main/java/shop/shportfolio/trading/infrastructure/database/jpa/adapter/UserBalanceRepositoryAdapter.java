package shop.shportfolio.trading.infrastructure.database.jpa.adapter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import shop.shportfolio.trading.application.ports.output.repository.TradingUserBalanceRepositoryPort;
import shop.shportfolio.trading.domain.entity.userbalance.UserBalance;
import shop.shportfolio.trading.infrastructure.database.jpa.entity.userbalance.UserBalanceEntity;
import shop.shportfolio.trading.infrastructure.database.jpa.mapper.TradingUserBalanceDataAccessMapper;
import shop.shportfolio.trading.infrastructure.database.jpa.repository.UserBalanceJpaRepository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class UserBalanceRepositoryAdapter implements TradingUserBalanceRepositoryPort {

    private final UserBalanceJpaRepository repository;
    private final TradingUserBalanceDataAccessMapper mapper;

    @Autowired
    public UserBalanceRepositoryAdapter(UserBalanceJpaRepository repository,
                                        TradingUserBalanceDataAccessMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public UserBalance saveUserBalance(UserBalance userBalance) {
        UserBalanceEntity userBalanceEntity = mapper.userBalanceToUserBalanceEntity(userBalance);
        UserBalanceEntity saved = repository.save(userBalanceEntity);
        return mapper.userBalanceEntityToUserBalance(saved);
    }

    @Override
    public Optional<UserBalance> findUserBalanceByUserId(UUID userId) {
        Optional<UserBalanceEntity> optionalUserBalanceEntity = repository.findUserBalanceByUserId(userId);
        return optionalUserBalanceEntity.map(mapper::userBalanceEntityToUserBalance);
    }
}
