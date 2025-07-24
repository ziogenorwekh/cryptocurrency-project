package shop.shportfolio.portfolio.domain.entity;

import lombok.Builder;
import lombok.Getter;
import shop.shportfolio.common.domain.entity.BaseEntity;
import shop.shportfolio.common.domain.valueobject.CreatedAt;
import shop.shportfolio.portfolio.domain.valueobject.GrowthRate;
import shop.shportfolio.portfolio.domain.valueobject.PortfolioGrowthId;
import shop.shportfolio.portfolio.domain.valueobject.PortfolioId;
import shop.shportfolio.portfolio.domain.valueobject.TotalAssetValue;

@Getter
public class PortfolioAssetHistory extends BaseEntity<PortfolioGrowthId> {
    private final PortfolioId portfolioId;
    private final CreatedAt recordedAt;       // 언제 기록된 데이터인가
    private final TotalAssetValue totalAssetValue; // 그 시점 총 자산 가치
    private final GrowthRate growthRate;

    @Builder
    public PortfolioAssetHistory(PortfolioId portfolioId, CreatedAt recordedAt,
                                 TotalAssetValue totalAssetValue, GrowthRate growthRate) {
        this.portfolioId = portfolioId;
        this.recordedAt = recordedAt;
        this.totalAssetValue = totalAssetValue;
        this.growthRate = growthRate;
    }

    public static PortfolioAssetHistory create(PortfolioId portfolioId,
                                               CreatedAt recordedAt,
                                               TotalAssetValue totalAssetValue,
                                               GrowthRate growthRate) {
        return new PortfolioAssetHistory(portfolioId, recordedAt, totalAssetValue, growthRate);
    }

}
