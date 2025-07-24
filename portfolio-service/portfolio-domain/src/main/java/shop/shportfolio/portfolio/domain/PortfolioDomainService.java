package shop.shportfolio.portfolio.domain;

import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.portfolio.domain.entity.*;
import shop.shportfolio.portfolio.domain.valueobject.*;

import java.time.LocalDateTime;

public interface PortfolioDomainService {


    Portfolio createPortfolio(PortfolioId portfolioId,
                              UserId userId, TotalAssetValue totalAssetValue,
                              CreatedAt createdAt, UpdatedAt updatedAt);

    Balance createBalance(BalanceId balanceId, PortfolioId portfolioId, MarketId marketId, Quantity quantity,
                          OrderPrice orderPrice, Money money, UpdatedAt updatedAt);

    AssetChangeLog createAssetChangeLog(ChangeLogId changeLogId,
                                        PortfolioId portfolioId,
                                        ChangeType changeType,
                                        MarketId marketId,
                                        Money changeMoney,
                                        Description description,
                                        CreatedAt createdAt,
                                        UpdatedAt updatedAt);

}
