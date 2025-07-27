package shop.shportfolio.portfolio.domain;

import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.portfolio.domain.entity.*;
import shop.shportfolio.portfolio.domain.valueobject.*;

public interface PortfolioDomainService {


    Portfolio createPortfolio(PortfolioId portfolioId,
                              UserId userId, TotalAssetValue totalAssetValue,
                              CreatedAt createdAt, UpdatedAt updatedAt);

    Balance createBalance(BalanceId balanceId, PortfolioId portfolioId, MarketId marketId, Quantity quantity,
                          PurchasePrice purchasePrice, Money money, UpdatedAt updatedAt);

    AssetChangeLog createAssetChangeLog(ChangeLogId changeLogId,
                                        PortfolioId portfolioId,
                                        ChangeType changeType,
                                        MarketId marketId,
                                        Money changeMoney,
                                        Description description,
                                        CreatedAt createdAt,
                                        UpdatedAt updatedAt);

    void updateTotalAssetValue(Portfolio portfolio, TotalAssetValue totalAssetValue);

    PortfolioAssetHistory createPortfolioAssetHistory(Portfolio portfolio,
                                                      PortfolioAssetHistory portfolioAssetHistory);


    void addPurchase(Balance balance, PurchasePrice purchasePrice,Quantity amount);

    void subtractQuantity(Balance balance, Quantity quantity);


}
