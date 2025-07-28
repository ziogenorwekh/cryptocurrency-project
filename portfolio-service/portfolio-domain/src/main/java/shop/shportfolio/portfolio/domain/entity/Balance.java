package shop.shportfolio.portfolio.domain.entity;

import lombok.Getter;
import shop.shportfolio.common.domain.entity.BaseEntity;
import shop.shportfolio.common.domain.valueobject.MarketId;
import shop.shportfolio.common.domain.valueobject.UpdatedAt;
import shop.shportfolio.portfolio.domain.valueobject.*;

@Getter
public class Balance extends BaseEntity<BalanceId> {

    protected final PortfolioId portfolioId;
    protected final MarketId marketId;
    protected UpdatedAt updatedAt;

    public Balance(BalanceId balanceId, PortfolioId portfolioId,
                   MarketId marketId, UpdatedAt updatedAt) {
        setId(balanceId);
        this.portfolioId = portfolioId;
        this.marketId = marketId;
        this.updatedAt = updatedAt;
    }
}
