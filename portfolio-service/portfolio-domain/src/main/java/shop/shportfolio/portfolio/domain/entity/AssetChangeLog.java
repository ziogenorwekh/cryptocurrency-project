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
    private final MarketId marketId;
    private final Money changeMoney;
    private final ChangeType changeType;
    private final TransactionStatus transactionStatus;
    private final Description description;
    private final CreatedAt createdAt;

    public AssetChangeLog(ChangeLogId changeLogId, PortfolioId portfolioId, UserId userId, ChangeType changeType, MarketId marketId,
                          Money changeMoney, TransactionStatus transactionStatus, CreatedAt createdAt, Description description) {
        this.userId = userId;
        this.transactionStatus = transactionStatus;
        setId(changeLogId);
        this.portfolioId = portfolioId;
        this.changeType = changeType;
        this.marketId = marketId;
        this.changeMoney = changeMoney;
        this.createdAt = createdAt;
        this.description = description;
    }

    public static AssetChangeLog create(ChangeLogId changeLogId,PortfolioId portfolioId, UserId userId,
                                        ChangeType changeType, MarketId marketId,
                                        Money changeMoney, CreatedAt createdAt,TransactionStatus transactionStatus) {
        AssetChangeLog assetChangeLog = new AssetChangeLog(changeLogId, portfolioId, userId,
                changeType, marketId, changeMoney, transactionStatus, createdAt,changeType.getDefaultDescription());
        assetChangeLog.validateChangeMoneySign();
        return assetChangeLog;
    }

    private void validateChangeMoneySign() {
        if (changeType == ChangeType.DEPOSIT && changeMoney.isNegative()) {
            throw new PortfolioDomainException("Deposit cannot have negative money");
        }
    }


    @Override
    public String toString() {
        return "AssetChangeLog{" +
                "portfolioId=" + portfolioId.getValue() +
                ", userId=" + userId.getValue() +
                ", changeType=" + changeType.toString() +
                ", marketId=" + marketId.getValue() +
                ", changeMoney=" + changeMoney.getValue() +
                ", description=" + description.getValue() +
                ", createdAt=" + createdAt.toString() +
                '}';
    }
}
