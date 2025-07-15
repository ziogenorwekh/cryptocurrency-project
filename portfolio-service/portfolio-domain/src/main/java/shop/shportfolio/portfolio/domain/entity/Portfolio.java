package shop.shportfolio.portfolio.domain.entity;

import lombok.Getter;
import shop.shportfolio.common.domain.entity.AggregateRoot;
import shop.shportfolio.common.domain.valueobject.CreatedAt;
import shop.shportfolio.common.domain.valueobject.UpdatedAt;
import shop.shportfolio.common.domain.valueobject.UserId;
import shop.shportfolio.portfolio.domain.valueobject.PortfolioId;
import shop.shportfolio.portfolio.domain.valueobject.TotalAssetValue;

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
}
