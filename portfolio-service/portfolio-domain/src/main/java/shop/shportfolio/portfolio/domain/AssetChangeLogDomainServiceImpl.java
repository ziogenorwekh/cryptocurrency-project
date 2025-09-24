package shop.shportfolio.portfolio.domain;

import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.portfolio.domain.entity.AssetChangeLog;
import shop.shportfolio.portfolio.domain.valueobject.ChangeLogId;
import shop.shportfolio.portfolio.domain.valueobject.ChangeType;
import shop.shportfolio.portfolio.domain.valueobject.PortfolioId;

public class AssetChangeLogDomainServiceImpl implements AssetChangeLogDomainService {
    @Override
    public AssetChangeLog createAssetChangeLog(ChangeLogId changeLogId, PortfolioId portfolioId, UserId userId,
                                               ChangeType changeType, MarketId marketId,
                                               Money changeMoney,
                                               CreatedAt createdAt,TransactionStatus transactionStatus) {
        return AssetChangeLog.create(changeLogId, portfolioId, userId, changeType, marketId, changeMoney, createdAt
                , transactionStatus);
    }
}
