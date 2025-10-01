package shop.shportfolio.portfolio.domain.view;

import lombok.Getter;
import shop.shportfolio.common.domain.entity.ViewEntity;
import shop.shportfolio.common.domain.valueobject.MarketId;
import shop.shportfolio.common.domain.valueobject.Quantity;
import shop.shportfolio.common.domain.valueobject.UserId;
import shop.shportfolio.portfolio.domain.entity.CryptoBalance;
import shop.shportfolio.common.domain.valueobject.BalanceId;
import shop.shportfolio.portfolio.domain.valueobject.PurchasePrice;

@Getter
public class CryptoView extends ViewEntity<BalanceId> {

    private final UserId userId;
    private final MarketId marketId;
    private final Quantity quantity;
    private final PurchasePrice purchasePrice;

    public CryptoView(BalanceId balanceId, UserId userId, MarketId marketId, Quantity quantity, PurchasePrice purchasePrice) {
        setId(balanceId);
        this.userId = userId;
        this.marketId = marketId;
        this.quantity = quantity;
        this.purchasePrice = purchasePrice;
    }

    public static CryptoView toCryptoView(CryptoBalance cryptoBalance, UserId userId) {
        return new CryptoView(cryptoBalance.getId(), userId, cryptoBalance.getMarketId(), cryptoBalance.getQuantity(),
                cryptoBalance.getPurchasePrice());
    }
}
