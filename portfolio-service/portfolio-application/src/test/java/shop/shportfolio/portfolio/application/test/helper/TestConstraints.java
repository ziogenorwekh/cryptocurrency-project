package shop.shportfolio.portfolio.application.test.helper;

import shop.shportfolio.common.domain.dto.payment.PaymentResponse;
import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.portfolio.domain.entity.CryptoBalance;
import shop.shportfolio.portfolio.domain.entity.CurrencyBalance;
import shop.shportfolio.portfolio.domain.entity.Portfolio;
import shop.shportfolio.portfolio.domain.valueobject.BalanceId;
import shop.shportfolio.portfolio.domain.valueobject.PortfolioId;
import shop.shportfolio.portfolio.domain.valueobject.PurchasePrice;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

public class TestConstraints {

    public static UUID userId = UUID.randomUUID();
    public static String marketId = "KRW-BTC";
    public static UUID portfolioId = UUID.randomUUID();
    public static UUID balanceId = UUID.randomUUID();
    public static BigDecimal quantity = BigDecimal.valueOf(100);
    public static BigDecimal purchasePrice = BigDecimal.valueOf(100_000_0);
    public static CryptoBalance cryptoBalance = CryptoBalance.create(new BalanceId(balanceId),
            new PortfolioId(portfolioId), new MarketId(marketId),
            new PurchasePrice(purchasePrice), new Quantity(quantity), UpdatedAt.now());
    public static BigDecimal money = BigDecimal.valueOf(100_900_0);
    public static String orderId = UUID.randomUUID().toString();
    public static String paymentKey = UUID.randomUUID().toString();
    public static BigDecimal totalAssetValue = BigDecimal.valueOf(100_000_0);
    public static Portfolio portfolio = Portfolio.createPortfolio(
            new PortfolioId(portfolioId),new UserId(userId),CreatedAt.now(),
            UpdatedAt.now()
    );

    public static PaymentResponse paymentResponseDone = new PaymentResponse(paymentKey, orderId, money.longValue(),
            PaymentMethod.EASY_PAY, PaymentStatus.DONE,
            LocalDateTime.now(ZoneOffset.UTC), LocalDateTime.now(ZoneOffset.UTC)
            , "이지페이 결제", "rawResponse");

    public static PaymentResponse paymentResponseFAILED = new PaymentResponse(paymentKey, orderId, money.longValue(),
            PaymentMethod.EASY_PAY, PaymentStatus.ABORTED,
            LocalDateTime.now(ZoneOffset.UTC), LocalDateTime.now(ZoneOffset.UTC)
            , "이지페이 결제", "rawResponse");

    public static CurrencyBalance currencyBalance = CurrencyBalance.create(new BalanceId(balanceId), new PortfolioId(portfolioId),
            new MarketId("KRW"), UpdatedAt.now(), Money.of(money));

    public static CurrencyBalance currencyBalance_1_200_000 = CurrencyBalance.create(new BalanceId(balanceId),
            new PortfolioId(portfolioId),
            new MarketId("KRW"), UpdatedAt.now(), Money.of(BigDecimal.valueOf(1_200_000)));

    public static CurrencyBalance currencyBalance_900_000 = CurrencyBalance.create(new BalanceId(balanceId),
            new PortfolioId(portfolioId),
            new MarketId("KRW"), UpdatedAt.now(), Money.of(BigDecimal.valueOf(900_000)));

    public static Portfolio newPortfolio = Portfolio.createPortfolio(
            new PortfolioId(portfolioId),new UserId(userId),CreatedAt.now()
            ,UpdatedAt.now()
    );

    public static CryptoBalance cryptoBalance1 = CryptoBalance.create(new BalanceId(UUID.randomUUID()),
            new PortfolioId(portfolioId), new MarketId(marketId),
            new PurchasePrice(BigDecimal.valueOf(10000)), new Quantity(BigDecimal.valueOf(150)), UpdatedAt.now());

    public static CryptoBalance cryptoBalance2 = CryptoBalance.create(
            new BalanceId(UUID.randomUUID()), new PortfolioId(portfolioId),
            new MarketId("KRW-XRP"), new PurchasePrice(BigDecimal.valueOf(100)),
            new Quantity(BigDecimal.valueOf(2000)), UpdatedAt.now()
    );

    public static CryptoBalance cryptoBalance3 = CryptoBalance.create(
            new BalanceId(UUID.randomUUID()), new PortfolioId(portfolioId),
            new MarketId("KRW-ADA"), new PurchasePrice(BigDecimal.valueOf(100)),
            new Quantity(BigDecimal.valueOf(1000)), UpdatedAt.now()
    );

    public static CryptoBalance cryptoBalance4 = CryptoBalance.create(
            new BalanceId(UUID.randomUUID()), new PortfolioId(portfolioId),
            new MarketId("KRW-DOGE"), new PurchasePrice(BigDecimal.valueOf(300)),
            new Quantity(BigDecimal.valueOf(5000)), UpdatedAt.now()
    );

    public static CryptoBalance cryptoBalance5 = CryptoBalance.create(
            new BalanceId(UUID.randomUUID()), new PortfolioId(portfolioId),
            new MarketId("KRW-BCH"), new PurchasePrice(BigDecimal.valueOf(500)),
            new Quantity(BigDecimal.valueOf(3)), UpdatedAt.now()
    );

    public static CryptoBalance cryptoBalance6 = CryptoBalance.create(
            new BalanceId(UUID.randomUUID()), new PortfolioId(portfolioId),
            new MarketId("KRW-TRX"), new PurchasePrice(BigDecimal.valueOf(100)),
            new Quantity(BigDecimal.valueOf(7000)), UpdatedAt.now()
    );

    public static CryptoBalance cryptoBalance7 = CryptoBalance.create(
            new BalanceId(UUID.randomUUID()), new PortfolioId(portfolioId),
            new MarketId("KRW-XLM"), new PurchasePrice(BigDecimal.valueOf(200)),
            new Quantity(BigDecimal.valueOf(1500)), UpdatedAt.now()
    );
}
