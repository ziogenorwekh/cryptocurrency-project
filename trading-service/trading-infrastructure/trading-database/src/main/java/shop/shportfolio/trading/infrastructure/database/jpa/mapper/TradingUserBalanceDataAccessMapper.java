package shop.shportfolio.trading.infrastructure.database.jpa.mapper;

import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.valueobject.CreatedAt;
import shop.shportfolio.common.domain.valueobject.Money;
import shop.shportfolio.common.domain.valueobject.OrderId;
import shop.shportfolio.common.domain.valueobject.UserId;
import shop.shportfolio.trading.domain.entity.userbalance.LockBalance;
import shop.shportfolio.trading.domain.entity.userbalance.UserBalance;
import shop.shportfolio.trading.domain.valueobject.UserBalanceId;
import shop.shportfolio.trading.infrastructure.database.jpa.entity.userbalance.LockBalanceEntity;
import shop.shportfolio.trading.infrastructure.database.jpa.entity.userbalance.UserBalanceEntity;

import java.util.ArrayList;


@Component
public class TradingUserBalanceDataAccessMapper {

    public UserBalance userBalanceEntityToUserBalance(UserBalanceEntity userBalanceEntity) {
        return UserBalance.builder()
                .userBalanceId(new UserBalanceId(userBalanceEntity.getUserBalanceId()))
                .userId(new UserId(userBalanceEntity.getUserId()))
                .lockBalances(userBalanceEntity.getLockBalances()
                        .stream()
                        .map(this::lockBalanceEntityToLockBalance)
                        .toList())
                .assetCode(userBalanceEntity.getAssetCode())
                .availableMoney(Money.of(userBalanceEntity.getMoney()))
                .build();
    }

    public UserBalanceEntity userBalanceToUserBalanceEntity(UserBalance userBalance) {

        UserBalanceEntity userBalanceEntity = UserBalanceEntity.builder()
                .userBalanceId(userBalance.getId().getValue())
                .userId(userBalance.getUserId().getValue())
                .money(userBalance.getAvailableMoney().getValue())
                .assetCode(userBalance.getAssetCode())
                .lockBalances(new  ArrayList<>())
                .build();

        if (userBalance.getLockBalances() != null) {
            var lockBalanceEntities = userBalance.getLockBalances().stream()
                    .map(lockBalance -> lockBalanceToLockBalanceEntity(userBalanceEntity, lockBalance))
                    .toList();

            userBalanceEntity.getLockBalances().clear();
            userBalanceEntity.getLockBalances().addAll(lockBalanceEntities);
        }

        return userBalanceEntity;
    }

    private LockBalance lockBalanceEntityToLockBalance(LockBalanceEntity lockBalanceEntity) {
        return LockBalance.builder()
                .orderId(new OrderId(lockBalanceEntity.getOrderId()))
                .lockedAt(new CreatedAt(lockBalanceEntity.getLockedAt()))
                .lockStatus(lockBalanceEntity.getLockStatus())
                .userId(new UserId(lockBalanceEntity.getUserId()))
                .lockedAmount(Money.of(lockBalanceEntity.getLockedAmount()))
                .build();
    }

    private LockBalanceEntity lockBalanceToLockBalanceEntity(UserBalanceEntity userBalanceEntity, LockBalance lockBalance) {
        return LockBalanceEntity.builder()
                .orderId(lockBalance.getId().getValue())
                .lockedAmount(lockBalance.getLockedAmount().getValue())
                .lockedAt(lockBalance.getLockedAt().getValue())
                .lockStatus(lockBalance.getLockStatus())
                .userId(lockBalance.getUserId().getValue())
                .userBalance(userBalanceEntity) // 연관관계 설정
                .build();
    }
}
