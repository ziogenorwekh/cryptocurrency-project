package shop.shportfolio.portfolio.application.test.helper;

import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.portfolio.domain.entity.Balance;
import shop.shportfolio.portfolio.domain.valueobject.BalanceId;
import shop.shportfolio.portfolio.domain.valueobject.PortfolioId;
import shop.shportfolio.portfolio.domain.valueobject.PurchasePrice;
import shop.shportfolio.portfolio.domain.view.UserBalanceView;

import java.math.BigDecimal;
import java.util.UUID;

public class PortfolioTestConstraints {

    public static UUID userId = UUID.randomUUID();
    public static String marketId = "KRW-BTC";
    public static UUID portfolioId = UUID.randomUUID();
    public static UUID balanceId = UUID.randomUUID();
    public static BigDecimal quantity = BigDecimal.valueOf(100);
    public static BigDecimal purchasePrice = BigDecimal.valueOf(100_000_0);
    public static Balance balance = Balance.createBalance(new BalanceId(balanceId),
            new PortfolioId(portfolioId), new MarketId(marketId),
            new PurchasePrice(purchasePrice), new Quantity(quantity), UpdatedAt.now());
    public static BigDecimal money = BigDecimal.valueOf(100_900_0);
    public static UserBalanceView userBalanceView = UserBalanceView.builder()
            .userId(new UserId(userId))
            .money(new Money(money))
            .assetCode(AssetCode.KRW)
            .build();
}
