package shop.shportfolio.portfolio.domain.entity;

import lombok.Getter;
import shop.shportfolio.common.domain.entity.BaseEntity;
import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.portfolio.domain.valueobject.*;

@Getter
public class AssetChangeLog extends BaseEntity<ChangeLogId> {

    private final PortfolioId portfolioId;
    private final ChangeType changeType;
    private final MarketId marketId;
    private final Amount changeAmount;
    private final ChangeDate changeDate;
    private final Description description;
    private final CreatedAt createdAt;
    private final UpdatedAt updatedAt;

    public AssetChangeLog(PortfolioId portfolioId, ChangeType changeType, MarketId marketId,
                          Amount changeAmount, ChangeDate changeDate,
                          CreatedAt createdAt, UpdatedAt updatedAt) {
        this.portfolioId = portfolioId;
        this.changeType = changeType;
        this.marketId = marketId;
        this.changeAmount = changeAmount;
        this.changeDate = changeDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.description = changeType.getDefaultDescription();
    }
}
