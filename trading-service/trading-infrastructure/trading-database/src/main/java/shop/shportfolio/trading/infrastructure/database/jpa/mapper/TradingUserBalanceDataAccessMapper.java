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


@Component
public class TradingUserBalanceDataAccessMapper {

    public UserBalance userBalanceToUserBalanceEntity(UserBalanceEntity userBalanceEntity) {
        return UserBalance.builder()
                .userBalanceId(new UserBalanceId(userBalanceEntity.getUserBalanceId()))
                .userId(new UserId(userBalanceEntity.getUserId()))
                .lockBalances(userBalanceEntity.getLockBalances()
                        .stream().map(this::lockBalanceToLockBalanceEntity).toList())
                .assetCode(userBalanceEntity.getAssetCode())
                .availableMoney(Money.of(userBalanceEntity.getMoney()))
                .build();
    };

    public UserBalanceEntity userBalanceToUserBalanceEntity(UserBalance userBalance) {
        return UserBalanceEntity.builder()
                .userBalanceId(userBalance.getId().getValue())
                .userId(userBalance.getUserId().getValue())
                .money(userBalance.getAvailableMoney().getValue())
                .assetCode(userBalance.getAssetCode())
                .lockBalances(userBalance.getLockBalances().stream()
                        .map(this::lockBalanceToLockBalanceEntity).toList())
                .build();
    }


    private LockBalance lockBalanceToLockBalanceEntity(LockBalanceEntity lockBalanceEntity) {
        return LockBalance.builder()
                .orderId(new OrderId(lockBalanceEntity.getOrderId()))
                .lockedAt(new CreatedAt(lockBalanceEntity.getLockedAt()))
                .lockStatus(lockBalanceEntity.getLockStatus())
                .userId(new UserId(lockBalanceEntity.getUserId()))
                .lockedAmount(Money.of(lockBalanceEntity.getLockedAmount()))
                .build();
    }

    private LockBalanceEntity lockBalanceToLockBalanceEntity(LockBalance lockBalance) {
        return LockBalanceEntity.builder()
                .orderId(lockBalance.getId().getValue())
                .lockedAmount(lockBalance.getLockedAmount().getValue())
                .lockedAt(lockBalance.getLockedAt().getValue())
                .lockStatus(lockBalance.getLockStatus())
                .userId(lockBalance.getUserId().getValue())
                .build();
    }
}
