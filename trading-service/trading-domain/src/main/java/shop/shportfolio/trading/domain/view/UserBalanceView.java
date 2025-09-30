package shop.shportfolio.trading.domain.view;

import lombok.Builder;
import lombok.Getter;
import shop.shportfolio.common.domain.entity.ViewEntity;
import shop.shportfolio.common.domain.valueobject.AssetCode;
import shop.shportfolio.common.domain.valueobject.DirectionType;
import shop.shportfolio.common.domain.valueobject.Money;
import shop.shportfolio.common.domain.valueobject.UserId;

@Getter
public class UserBalanceView extends ViewEntity<UserId> {

    private final AssetCode assetCode;
    private final Money amount;
    private final DirectionType directionType;

    @Builder
    public UserBalanceView(UserId userId, AssetCode assetCode, Money amount, DirectionType directionType) {
        setId(userId);
        this.assetCode = assetCode;
        this.amount = amount;
        this.directionType = directionType;
    }
}
