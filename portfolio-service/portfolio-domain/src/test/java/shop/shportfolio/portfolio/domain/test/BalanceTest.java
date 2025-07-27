package shop.shportfolio.portfolio.domain.test;

import org.junit.jupiter.api.TestInstance;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import shop.shportfolio.common.domain.valueobject.MarketId;
import shop.shportfolio.common.domain.valueobject.Quantity;
import shop.shportfolio.common.domain.valueobject.UpdatedAt;
import shop.shportfolio.common.domain.valueobject.UserId;
import shop.shportfolio.portfolio.domain.entity.Balance;
import shop.shportfolio.portfolio.domain.exception.PortfolioDomainException;
import shop.shportfolio.portfolio.domain.valueobject.BalanceId;
import shop.shportfolio.portfolio.domain.valueobject.PortfolioId;
import shop.shportfolio.portfolio.domain.valueobject.PurchasePrice;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class BalanceTest {
    private Balance balance;

    @BeforeEach
    public void setUp() {
        balance = new Balance(
                new BalanceId(UUID.randomUUID()),
                new PortfolioId(UUID.randomUUID()),
                new MarketId("market-1"),
                new Quantity(BigDecimal.valueOf(10)),
                new PurchasePrice(BigDecimal.valueOf(1000)),
                UpdatedAt.now()
        );
    }

    @Test
    @DisplayName("매수 시 매수평균가와 수량이 갱신되어야 한다")
    public void testAddPurchase() {
        // given
        PurchasePrice newPrice = new PurchasePrice(BigDecimal.valueOf(1200));
        Quantity newQuantity = new Quantity(BigDecimal.valueOf(5));

        balance.addPurchase(newPrice, newQuantity);

        BigDecimal expectedQuantity = BigDecimal.valueOf(15);
        BigDecimal expectedAvgPrice = (BigDecimal.valueOf(1000).multiply(BigDecimal.valueOf(10))
                .add(BigDecimal.valueOf(1200).multiply(BigDecimal.valueOf(5))))
                .divide(expectedQuantity, 8, RoundingMode.HALF_UP);

        org.junit.jupiter.api.Assertions.assertEquals(expectedQuantity, balance.getQuantity().getValue());
        org.junit.jupiter.api.Assertions.assertEquals(expectedAvgPrice, balance.getPurchasePrice().getValue());
    }

    @Test
    @DisplayName("수량 차감 시 정상 처리되어야 한다")
    public void testSubtractQuantity() {
        Quantity subtractAmount = new Quantity(BigDecimal.valueOf(5));
        balance.subtractQuantity(subtractAmount);

        org.junit.jupiter.api.Assertions.assertEquals(BigDecimal.valueOf(5), balance.getQuantity().getValue());
    }

    @Test
    @DisplayName("차감 수량이 보유 수량보다 많으면 예외가 발생해야 한다")
    public void testSubtractQuantity_InsufficientQuantity() {
        Quantity subtractAmount = new Quantity(BigDecimal.valueOf(15));

        org.junit.jupiter.api.Assertions.assertThrows(PortfolioDomainException.class, () -> {
            balance.subtractQuantity(subtractAmount);
        });
    }
}
