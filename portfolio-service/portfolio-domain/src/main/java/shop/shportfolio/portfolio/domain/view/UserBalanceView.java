package shop.shportfolio.portfolio.domain.view;

import lombok.Builder;
import lombok.Getter;
import shop.shportfolio.common.domain.valueobject.AssetCode;
import shop.shportfolio.common.domain.valueobject.Money;
import shop.shportfolio.common.domain.valueobject.UpdatedAt;
import shop.shportfolio.common.domain.valueobject.UserId;
import shop.shportfolio.common.domain.view.BaseView;

@Getter
public class UserBalanceView extends BaseView {
    private final UserId userId;
    private final AssetCode assetCode;
    private Money money;

    @Builder
    public UserBalanceView(UserId userId, AssetCode assetCode, Money money) {
        super(UpdatedAt.now());
        this.userId = userId;
        this.assetCode = assetCode;
        this.money = money;
    }

    public void addMoney(Money money) {
        this.money = this.money.add(money);
        this.updatedAt = UpdatedAt.now();
    }
    public void subMoney(Money money) {
        this.money = this.money.subtract(money);
        this.updatedAt = UpdatedAt.now();
    }

}
