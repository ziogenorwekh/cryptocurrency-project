package shop.shportfolio.portfolio.domain.entity;

import lombok.Getter;
import shop.shportfolio.common.domain.entity.BaseEntity;
import shop.shportfolio.common.domain.valueobject.MarketId;
import shop.shportfolio.common.domain.valueobject.UpdatedAt;
import shop.shportfolio.portfolio.domain.valueobject.PortfolioId;
import shop.shportfolio.portfolio.domain.valueobject.ProfitLossId;
import shop.shportfolio.portfolio.domain.valueobject.RealizedProfitLoss;
import shop.shportfolio.portfolio.domain.valueobject.UnrealizedProfitLoss;

import java.time.LocalDateTime;

@Getter
public class ProfitLoss extends BaseEntity<ProfitLossId> {

    private final PortfolioId portfolioId;
    private final MarketId marketId;
    private final RealizedProfitLoss realizedProfitLoss;
    private final UnrealizedProfitLoss unrealizedProfitLoss;
    private final LocalDateTime timestamp;
    private UpdatedAt updatedAt;

    public ProfitLoss(ProfitLossId profitLossId, PortfolioId portfolioId,
                      MarketId marketId, RealizedProfitLoss realizedProfitLoss,
                      UnrealizedProfitLoss unrealizedProfitLoss, LocalDateTime timestamp, UpdatedAt updatedAt) {
        setId(profitLossId);
        this.portfolioId = portfolioId;
        this.marketId = marketId;
        this.realizedProfitLoss = realizedProfitLoss;
        this.unrealizedProfitLoss = unrealizedProfitLoss;
        this.timestamp = timestamp;
        this.updatedAt = updatedAt;
    }
}
