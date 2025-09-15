package shop.shportfolio.trading.domain.entity.userbalance;

import lombok.Builder;
import lombok.Getter;
import shop.shportfolio.common.domain.entity.BaseEntity;
import shop.shportfolio.common.domain.valueobject.*;

@Getter
public class CryptoBalance extends BaseEntity<BalanceId> {

    private final UserId userId;
    private final MarketId marketId;
    private Money purchasedPrice;
    private Quantity purchasedQuantity;

    @Builder
    public CryptoBalance(BalanceId balanceId, UserId userId, MarketId marketId, Money purchasedPrice, Quantity purchasedQuantity) {
        setId(balanceId);
        this.userId = userId;
        this.marketId = marketId;
        this.purchasedPrice = purchasedPrice;
        this.purchasedQuantity = purchasedQuantity;
    }

    public static CryptoBalance createCryptoBalance(BalanceId balanceId, UserId userId, MarketId marketId, Money purchasedPrice, Quantity purchasedQuantity) {
        return new CryptoBalance(balanceId, userId, marketId, purchasedPrice, purchasedQuantity);
    }


    public void updateQuantity(Quantity quantity) {
        this.purchasedQuantity = quantity;
    }

    public void updatePurchasedAmount(Money purchasedAmount) {
        this.purchasedPrice = purchasedAmount;
    }
}
