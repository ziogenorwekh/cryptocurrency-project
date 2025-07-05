package shop.shportfolio.portfolio.domain.entity;

import lombok.Getter;
import shop.shportfolio.common.domain.entity.BaseEntity;
import shop.shportfolio.common.domain.valueobject.MarketId;
import shop.shportfolio.common.domain.valueobject.Quantity;
import shop.shportfolio.common.domain.valueobject.UpdatedAt;
import shop.shportfolio.portfolio.domain.valueobject.*;

@Getter
public class Balance extends BaseEntity<BalanceId> {

    private final PortfolioId portfolioId;
    private final MarketId marketId;
    private final Quantity quantity;
    private final AvailableQuantity availableQuantity;
    private final LockedQuantity lockedQuantity;
    private final ValuationPrice valuationPrice;
    private final ValuationAmount valuationAmount;
    private UpdatedAt updatedAt;

    public Balance(BalanceId balanceId, PortfolioId portfolioId, MarketId marketId, Quantity quantity,
                   AvailableQuantity availableQuantity, LockedQuantity lockedQuantity,
                   ValuationPrice valuationPrice, ValuationAmount valuationAmount, UpdatedAt updatedAt) {
        setId(balanceId);
        this.portfolioId = portfolioId;
        this.marketId = marketId;
        this.quantity = quantity;
        this.availableQuantity = availableQuantity;
        this.lockedQuantity = lockedQuantity;
        this.valuationPrice = valuationPrice;
        this.valuationAmount = valuationAmount;
        this.updatedAt = updatedAt;
    }
}
