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
    private Quantity quantity;
    private ValuationPrice valuationPrice;
    private ValuationAmount valuationAmount;
    private UpdatedAt updatedAt;

    public Balance(BalanceId balanceId, PortfolioId portfolioId, MarketId marketId, Quantity quantity,
                   ValuationPrice valuationPrice, ValuationAmount valuationAmount, UpdatedAt updatedAt) {
        setId(balanceId);
        this.portfolioId = portfolioId;
        this.marketId = marketId;
        this.quantity = quantity;
        this.valuationPrice = valuationPrice;
        this.valuationAmount = valuationAmount;
        this.updatedAt = updatedAt;
    }

    public void increase(Quantity delta) {
        this.quantity = this.quantity.add(delta);
        this.updatedAt = UpdatedAt.now();
    }

    public void decrease(Quantity delta) {
        if (this.quantity.isLessThan(delta)) {
            throw new IllegalStateException("Insufficient balance");
        }
        this.quantity = this.quantity.subtract(delta);
        this.updatedAt = UpdatedAt.now();
    }

    public void updateValuation(ValuationPrice price, ValuationAmount amount) {
        this.valuationPrice = price;
        this.valuationAmount = amount;
        this.updatedAt = UpdatedAt.now();
    }
}
