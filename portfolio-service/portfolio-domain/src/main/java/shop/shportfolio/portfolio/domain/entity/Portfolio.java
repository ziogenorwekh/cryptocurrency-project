package shop.shportfolio.portfolio.domain.entity;

import lombok.Getter;
import shop.shportfolio.common.domain.entity.AggregateRoot;
import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.portfolio.domain.valueobject.GrowthRate;
import shop.shportfolio.portfolio.domain.valueobject.PortfolioId;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
public class Portfolio extends AggregateRoot<PortfolioId> {

    private final UserId userId;
    private final CreatedAt createdAt;
    private UpdatedAt updatedAt;

    public Portfolio(PortfolioId portfolioId, UserId userId, CreatedAt createdAt,
                     UpdatedAt updatedAt) {
        setId(portfolioId);
        this.userId = userId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }


    public static Portfolio createPortfolio(PortfolioId portfolioId, UserId userId, CreatedAt createdAt,
                                            UpdatedAt updatedAt) {
        return new Portfolio(portfolioId, userId, createdAt, updatedAt);
    }
}
