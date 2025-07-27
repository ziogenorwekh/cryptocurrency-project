package shop.shportfolio.portfolio.domain;

import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.portfolio.domain.entity.AssetChangeLog;
import shop.shportfolio.portfolio.domain.entity.Balance;
import shop.shportfolio.portfolio.domain.entity.Portfolio;
import shop.shportfolio.portfolio.domain.entity.PortfolioAssetHistory;
import shop.shportfolio.portfolio.domain.valueobject.*;

public class PortfolioDomainServiceImpl implements PortfolioDomainService {
    @Override
    public Portfolio createPortfolio(PortfolioId portfolioId, UserId userId, TotalAssetValue totalAssetValue, CreatedAt createdAt, UpdatedAt updatedAt) {
        return Portfolio.createPortfolio(portfolioId, userId, createdAt, totalAssetValue, updatedAt);
    }

    @Override
    public Balance createBalance(BalanceId balanceId, PortfolioId portfolioId, MarketId marketId,
                                 Quantity quantity, PurchasePrice purchasePrice, Money money, UpdatedAt updatedAt) {
        return Balance.create(balanceId,portfolioId,marketId,purchasePrice,quantity,updatedAt);
    }

    @Override
    public AssetChangeLog createAssetChangeLog(ChangeLogId changeLogId, PortfolioId portfolioId, ChangeType changeType, MarketId marketId, Money changeMoney, Description description, CreatedAt createdAt, UpdatedAt updatedAt) {
        return null;
    }

    @Override
    public void updateTotalAssetValue(Portfolio portfolio, TotalAssetValue totalAssetValue) {
        portfolio.updateAssetValue(totalAssetValue);
    }

    @Override
    public PortfolioAssetHistory createPortfolioAssetHistory(Portfolio portfolio, PortfolioAssetHistory portfolioAssetHistory) {
        return portfolio.createNewAssetHistory(portfolioAssetHistory);
    }

    @Override
    public void addPurchase(Balance balance, PurchasePrice purchasePrice, Quantity amount) {
        balance.addPurchase(purchasePrice,amount);
    }

    @Override
    public void subtractQuantity(Balance balance, Quantity quantity) {
        balance.subtractQuantity(quantity);
    }
}
