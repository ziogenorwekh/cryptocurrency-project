package shop.shportfolio.matching.application.test.helper;

import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.MarketOrder;
import shop.shportfolio.trading.domain.entity.ReservationOrder;
import shop.shportfolio.trading.domain.valueobject.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

public class TestConstants {

    public static final String TEST_MARKET_ID = "KRW-BTC";
    public static final UUID TEST_USER_ID = UUID.randomUUID();

    public static final LimitOrder LIMIT_ORDER_BUY = new LimitOrder(
            new OrderId(UUID.randomUUID().toString()),
            new UserId(TEST_USER_ID),
            new MarketId(TEST_MARKET_ID),
            OrderSide.BUY,
            new Quantity(BigDecimal.valueOf(2.0)),
            new Quantity(BigDecimal.valueOf(2.0)),
            new OrderPrice(BigDecimal.valueOf(1_050_000.0)),
            OrderType.LIMIT,
            CreatedAt.now(),
            OrderStatus.OPEN
    );

    public static final LimitOrder LIMIT_ORDER2_BUY = new LimitOrder(
            OrderId.anonymous(),
            new UserId(UUID.randomUUID()),
            new MarketId(TEST_MARKET_ID),
            OrderSide.BUY,
            new Quantity(BigDecimal.valueOf(1.2)),
            new Quantity(BigDecimal.valueOf(1.2)),
            new OrderPrice(BigDecimal.valueOf(1_000_000.0)),
            OrderType.LIMIT,
            CreatedAt.now(),
            OrderStatus.OPEN
    );

    public static final LimitOrder LIMIT_ORDER3_BUY = new LimitOrder(
            OrderId.anonymous(),
            new UserId(UUID.randomUUID()),
            new MarketId(TEST_MARKET_ID),
            OrderSide.BUY,
            new Quantity(BigDecimal.valueOf(1.5)),
            new Quantity(BigDecimal.valueOf(1.5)),
            new OrderPrice(BigDecimal.valueOf(1_010_000.0)),
            OrderType.LIMIT,
            CreatedAt.now(),
            OrderStatus.OPEN
    );
    public static final LimitOrder LIMIT_ORDER4_BUY = new LimitOrder(
            OrderId.anonymous(),
            new UserId(UUID.randomUUID()),
            new MarketId(TEST_MARKET_ID),
            OrderSide.BUY,
            new Quantity(BigDecimal.valueOf(1.3)),
            new Quantity(BigDecimal.valueOf(1.3)),
            new OrderPrice(BigDecimal.valueOf(1_020_000.0)),
            OrderType.LIMIT,
            CreatedAt.now(),
            OrderStatus.OPEN
    );
    public static final LimitOrder LIMIT_ORDER_SELL = new LimitOrder(
            OrderId.anonymous(),
            new UserId(TEST_USER_ID),
            new MarketId(TEST_MARKET_ID),
            OrderSide.SELL,
            new Quantity(BigDecimal.valueOf(1.0)),
            new Quantity(BigDecimal.valueOf(1.0)),
            new OrderPrice(BigDecimal.valueOf(1_000_000.0)),
            OrderType.LIMIT,
            CreatedAt.now(),
            OrderStatus.OPEN
    );
    public static final LimitOrder LIMIT_ORDER1_SELL = new LimitOrder(
            OrderId.anonymous(),
            new UserId(UUID.randomUUID()),
            new MarketId(TEST_MARKET_ID),
            OrderSide.SELL,
            new Quantity(BigDecimal.valueOf(1.0)),
            new Quantity(BigDecimal.valueOf(1.0)),
            new OrderPrice(BigDecimal.valueOf(1_000_000.0)),
            OrderType.LIMIT,
            CreatedAt.now(),
            OrderStatus.OPEN
    );

    public static final LimitOrder LIMIT_ORDER2_SELL = new LimitOrder(
            OrderId.anonymous(),
            new UserId(UUID.randomUUID()),
            new MarketId(TEST_MARKET_ID),
            OrderSide.SELL,
            new Quantity(BigDecimal.valueOf(1.2)),
            new Quantity(BigDecimal.valueOf(1.2)),
            new OrderPrice(BigDecimal.valueOf(1_030_000.0)),
            OrderType.LIMIT,
            CreatedAt.now(),
            OrderStatus.OPEN
    );
    public static final LimitOrder LIMIT_ORDER3_SELL = new LimitOrder(
            OrderId.anonymous(),
            new UserId(UUID.randomUUID()),
            new MarketId(TEST_MARKET_ID),
            OrderSide.SELL,
            new Quantity(BigDecimal.valueOf(0.3)),
            new Quantity(BigDecimal.valueOf(0.3)),
            new OrderPrice(BigDecimal.valueOf(1_020_000.0)),
            OrderType.LIMIT,
            CreatedAt.now(),
            OrderStatus.OPEN
    );
    public static final LimitOrder LIMIT_ORDER4_SELL = new LimitOrder(
            OrderId.anonymous(),
            new UserId(UUID.randomUUID()),
            new MarketId(TEST_MARKET_ID),
            OrderSide.SELL,
            new Quantity(BigDecimal.valueOf(0.4)),
            new Quantity(BigDecimal.valueOf(0.4)),
            new OrderPrice(BigDecimal.valueOf(1_030_000.0)),
            OrderType.LIMIT,
            CreatedAt.now(),
            OrderStatus.OPEN
    );

    public static ReservationOrder RESERVATION_ORDER_BUY = ReservationOrder.createReservationOrder(
            new UserId(TEST_USER_ID), new MarketId(TEST_MARKET_ID), OrderSide.BUY, new Quantity(BigDecimal.ONE),
            OrderType.RESERVATION, TriggerCondition.of(TriggerType.BELOW, new OrderPrice(
                    BigDecimal.valueOf(1_030_000.0))),
            new ScheduledTime(LocalDateTime.now(ZoneOffset.UTC).minusMinutes(1)),
            new ExpireAt(LocalDateTime.now(ZoneOffset.UTC).plusMonths(1)),
            new IsRepeatable(true)
    );

    public static ReservationOrder RESERVATION_ORDER_SELL = ReservationOrder.createReservationOrder(
            new UserId(TEST_USER_ID), new MarketId(TEST_MARKET_ID), OrderSide.SELL, new Quantity(BigDecimal.valueOf(2L)),
            OrderType.RESERVATION, TriggerCondition.of(TriggerType.ABOVE, new OrderPrice(
                    BigDecimal.valueOf(1_000_000.0))),
            new ScheduledTime(LocalDateTime.now(ZoneOffset.UTC).minusMinutes(1)),
            new ExpireAt(LocalDateTime.now(ZoneOffset.UTC).plusMonths(1)),
            new IsRepeatable(true)
    );

    public static MarketOrder MARKET_ORDER_BUY = MarketOrder.createMarketOrder(
            new UserId(TEST_USER_ID),
            new MarketId(TEST_MARKET_ID),
            OrderSide.BUY,
            new OrderPrice(BigDecimal.valueOf(1_030_000)),
            OrderType.MARKET
    );

    public static MarketOrder MARKET_ORDER_SELL = MarketOrder.createMarketOrder(
            new UserId(TEST_USER_ID),
            new MarketId(TEST_MARKET_ID),
            OrderSide.SELL,
            new OrderPrice(BigDecimal.valueOf(1_020_000)),
            OrderType.MARKET
    );

}
