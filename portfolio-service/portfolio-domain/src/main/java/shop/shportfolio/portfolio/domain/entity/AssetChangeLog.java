package shop.shportfolio.portfolio.domain.entity;

import lombok.Getter;
import shop.shportfolio.common.domain.entity.BaseEntity;
import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.portfolio.domain.exception.PortfolioDomainException;
import shop.shportfolio.portfolio.domain.valueobject.*;

@Getter
public class AssetChangeLog extends BaseEntity<ChangeLogId> {

    private final PortfolioId portfolioId;
    private final UserId userId;
    private final ChangeType changeType;
    private final MarketId marketId;
    private Money changeMoney;
    private final Description description;
    private final CreatedAt createdAt;

    public AssetChangeLog(ChangeLogId changeLogId, PortfolioId portfolioId, UserId userId, ChangeType changeType, MarketId marketId,
                          Money changeMoney, CreatedAt createdAt) {
        this.userId = userId;
        setId(changeLogId);
        this.portfolioId = portfolioId;
        this.changeType = changeType;
        this.marketId = marketId;
        this.changeMoney = changeMoney;
        this.createdAt = createdAt;
        this.description = changeType.getDefaultDescription();
    }

    public static AssetChangeLog create(ChangeLogId changeLogId,PortfolioId portfolioId, UserId userId,
                                        ChangeType changeType, MarketId marketId,
                                        Money changeMoney, CreatedAt createdAt) {
        AssetChangeLog assetChangeLog = new AssetChangeLog(changeLogId, portfolioId, userId, changeType, marketId, changeMoney, createdAt);
        assetChangeLog.validateChangeMoneySign();
        return assetChangeLog;
    }

    private void validateChangeMoneySign() {
        if (changeType == ChangeType.DEPOSIT && changeMoney.isNegative()) {
            throw new PortfolioDomainException("Deposit cannot have negative money");
        }
    }

}
