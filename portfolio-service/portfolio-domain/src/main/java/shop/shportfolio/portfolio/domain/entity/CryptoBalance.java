package shop.shportfolio.portfolio.domain.entity;

import lombok.Builder;
import lombok.Getter;
import shop.shportfolio.common.domain.valueobject.MarketId;
import shop.shportfolio.common.domain.valueobject.Quantity;
import shop.shportfolio.common.domain.valueobject.UpdatedAt;
import shop.shportfolio.portfolio.domain.exception.PortfolioDomainException;
import shop.shportfolio.common.domain.valueobject.BalanceId;
import shop.shportfolio.portfolio.domain.valueobject.PortfolioId;
import shop.shportfolio.portfolio.domain.valueobject.PurchasePrice;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
public class CryptoBalance extends Balance {

    protected Quantity quantity;
    protected PurchasePrice purchasePrice;


    @Builder
    public CryptoBalance(BalanceId balanceId, PortfolioId portfolioId,
                         MarketId marketId, UpdatedAt updatedAt,
                         Quantity quantity, PurchasePrice purchasePrice) {
        super(balanceId, portfolioId, marketId, updatedAt);
        this.quantity = quantity;
        this.purchasePrice = purchasePrice;
    }

    public static CryptoBalance create(BalanceId balanceId, PortfolioId portfolioId, MarketId marketId,
                                       PurchasePrice purchasePrice, Quantity quantity, UpdatedAt updatedAt) {
        return new CryptoBalance(balanceId, portfolioId, marketId, updatedAt, quantity, purchasePrice);
    }

    public void addPurchase(PurchasePrice newPrice, Quantity newAmount) {
        BigDecimal totalValue = this.purchasePrice.getValue().multiply(this.quantity.getValue())
                .add(newPrice.getValue().multiply(newAmount.getValue()));
        Quantity newTotalQuantity = this.quantity.add(newAmount);
        // 소수점 8자리, 반올림 HALF_UP 지정
        PurchasePrice updatedPrice = new PurchasePrice(totalValue.divide(newTotalQuantity.getValue(),
                8, RoundingMode.HALF_UP));
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


    @Override
    public String toString() {
        return "CryptoBalance{" +
                "updatedAt=" + updatedAt +
                ", marketId=" + marketId.getValue() +
                ", portfolioId=" + portfolioId.getValue() +
                ", purchasePrice=" + purchasePrice.getValue() +
                ", quantity=" + quantity.getValue() +
                '}';
    }
}
