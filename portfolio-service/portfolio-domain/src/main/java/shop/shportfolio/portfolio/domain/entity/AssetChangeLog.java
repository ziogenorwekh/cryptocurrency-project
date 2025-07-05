package shop.shportfolio.portfolio.domain.entity;

import shop.shportfolio.common.domain.entity.BaseEntity;
import shop.shportfolio.common.domain.valueobject.CreatedAt;
import shop.shportfolio.common.domain.valueobject.Description;
import shop.shportfolio.common.domain.valueobject.MarketId;
import shop.shportfolio.common.domain.valueobject.UpdatedAt;
import shop.shportfolio.portfolio.domain.valueobject.*;

public class AssetChangeLog extends BaseEntity<ChangeLogId> {

    private final PortfolioId portfolioId;
    private final ChangeType changeType;
    private final MarketId marketId;
    private final Amount changeAmount;
    private final ChangeDate changeDate;
    private final Description description;
    private final CreatedAt createdAt;
    private UpdatedAt updatedAt;

    public AssetChangeLog(PortfolioId portfolioId, ChangeType changeType, MarketId marketId,
                          Amount changeAmount, ChangeDate changeDate, Description description,
                          CreatedAt createdAt, UpdatedAt updatedAt) {
        this.portfolioId = portfolioId;
        this.changeType = changeType;
        this.marketId = marketId;
        this.changeAmount = changeAmount;
        this.changeDate = changeDate;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
