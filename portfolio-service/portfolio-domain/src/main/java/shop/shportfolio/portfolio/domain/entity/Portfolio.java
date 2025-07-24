package shop.shportfolio.portfolio.domain.entity;

import lombok.Getter;
import shop.shportfolio.common.domain.entity.AggregateRoot;
import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.portfolio.domain.valueobject.GrowthRate;
import shop.shportfolio.portfolio.domain.valueobject.PortfolioId;
import shop.shportfolio.portfolio.domain.valueobject.TotalAssetValue;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Getter
public class Portfolio extends AggregateRoot<PortfolioId> {

    private final UserId userId;
    private TotalAssetValue totalAssetValue;
    private final CreatedAt createdAt;
    private UpdatedAt updatedAt;

    public Portfolio(PortfolioId portfolioId, UserId userId, CreatedAt createdAt,
                     TotalAssetValue totalAssetValue, UpdatedAt updatedAt) {
        setId(portfolioId);
        this.userId = userId;
        this.createdAt = createdAt;
        this.totalAssetValue = totalAssetValue;
        this.updatedAt = updatedAt;
    }


    public static Portfolio createPortfolio(PortfolioId portfolioId, UserId userId, CreatedAt createdAt,
                                            TotalAssetValue totalAssetValue, UpdatedAt updatedAt) {
        return new Portfolio(portfolioId, userId, createdAt, totalAssetValue, updatedAt);
    }

    public void updateAssetValue(TotalAssetValue totalAssetValue) {
        this.totalAssetValue = totalAssetValue;
        this.updatedAt = new UpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
    }


    public PortfolioAssetHistory createNewAssetHistory(PortfolioAssetHistory latestHistory) {
        GrowthRate newGrowthRate;
        if (latestHistory == null) {
            newGrowthRate = GrowthRate.of(BigDecimal.ZERO);
        } else {
            BigDecimal previousValue = latestHistory.getTotalAssetValue().getValue();
            BigDecimal currentValue = this.totalAssetValue.getValue();
            if (previousValue.compareTo(BigDecimal.ZERO) == 0) {
                newGrowthRate = GrowthRate.of(BigDecimal.ZERO);
            } else {
                BigDecimal rateValue = currentValue.subtract(previousValue)
                        .divide(previousValue, 8, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100));
                newGrowthRate = new GrowthRate(rateValue);
            }
        }
        return PortfolioAssetHistory.create(
                this.getId(),
                CreatedAt.now(),
                this.totalAssetValue,
                newGrowthRate
        );
    }

}
