package shop.shportfolio.portfolio.domain.entity;

import lombok.Getter;
import shop.shportfolio.common.domain.valueobject.MarketId;
import shop.shportfolio.common.domain.valueobject.Money;
import shop.shportfolio.common.domain.valueobject.UpdatedAt;
import shop.shportfolio.portfolio.domain.exception.PortfolioDomainException;
import shop.shportfolio.portfolio.domain.valueobject.BalanceId;
import shop.shportfolio.portfolio.domain.valueobject.PortfolioId;

@Getter
public class CurrencyBalance extends Balance {

    private Money amount;

    public CurrencyBalance(BalanceId balanceId, PortfolioId portfolioId,
                           MarketId marketId, UpdatedAt updatedAt,Money amount) {
        super(balanceId, portfolioId, marketId, updatedAt);
        this.amount = amount;
    }

    public static CurrencyBalance create(BalanceId balanceId, PortfolioId portfolioId,
                                         MarketId marketId, UpdatedAt updatedAt, Money amount) {
        return new CurrencyBalance(balanceId, portfolioId, marketId, updatedAt, amount);
    }

    public void addMoney(Money money) {
        amount = amount.add(money);
        this.updatedAt = UpdatedAt.now();
    }

    public void subtractMoney(Money money) {
        if (amount.getValue().compareTo(money.getValue()) < 0) {
            throw new PortfolioDomainException("Amount to subtract is insufficient");
        }
        amount = amount.subtract(money);
        this.updatedAt = UpdatedAt.now();
    }

}
