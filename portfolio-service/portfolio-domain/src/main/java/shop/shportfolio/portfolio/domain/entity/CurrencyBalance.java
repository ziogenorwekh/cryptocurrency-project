package shop.shportfolio.portfolio.domain.entity;

import lombok.Getter;
import shop.shportfolio.common.domain.valueobject.MarketId;
import shop.shportfolio.common.domain.valueobject.Money;
import shop.shportfolio.common.domain.valueobject.UpdatedAt;
import shop.shportfolio.common.domain.valueobject.UserId;
import shop.shportfolio.portfolio.domain.valueobject.BalanceId;
import shop.shportfolio.portfolio.domain.valueobject.PortfolioId;

import java.math.BigDecimal;

@Getter
public class CurrencyBalance extends Balance {

    private Money amount;
    private final UserId userId;

    public CurrencyBalance(BalanceId balanceId, PortfolioId portfolioId,
                           MarketId marketId, UpdatedAt updatedAt, Money amount, UserId userId) {
        super(balanceId, portfolioId, marketId, updatedAt);
        this.amount = amount;
        this.userId = userId;
    }

    public static CurrencyBalance create(BalanceId balanceId, PortfolioId portfolioId,
                                         MarketId marketId, UpdatedAt updatedAt, Money amount, UserId userId) {
        return new CurrencyBalance(balanceId, portfolioId, marketId, updatedAt, amount, userId);
    }

    public void updateMoney(Money money) {
        this.amount = money;
    }

    public boolean isOverCurrencyBalanceAmount(Long withdrawalAmount) {
        return this.amount.isLessThan(BigDecimal.valueOf(withdrawalAmount));
    }

    public void addMoney(Money money) {
        this.amount = this.amount.add(money);
    }

    public void subtractMoney(Money money) {
        this.amount = this.amount.subtract(money);
    }

}
