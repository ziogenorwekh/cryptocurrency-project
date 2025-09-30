package shop.shportfolio.trading.application.ports.output.repository;

import shop.shportfolio.trading.domain.entity.userbalance.CryptoBalance;
import shop.shportfolio.trading.domain.entity.userbalance.UserBalance;

import java.util.Optional;
import java.util.UUID;

public interface TradingUserBalanceRepositoryPort {

    UserBalance saveUserBalance(UserBalance userBalance);
    Optional<UserBalance> findUserBalanceByUserIdWithLock(UUID userId);
    Optional<UserBalance> findUserBalanceByUserId(UUID userId);
    void deleteUserBalanceByUserId(UUID userId);

    CryptoBalance saveCryptoBalance(CryptoBalance cryptoBalance);
    Optional<CryptoBalance> findCryptoBalanceByUserIdAndMarketId(UUID userId,String marketId);
}
