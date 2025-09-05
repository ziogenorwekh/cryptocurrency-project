package shop.shportfolio.trading.application.test.helper;

import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.MarketOrder;
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
    public static final String TEST_MARKET_ID = "KRW-BTC";
    public static final MarketStatus MARKET_STATUS = MarketStatus.ACTIVE;


    public static final BigDecimal ORDER_PRICE = BigDecimal.valueOf(1_050_000);
    public static final String ORDER_SIDE = "BUY";
    public static final BigDecimal QUANTITY = BigDecimal.valueOf(5L);
    public static final OrderType ORDER_TYPE_LIMIT = OrderType.LIMIT;
    public static final OrderType ORDER_TYPE_MARKET = OrderType.MARKET;

    public final MarketItem MARKET_ITEM = MarketItem.createMarketItem(
            TEST_MARKET_ID,
            new MarketKoreanName("비트코인"),
            new MarketEnglishName("BTC"),
            new MarketWarning(""),
            new TickPrice(BigDecimal.valueOf(1000L)),
            MARKET_STATUS
    );

    public final LimitOrder LIMIT_ORDER_BUY = LimitOrder.createLimitOrder(
            new UserId(TEST_USER_ID),
            new MarketId(TEST_MARKET_ID),
            OrderSide.BUY,
            new Quantity(BigDecimal.valueOf(1.0)),
            new OrderPrice(BigDecimal.valueOf(1_050_000.0)),
            OrderType.LIMIT
    );

    public final LimitOrder LIMIT_ORDER_SELL = LimitOrder.createLimitOrder(
            new UserId(TEST_USER_ID),
            new MarketId(TEST_MARKET_ID),
            OrderSide.SELL,
            new Quantity(BigDecimal.valueOf(1.0)),
            new OrderPrice(BigDecimal.valueOf(1_000_000.0)),
            OrderType.LIMIT
    );
    public final LimitOrder LIMIT_ORDER2_BUY = new LimitOrder(
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

    public final LimitOrder LIMIT_ORDER3_BUY = new LimitOrder(
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
    public final LimitOrder LIMIT_ORDER4_BUY = new LimitOrder(
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
    public final LimitOrder LIMIT_ORDER2_SELL = new LimitOrder(
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
    public final LimitOrder LIMIT_ORDER3_SELL = new LimitOrder(
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
    public final LimitOrder LIMIT_ORDER4_SELL = new LimitOrder(
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


    public ReservationOrder RESERVATION_ORDER_BUY = ReservationOrder.createReservationOrder(
            new UserId(TEST_USER_ID), new MarketId(TEST_MARKET_ID), OrderSide.BUY, new Quantity(QUANTITY),
            OrderType.RESERVATION, TriggerCondition.of(TriggerType.BELOW, new OrderPrice(
                    BigDecimal.valueOf(1_030_000.0))),
            new ScheduledTime(LocalDateTime.now(ZoneOffset.UTC).minusMinutes(1)),
            new ExpireAt(LocalDateTime.now(ZoneOffset.UTC).plusMonths(1)),
            new IsRepeatable(true)
    );

    public ReservationOrder RESERVATION_ORDER_SELL = ReservationOrder.createReservationOrder(
            new UserId(TEST_USER_ID), new MarketId(TEST_MARKET_ID), OrderSide.SELL, new Quantity(BigDecimal.valueOf(2L)),
            OrderType.RESERVATION, TriggerCondition.of(TriggerType.ABOVE, new OrderPrice(
                    BigDecimal.valueOf(1_000_000.0))),
            new ScheduledTime(LocalDateTime.now(ZoneOffset.UTC).minusMinutes(1)),
            new ExpireAt(LocalDateTime.now(ZoneOffset.UTC).plusMonths(1)),
            new IsRepeatable(true)
    );

    public MarketOrder MARKET_ORDER_BUY = MarketOrder.createMarketOrder(
            new UserId(TEST_USER_ID),
            new MarketId(TEST_MARKET_ID),
            OrderSide.BUY,
            null,
            new OrderPrice(BigDecimal.valueOf(1_030_000)),
            OrderType.MARKET
    );

    public MarketOrder MARKET_ORDER_SELL = MarketOrder.createMarketOrder(
            new UserId(TEST_USER_ID),
            new MarketId(TEST_MARKET_ID),
            OrderSide.SELL,
            new Quantity(BigDecimal.ONE),
            null,
            OrderType.MARKET
    );


    public BigDecimal USER_BALANCE_1_900_000 = BigDecimal.valueOf(1_900_000);

    public BigDecimal USER_BALANCE_1_050_000 = BigDecimal.valueOf(1_050_000);

    public BigDecimal USER_BALANCE_A_LOT_OF_MONEY = BigDecimal.valueOf(1_050_000_000);

    public LimitOrder limitOrderBuy() {
        return LimitOrder.createLimitOrder(
                new UserId(TEST_USER_ID),
                new MarketId(TEST_MARKET_ID),
                OrderSide.BUY,
                new Quantity(BigDecimal.valueOf(1.0)),
                new OrderPrice(BigDecimal.valueOf(1_050_000.0)),
                OrderType.LIMIT
        );
    }

    public LimitOrder limitOrderSell() {
        return LimitOrder.createLimitOrder(
                new UserId(TEST_USER_ID),
                new MarketId(TEST_MARKET_ID),
                OrderSide.SELL,
                new Quantity(BigDecimal.valueOf(1.0)),
                new OrderPrice(BigDecimal.valueOf(1_000_000.0)),
                OrderType.LIMIT
        );
    }

    public LimitOrder limitOrder2Buy() {
        return new LimitOrder(
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
    }

    public LimitOrder limitOrder3Buy() {
        return new LimitOrder(
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
    }

    public LimitOrder limitOrder4Buy() {
        return new LimitOrder(
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
    }

    public LimitOrder limitOrder2Sell() {
        return new LimitOrder(
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
    }

    public LimitOrder limitOrder3Sell() {
        return new LimitOrder(
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
    }

    public LimitOrder limitOrder4Sell() {
        return new LimitOrder(
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
    }

    // ====== ReservationOrder ======
    public ReservationOrder reservationOrderBuy() {
        return ReservationOrder.createReservationOrder(
                new UserId(TEST_USER_ID),
                new MarketId(TEST_MARKET_ID),
                OrderSide.BUY,
                new Quantity(BigDecimal.valueOf(5L)),
                OrderType.RESERVATION,
                TriggerCondition.of(TriggerType.BELOW, new OrderPrice(BigDecimal.valueOf(1_030_000.0))),
                new ScheduledTime(LocalDateTime.now(ZoneOffset.UTC).minusMinutes(1)),
                new ExpireAt(LocalDateTime.now(ZoneOffset.UTC).plusMonths(1)),
                new IsRepeatable(true)
        );
    }

    public ReservationOrder reservationOrderSell() {
        return ReservationOrder.createReservationOrder(
                new UserId(TEST_USER_ID),
                new MarketId(TEST_MARKET_ID),
                OrderSide.SELL,
                new Quantity(BigDecimal.valueOf(2L)),
                OrderType.RESERVATION,
                TriggerCondition.of(TriggerType.ABOVE, new OrderPrice(BigDecimal.valueOf(1_000_000.0))),
                new ScheduledTime(LocalDateTime.now(ZoneOffset.UTC).minusMinutes(1)),
                new ExpireAt(LocalDateTime.now(ZoneOffset.UTC).plusMonths(1)),
                new IsRepeatable(true)
        );
    }

    // ====== MarketOrder ======
    public MarketOrder marketOrderBuy() {
        return MarketOrder.createMarketOrder(
                new UserId(TEST_USER_ID),
                new MarketId(TEST_MARKET_ID),
                OrderSide.BUY,
                null,
                new OrderPrice(BigDecimal.valueOf(1_030_000)),
                OrderType.MARKET
        );
    }

    public MarketOrder marketOrderSell() {
        return MarketOrder.createMarketOrder(
                new UserId(TEST_USER_ID),
                new MarketId(TEST_MARKET_ID),
                OrderSide.SELL,
//                new OrderPrice(BigDecimal.valueOf(1_020_000)),
                new Quantity(BigDecimal.ONE),
                null,
                OrderType.MARKET
        );
    }

    // ====== UserBalance ======
    public UserBalance createUserBalance(BigDecimal amount) {
        return UserBalance.createUserBalance(
                new UserBalanceId(UUID.randomUUID()),
                new UserId(UUID.randomUUID()),
                AssetCode.KRW,
                Money.of(amount),
                null
        );
    }

    public UserBalance createUserBalance(UserId userId, BigDecimal amount) {
        return UserBalance.createUserBalance(
                new UserBalanceId(UUID.randomUUID()),
                userId,
                AssetCode.KRW,
                Money.of(amount),
                null
        );
    }

}