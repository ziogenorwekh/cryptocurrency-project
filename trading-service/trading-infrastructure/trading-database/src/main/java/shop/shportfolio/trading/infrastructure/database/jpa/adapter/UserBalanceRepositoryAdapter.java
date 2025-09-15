package shop.shportfolio.trading.infrastructure.database.jpa.adapter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import shop.shportfolio.trading.application.ports.output.repository.TradingUserBalanceRepositoryPort;
import shop.shportfolio.trading.domain.entity.userbalance.CryptoBalance;
import shop.shportfolio.trading.domain.entity.userbalance.UserBalance;
import shop.shportfolio.trading.infrastructure.database.jpa.entity.userbalance.CryptoBalanceEntity;
import shop.shportfolio.trading.infrastructure.database.jpa.entity.userbalance.UserBalanceEntity;
import shop.shportfolio.trading.infrastructure.database.jpa.mapper.TradingUserBalanceDataAccessMapper;
import shop.shportfolio.trading.infrastructure.database.jpa.repository.CryptoBalanceJpaRepository;
import shop.shportfolio.trading.infrastructure.database.jpa.repository.UserBalanceJpaRepository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class UserBalanceRepositoryAdapter implements TradingUserBalanceRepositoryPort {

    private final UserBalanceJpaRepository repository;
    private final CryptoBalanceJpaRepository cryptoBalanceJpaRepository;
    private final TradingUserBalanceDataAccessMapper mapper;

    @Autowired
    public UserBalanceRepositoryAdapter(UserBalanceJpaRepository repository, CryptoBalanceJpaRepository cryptoBalanceJpaRepository,
                                        TradingUserBalanceDataAccessMapper mapper) {
        this.repository = repository;
        this.cryptoBalanceJpaRepository = cryptoBalanceJpaRepository;
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

    @Override
    public void deleteUserBalanceByUserId(UUID userId) {
        UserBalanceEntity userBalanceEntity = repository.findUserBalanceByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User balance not found"));
        userBalanceEntity.getLockBalances().clear();
        repository.delete(userBalanceEntity);
    }

    @Override
    public CryptoBalance saveCryptoBalance(CryptoBalance cryptoBalance) {
        CryptoBalanceEntity cryptoBalanceEntity = mapper.cryptoBalanceToCryptoBalanceEntity(cryptoBalance);
        CryptoBalanceEntity saved = cryptoBalanceJpaRepository.save(cryptoBalanceEntity);
        return mapper.cryptoBalanceEntityToCryptoBalance(saved);
    }

    @Override
    public Optional<CryptoBalance> findCryptoBalanceByUserIdAndMarketId(UUID userId, String marketId) {

        return cryptoBalanceJpaRepository.findCryptoBalanceByUserIdAndMarketId(userId,marketId)
                .map(mapper::cryptoBalanceEntityToCryptoBalance);
    }
}
