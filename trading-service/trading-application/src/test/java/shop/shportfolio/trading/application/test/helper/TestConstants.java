package shop.shportfolio.trading.application.test.helper;

import shop.shportfolio.common.domain.valueobject.MarketId;
import shop.shportfolio.common.domain.valueobject.OrderPrice;
import shop.shportfolio.common.domain.valueobject.Quantity;
import shop.shportfolio.common.domain.valueobject.UserId;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.orderbook.MarketItem;
import shop.shportfolio.trading.domain.entity.ReservationOrder;
import shop.shportfolio.trading.domain.entity.userbalance.UserBalance;
import shop.shportfolio.trading.domain.valueobject.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    public static final ReservationOrder RESERVATION_ORDER = ReservationOrder.createReservationOrder(
            new UserId(TEST_USER_ID), new MarketId(TEST_MARKET_ID), OrderSide.BUY, new Quantity(QUANTITY),
            OrderType.RESERVATION, TriggerCondition.of(TriggerType.BELOW, new OrderPrice(ORDER_PRICE)),
            new ScheduledTime(LocalDateTime.now().plusDays(1)), new ExpireAt(LocalDateTime.now().plusMonths(1)),
            new IsRepeatable(true)
    );

    public static final UserBalance USER_BALANCE_1_900_000 = UserBalance.createUserBalance(
            new UserBalanceId(UUID.randomUUID()), new UserId(TEST_USER_ID), AssetCode.KRW,
            Money.of(BigDecimal.valueOf(1_900_000)),null
    );

    public static final UserBalance USER_BALANCE_1_050_000 = UserBalance.createUserBalance(
            new UserBalanceId(UUID.randomUUID()), new UserId(TEST_USER_ID), AssetCode.KRW,
            Money.of(BigDecimal.valueOf(1_050_000)),null
    );

    public static final UserBalance USER_BALANCE_A_LOT_OF_MONEY = UserBalance.createUserBalance(
            new UserBalanceId(UUID.randomUUID()), new UserId(TEST_USER_ID), AssetCode.KRW,
            Money.of(BigDecimal.valueOf(1_050_000_000)),null
    );
}
