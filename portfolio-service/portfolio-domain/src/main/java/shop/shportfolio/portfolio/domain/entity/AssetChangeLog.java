package shop.shportfolio.portfolio.domain.entity;

import lombok.Getter;
import shop.shportfolio.common.domain.entity.BaseEntity;
import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.portfolio.domain.exception.PortfolioDomainException;
import shop.shportfolio.portfolio.domain.valueobject.*;

@Getter
public class AssetChangeLog extends BaseEntity<ChangeLogId> {

    private final PortfolioId portfolioId;
    private final ChangeType changeType;
    private final MarketId marketId;
    private Money changeMoney;
    private final Description description;
    private final CreatedAt createdAt;

    public AssetChangeLog(PortfolioId portfolioId, ChangeType changeType, MarketId marketId,
                          Money changeMoney, CreatedAt createdAt) {
        this.portfolioId = portfolioId;
        this.changeType = changeType;
        this.marketId = marketId;
        this.changeMoney = changeMoney;
        this.createdAt = createdAt;
        this.description = changeType.getDefaultDescription();
    }

    public static AssetChangeLog create(PortfolioId portfolioId, ChangeType changeType, MarketId marketId,
                                        Money changeMoney, CreatedAt createdAt) {
        AssetChangeLog assetChangeLog = new AssetChangeLog(portfolioId, changeType, marketId, changeMoney, createdAt);
        assetChangeLog.validateChangeMoneySign();
        return assetChangeLog;
    }

    private void validateChangeMoneySign() {
        if (changeType == ChangeType.DEPOSIT && changeMoney.isNegative()) {
            throw new PortfolioDomainException("Deposit cannot have negative money");
        }
    }

}
