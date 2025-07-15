package shop.shportfolio.trading.application.test.helper;

import shop.shportfolio.trading.domain.entity.MarketItem;
import shop.shportfolio.trading.domain.valueobject.*;

import java.math.BigDecimal;
import java.util.UUID;

public class TestConstants {
    public static final UUID TEST_USER_ID = UUID.randomUUID();
    public static final String TEST_MARKET_ID = "BTC-KRW";
    public static final MarketStatus MARKET_STATUS = MarketStatus.ACTIVE;


    public static final BigDecimal ORDER_PRICE = BigDecimal.valueOf(1_050_000);
    public static final String ORDER_SIDE = "BUY";
    public static final BigDecimal QUANTITY = BigDecimal.valueOf(5L);
    public static final OrderType ORDER_TYPE_LIMIT = OrderType.LIMIT;
    public static final OrderType ORDER_TYPE_MARKET = OrderType.MARKET;

    public static final MarketItem MARKET_ITEM = MarketItem.createMarketItem(
            TEST_MARKET_ID,
            new MarketKoreanName("비트코인"),
            new MarketEnglishName("BTC"),
            new MarketWarning(""),
            new TickPrice(BigDecimal.valueOf(1000L)),
            MARKET_STATUS
    );
}
