package shop.shportfolio.portfolio.domain;

import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.portfolio.domain.entity.*;
import shop.shportfolio.portfolio.domain.valueobject.*;

public class PortfolioDomainServiceImpl implements PortfolioDomainService {
    @Override
    public Portfolio createPortfolio(PortfolioId portfolioId, UserId userId, CreatedAt createdAt, UpdatedAt updatedAt) {
        return Portfolio.createPortfolio(portfolioId, userId, createdAt, updatedAt);
    }

    @Override
    public CryptoBalance createCryptoBalance(BalanceId balanceId, PortfolioId portfolioId, MarketId marketId,
                                             Quantity quantity, PurchasePrice purchasePrice, UpdatedAt updatedAt) {
        return CryptoBalance.create(balanceId, portfolioId, marketId, purchasePrice, quantity, updatedAt);
    }

    @Override
    public CurrencyBalance createCurrencyBalance(BalanceId balanceId, PortfolioId portfolioId,
                                                 MarketId marketId, Money money, UpdatedAt updatedAt,UserId userId) {
        return CurrencyBalance.create(balanceId, portfolioId, marketId, updatedAt, money, userId);
    }


    @Override
    public void addPurchase(CryptoBalance balance, PurchasePrice purchasePrice, Quantity amount) {
        balance.addPurchase(purchasePrice, amount);
    }

    @Override
    public void subtractQuantity(CryptoBalance balance, Quantity quantity) {
        balance.subtractQuantity(quantity);
    }

    @Override
    public void updateMoney(CurrencyBalance currencyBalance, Money money) {
        currencyBalance.updateMoney(money);
    }

}
