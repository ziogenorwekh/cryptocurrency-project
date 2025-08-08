package shop.shportfolio.trading.application.test.helper;

import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.orderbook.MarketItem;
import shop.shportfolio.trading.domain.entity.ReservationOrder;
import shop.shportfolio.trading.domain.entity.userbalance.UserBalance;
import shop.shportfolio.trading.domain.valueobject.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
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

    public static final LimitOrder LIMIT_ORDER = LimitOrder.createLimitOrder(
            new UserId(TEST_USER_ID),
            new MarketId(TEST_MARKET_ID),
            OrderSide.BUY,
            new Quantity(BigDecimal.valueOf(1.0)),
            new OrderPrice(BigDecimal.valueOf(1_050_000.0)),
            OrderType.LIMIT
    );

    public static final LimitOrder LIMIT_ORDER2 = LimitOrder.createLimitOrder(
            new UserId(UUID.randomUUID()),
            new MarketId(TEST_MARKET_ID),
            OrderSide.SELL,
            new Quantity(BigDecimal.valueOf(1.2)),
            new OrderPrice(BigDecimal.valueOf(1_030_000.0)),
            OrderType.LIMIT
    );
    public static final LimitOrder LIMIT_ORDER3 = LimitOrder.createLimitOrder(
            new UserId(UUID.randomUUID()),
            new MarketId(TEST_MARKET_ID),
            OrderSide.SELL,
            new Quantity(BigDecimal.valueOf(0.3)),
            new OrderPrice(BigDecimal.valueOf(1_020_000.0)),
            OrderType.LIMIT
    );
    public static final LimitOrder LIMIT_ORDER4 = LimitOrder.createLimitOrder(
            new UserId(UUID.randomUUID()),
            new MarketId(TEST_MARKET_ID),
            OrderSide.SELL,
            new Quantity(BigDecimal.valueOf(0.4)),
            new OrderPrice(BigDecimal.valueOf(1_030_000.0)),
            OrderType.LIMIT
    );

    public static final ReservationOrder RESERVATION_ORDER = ReservationOrder.createReservationOrder(
            new UserId(TEST_USER_ID), new MarketId(TEST_MARKET_ID), OrderSide.BUY, new Quantity(QUANTITY),
            OrderType.RESERVATION, TriggerCondition.of(TriggerType.BELOW, new OrderPrice(ORDER_PRICE)),
            new ScheduledTime(LocalDateTime.now(ZoneOffset.UTC).plusDays(1)),
            new ExpireAt(LocalDateTime.now(ZoneOffset.UTC).plusMonths(1)),
            new IsRepeatable(true)
    );

    public static BigDecimal USER_BALANCE_1_900_000 = BigDecimal.valueOf(1_900_000);

    public static BigDecimal USER_BALANCE_1_050_000 =BigDecimal.valueOf(1_050_000);

    public static BigDecimal USER_BALANCE_A_LOT_OF_MONEY = BigDecimal.valueOf(1_050_000_000);


    public static UserBalance createUserBalance(BigDecimal amount) {
        return UserBalance.createUserBalance(
                new UserBalanceId(UUID.randomUUID()),
                new UserId(UUID.randomUUID()),
                AssetCode.KRW,
                Money.of(amount),
                null
        );
    }
}
