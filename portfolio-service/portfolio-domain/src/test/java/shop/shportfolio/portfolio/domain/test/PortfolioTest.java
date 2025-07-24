package shop.shportfolio.portfolio.domain.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import shop.shportfolio.common.domain.valueobject.CreatedAt;
import shop.shportfolio.common.domain.valueobject.UpdatedAt;
import shop.shportfolio.common.domain.valueobject.UserId;
import shop.shportfolio.portfolio.domain.entity.Portfolio;
import shop.shportfolio.portfolio.domain.entity.PortfolioAssetHistory;
import shop.shportfolio.portfolio.domain.valueobject.GrowthRate;
import shop.shportfolio.portfolio.domain.valueobject.PortfolioId;
import shop.shportfolio.portfolio.domain.valueobject.TotalAssetValue;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PortfolioTest {

    @Test
    @DisplayName("Should create PortfolioAssetHistory with zero growth rate when there is no previous history")
    void createNewAssetHistoryWithoutPreviousHistory() {
        PortfolioId portfolioId = new PortfolioId(UUID.randomUUID());
        UserId userId = new UserId(UUID.randomUUID());
        CreatedAt createdAt = CreatedAt.now();
        UpdatedAt updatedAt = new UpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
        TotalAssetValue totalAssetValue = TotalAssetValue.of(BigDecimal.valueOf(1_000_000));

        Portfolio portfolio = Portfolio.createPortfolio(portfolioId, userId, createdAt, totalAssetValue, updatedAt);

        PortfolioAssetHistory history = portfolio.createNewAssetHistory(null);

        Assertions.assertEquals(0, history.getGrowthRate().getValue().compareTo(BigDecimal.ZERO));

        Assertions.assertEquals(portfolioId, history.getPortfolioId());
        Assertions.assertEquals(totalAssetValue, history.getTotalAssetValue());
    }

    @Test
    @DisplayName("Should calculate growth rate correctly when previous history exists")
    void createNewAssetHistoryWithPreviousHistory() {
        PortfolioId portfolioId = new PortfolioId(UUID.randomUUID());
        UserId userId = new UserId(UUID.randomUUID());
        CreatedAt createdAt = CreatedAt.now();
        UpdatedAt updatedAt = new UpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
        TotalAssetValue previousValue = TotalAssetValue.of(BigDecimal.valueOf(1_000_000));
        TotalAssetValue currentValue = TotalAssetValue.of(BigDecimal.valueOf(1_100_000));

        PortfolioAssetHistory previousHistory = PortfolioAssetHistory.create(
                portfolioId,
                CreatedAt.now(),
                previousValue,
                GrowthRate.of(BigDecimal.ZERO)
        );

        Portfolio portfolio = Portfolio.createPortfolio(portfolioId, userId, createdAt, currentValue, updatedAt);

        PortfolioAssetHistory newHistory = portfolio.createNewAssetHistory(previousHistory);

        BigDecimal expectedRate = BigDecimal.valueOf(10.00000000); // 10%
        Assertions.assertEquals(0, expectedRate.compareTo(newHistory.getGrowthRate().getValue()));
    }
}
