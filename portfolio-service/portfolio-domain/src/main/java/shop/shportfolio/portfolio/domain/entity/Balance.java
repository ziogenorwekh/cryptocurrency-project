package shop.shportfolio.portfolio.domain.entity;

import lombok.Getter;
import shop.shportfolio.common.domain.entity.BaseEntity;
import shop.shportfolio.common.domain.valueobject.MarketId;
import shop.shportfolio.common.domain.valueobject.Quantity;
import shop.shportfolio.common.domain.valueobject.UpdatedAt;
import shop.shportfolio.portfolio.domain.exception.PortfolioDomainException;
import shop.shportfolio.portfolio.domain.valueobject.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
public class Balance extends BaseEntity<BalanceId> {

    private final PortfolioId portfolioId;
    private final MarketId marketId;
    private Quantity quantity;
    private PurchasePrice purchasePrice;
    private UpdatedAt updatedAt;

    public Balance(BalanceId balanceId, PortfolioId portfolioId, MarketId marketId, Quantity quantity,
                   PurchasePrice purchasePrice, UpdatedAt updatedAt) {
        setId(balanceId);
        this.portfolioId = portfolioId;
        this.marketId = marketId;
        this.quantity = quantity;
        this.purchasePrice = purchasePrice;
        this.updatedAt = updatedAt;
    }

    public void addPurchase(PurchasePrice newPrice, Quantity newAmount) {
        BigDecimal totalValue = this.purchasePrice.getValue().multiply(this.quantity.getValue())
                .add(newPrice.getValue().multiply(newAmount.getValue()));
        Quantity newTotalQuantity = this.quantity.add(newAmount);
        // 소수점 8자리, 반올림 HALF_UP 지정
        PurchasePrice updatedPrice = new PurchasePrice(totalValue.divide(newTotalQuantity.getValue(), 8, RoundingMode.HALF_UP));
        this.purchasePrice = updatedPrice;
        this.quantity = newTotalQuantity;
        this.updatedAt = UpdatedAt.now();
    }

    public void subtractQuantity(Quantity amount) {
        if (this.quantity.isLessThan(amount)) {
            throw new PortfolioDomainException("Quantity to subtract exceeds current quantity");
        }
        this.quantity = this.quantity.subtract(amount);
        this.updatedAt = UpdatedAt.now();
    }

    public void addQuantity(Quantity amount) {
        this.quantity = this.quantity.add(amount);
        this.updatedAt = UpdatedAt.now();
    }
}
