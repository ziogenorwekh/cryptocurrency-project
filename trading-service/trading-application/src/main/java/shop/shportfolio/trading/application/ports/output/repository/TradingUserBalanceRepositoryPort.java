package shop.shportfolio.trading.application.ports.output.repository;

import shop.shportfolio.trading.domain.entity.userbalance.LockBalance;
import shop.shportfolio.trading.domain.entity.userbalance.UserBalance;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TradingUserBalanceRepositoryPort {

    UserBalance saveUserBalance(UserBalance userBalance);
    Optional<UserBalance> findUserBalanceById(UUID userBalanceId);
    Optional<UserBalance> findUserBalanceByUserId(UUID userId);
    void deleteUserBalanceById(UUID userBalanceId);
    void deleteUserBalanceByUserId(UUID userId);

    List<LockBalance> getUserBalanceByUserId(UUID userId);
    LockBalance saveLockBalance(LockBalance lockBalance);
    Optional<LockBalance> findLockBalanceByUserId(UUID userId);
    Optional<LockBalance> findLockBalanceByOrderId(String orderId);
    void deleteLockBalanceByUserId(UUID userId);
    void deleteLockBalanceByOrderId(String orderId);

}
