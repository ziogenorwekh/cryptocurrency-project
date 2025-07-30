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
                                         MarketId marketId, UpdatedAt updatedAt, Money amount,UserId userId) {
        return new CurrencyBalance(balanceId, portfolioId, marketId, updatedAt, amount, userId);
    }

    public void updateMoney(Money money) {
        this.amount = money;
    }

//    private void addMoney(Money money) {
//        amount = amount.add(money);
//        this.updatedAt = UpdatedAt.now();
//    }
//
//    private void subtractMoney(Money money) {
//        if (amount.getValue().compareTo(money.getValue()) < 0) {
//            throw new PortfolioDomainException("Amount to subtract is insufficient");
//        }
//        amount = amount.subtract(money);
//        this.updatedAt = UpdatedAt.now();
//    }

    public boolean isOverCurrencyBalanceAmount(Long withdrawalAmount) {
        return this.amount.isLessThan(BigDecimal.valueOf(withdrawalAmount));
    }

}
