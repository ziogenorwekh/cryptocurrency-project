package shop.shportfolio.trading.application.test;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.trading.application.command.create.*;
import shop.shportfolio.trading.application.dto.orderbook.OrderBookAsksBithumbDto;
import shop.shportfolio.trading.application.dto.orderbook.OrderBookBidsBithumbDto;
import shop.shportfolio.trading.application.dto.orderbook.OrderBookBithumbDto;
import shop.shportfolio.trading.application.exception.OrderInValidatedException;
import shop.shportfolio.trading.application.ports.input.*;
import shop.shportfolio.trading.application.ports.output.kafka.TradeKafkaPublisher;
import shop.shportfolio.trading.application.ports.output.kafka.UserBalanceKafkaPublisher;
import shop.shportfolio.trading.application.ports.output.marketdata.BithumbApiPort;
import shop.shportfolio.trading.application.ports.output.redis.TradingOrderRedisPort;
import shop.shportfolio.trading.application.ports.output.repository.*;
import shop.shportfolio.trading.application.support.RedisKeyPrefix;
import shop.shportfolio.trading.application.test.helper.OrderBookTestHelper;
import shop.shportfolio.trading.application.test.helper.TestConstants;
import shop.shportfolio.trading.application.test.helper.TradingOrderTestHelper;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.orderbook.MarketItem;
import shop.shportfolio.trading.domain.entity.ReservationOrder;
import shop.shportfolio.trading.domain.entity.userbalance.UserBalance;
import shop.shportfolio.trading.domain.exception.TradingDomainException;
import shop.shportfolio.trading.domain.valueobject.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import shop.shportfolio.common.domain.valueobject.MarketId;
import shop.shportfolio.common.domain.valueobject.OrderPrice;
import shop.shportfolio.common.domain.valueobject.Quantity;
import shop.shportfolio.common.domain.valueobject.UserId;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@ExtendWith(MockitoExtension.class)
public class TradingOrderCreationTest {


    private TradingApplicationService tradingApplicationService;
    @Mock private TradingOrderRepositoryPort tradingOrderRepositoryPort;
    @Mock private TradingTradeRecordRepositoryPort tradingTradeRecordRepositoryPort;
    @Mock private TradingOrderRedisPort tradingOrderRedisPort;
    @Mock private TradeKafkaPublisher tradeKafkaPublisher;
    @Mock private TradingCouponRepositoryPort tradingCouponRepositoryPort;
    @Mock private TradingMarketDataRepositoryPort tradingMarketDataRepositoryPort;
    @Mock private BithumbApiPort bithumbApiPort;
    @Mock private TradingUserBalanceRepositoryPort tradingUserBalanceRepositoryPort;
    @Mock private UserBalanceKafkaPublisher userBalanceKafkaPublisher;
    private final MarketStatus marketStatus = TestConstants.MARKET_STATUS;
    private final UUID userId = TestConstants.TEST_USER_ID;
    private final String marketId = TestConstants.TEST_MARKET_ID;
    private final BigDecimal orderPrice = TestConstants.ORDER_PRICE;
    private final String orderSide = TestConstants.ORDER_SIDE;
    private final BigDecimal quantity = TestConstants.QUANTITY;
    private final OrderType orderTypeLimit = TestConstants.ORDER_TYPE_LIMIT;
    private final OrderType orderTypeMarket = TestConstants.ORDER_TYPE_MARKET;
    TestConstants testConstants;
    private MarketItem marketItem;
    private UserBalance userBalance;
    private TradingOrderTestHelper helper;
    private OrderBookBithumbDto orderBookBithumbDto;

    @BeforeEach
    public void setUp() {
        testConstants = new TestConstants();
        marketItem = testConstants.MARKET_ITEM;
        userBalance = testConstants.createUserBalance(testConstants.USER_BALANCE_1_900_000);
        MockitoAnnotations.openMocks(this);
        helper = new TradingOrderTestHelper();
        tradingApplicationService =
                helper.createTradingApplicationService(
                        tradingOrderRepositoryPort,
                        tradingTradeRecordRepositoryPort,
                        tradingOrderRedisPort,
                        tradingMarketDataRepositoryPort,
                        tradingCouponRepositoryPort,
                        tradeKafkaPublisher,
                        tradingUserBalanceRepositoryPort,
                        userBalanceKafkaPublisher,
                        bithumbApiPort
                );
        orderBookBithumbDto = new OrderBookBithumbDto();
        orderBookBithumbDto.setMarket(marketId);
        orderBookBithumbDto.setTimestamp(System.currentTimeMillis());
        orderBookBithumbDto.setTotalAskSize(5.0);
        orderBookBithumbDto.setTotalBidSize(3.0);
        OrderBookTestHelper.createOrderBook();
    }

    @Test
    @DisplayName("지정가 주문 생성 테스트")
    public void createLimitOrder() {
        // given
        BigDecimal quantity = BigDecimal.valueOf(1);
        CreateLimitOrderCommand createLimitOrderCommand = new CreateLimitOrderCommand(userId, marketId,
                OrderSide.BUY.getValue(), orderPrice, quantity, orderTypeLimit.name());
        Mockito.when(tradingUserBalanceRepositoryPort.findUserBalanceByUserId(userId))
                .thenReturn(Optional.of(userBalance));
        Mockito.when(tradingOrderRepositoryPort.saveLimitOrder(Mockito.any())).thenReturn(
                LimitOrder.createLimitOrder(
                        new UserId(userId),
                        new MarketId(marketId),
                        OrderSide.of(orderSide),
                        new Quantity(quantity),
                        new OrderPrice(orderPrice),
                        OrderType.LIMIT
                ));
        MarketItem marketItem = MarketItem.createMarketItem(marketId, new MarketKoreanName("비트코인"),
                new MarketEnglishName("BTC"), new MarketWarning(""),
                new TickPrice(BigDecimal.valueOf(1000L)),marketStatus);
        Mockito.when(tradingMarketDataRepositoryPort.findMarketItemByMarketId(marketId)).thenReturn(
                Optional.of(marketItem)
        );
        // when
        CreateLimitOrderResponse createLimitOrderResponse = tradingApplicationService.
                createLimitOrder(createLimitOrderCommand);
        // then
        Mockito.verify(tradingOrderRedisPort, Mockito.times(1)).
                saveLimitOrder(Mockito.any(), Mockito.any());
        Mockito.verify(tradingOrderRepositoryPort, Mockito.times(1))
                .saveLimitOrder(Mockito.any());
        Assertions.assertNotNull(createLimitOrderResponse);
        Assertions.assertEquals(userId, createLimitOrderResponse.getUserId());
        Assertions.assertEquals(marketId, createLimitOrderResponse.getMarketId());
        Assertions.assertEquals(quantity, createLimitOrderResponse.getQuantity());
        Assertions.assertEquals(orderPrice, createLimitOrderResponse.getPrice());
        Assertions.assertEquals("BUY", createLimitOrderResponse.getOrderSide());
        Assertions.assertEquals("LIMIT", createLimitOrderResponse.getOrderType().name());
    }

    @Test
    @DisplayName("지정가 주문 생성 테스트하는데, 내가 가진 소지금보다 많게 주문했을 경우에는")
    public void createLimitOrderOverUsersMoneyOrder() {
        // given
        BigDecimal overQuantity = BigDecimal.valueOf(2);
        BigDecimal overPrice = BigDecimal.valueOf(1_050_000.0);
        CreateLimitOrderCommand createLimitOrderCommand = new CreateLimitOrderCommand(userId, marketId,
                OrderSide.BUY.getValue(), overPrice, overQuantity, orderTypeLimit.name());
        Mockito.when(tradingUserBalanceRepositoryPort.findUserBalanceByUserId(userId))
                .thenReturn(Optional.of(userBalance));
        Mockito.when(tradingOrderRepositoryPort.saveLimitOrder(Mockito.any())).thenReturn(
                LimitOrder.createLimitOrder(
                        new UserId(userId),
                        new MarketId(marketId),
                        OrderSide.of(orderSide),
                        new Quantity(overQuantity),
                        new OrderPrice(overPrice),
                        OrderType.LIMIT
                ));
        MarketItem marketItem = MarketItem.createMarketItem(marketId, new MarketKoreanName("비트코인"),
                new MarketEnglishName("BTC"), new MarketWarning(""),
                new TickPrice(BigDecimal.valueOf(1000L)),marketStatus);
        Mockito.when(tradingMarketDataRepositoryPort.findMarketItemByMarketId(marketId)).thenReturn(
                Optional.of(marketItem)
        );
        // when
        TradingDomainException tradingDomainException = Assertions.assertThrows(TradingDomainException.class, () -> {
            tradingApplicationService.createLimitOrder(createLimitOrderCommand);
        });
        // then
        Assertions.assertNotNull(tradingDomainException);
        Assertions.assertEquals(tradingDomainException.getMessage(), "Order amount 2102100.00000000 exceeds available balance " +
                userBalance.getAvailableMoney().getValue());
    }

    @Test
    @DisplayName("지정가 주문 생성 테스트하는데, 내가 가진 소지금과 똑같으면 수수료때문에 에러나는 테스트")
    public void createLimitOrderSameOrderTotalAmountEqualsUserBalance() {
        // given
        BigDecimal overQuantity = BigDecimal.valueOf(1);
        BigDecimal overPrice = BigDecimal.valueOf(1_050_000.0);
        CreateLimitOrderCommand createLimitOrderCommand = new CreateLimitOrderCommand(userId, marketId,
                OrderSide.BUY.getValue(), overPrice, overQuantity, orderTypeLimit.name());
        UserBalance balance = testConstants.
                createUserBalance(testConstants.USER_BALANCE_1_050_000);
        Mockito.when(tradingUserBalanceRepositoryPort.findUserBalanceByUserId(userId))
                .thenReturn(Optional.of(balance));
        Mockito.when(tradingOrderRepositoryPort.saveLimitOrder(Mockito.any())).thenReturn(
                LimitOrder.createLimitOrder(
                        new UserId(userId),
                        new MarketId(marketId),
                        OrderSide.of(orderSide),
                        new Quantity(overQuantity),
                        new OrderPrice(overPrice),
                        OrderType.LIMIT
                ));
        MarketItem marketItem = MarketItem.createMarketItem(marketId, new MarketKoreanName("비트코인"),
                new MarketEnglishName("BTC"), new MarketWarning(""),
                new TickPrice(BigDecimal.valueOf(1000L)),marketStatus);
        Mockito.when(tradingMarketDataRepositoryPort.findMarketItemByMarketId(marketId)).thenReturn(
                Optional.of(marketItem)
        );
        // when
        TradingDomainException tradingDomainException = Assertions.assertThrows(TradingDomainException.class, () -> {
            tradingApplicationService.createLimitOrder(createLimitOrderCommand);
        });
        // then
        Assertions.assertNotNull(tradingDomainException);
        Assertions.assertEquals(tradingDomainException.getMessage(), "Order amount 1051050.00000000 exceeds available balance " +
                balance.getAvailableMoney().getValue());
    }

    @Disabled("다시 테스트 해야 함")
    @Test
    @DisplayName("시장가 주문 생성 테스트")
    public void createMarketOrder() {
        // given
        BigDecimal pirce = BigDecimal.valueOf(1050000);
        MarketItem marketItem = MarketItem.createMarketItem(marketId, new MarketKoreanName("비트코인"),
                new MarketEnglishName("BTC"), new MarketWarning(""),
                new TickPrice(BigDecimal.valueOf(1000L)),marketStatus);
        Mockito.when(tradingUserBalanceRepositoryPort.findUserBalanceByUserId(userId))
                .thenReturn(Optional.of(userBalance));
        Mockito.when(tradingMarketDataRepositoryPort.findMarketItemByMarketId(marketId)).thenReturn(
                Optional.of(marketItem));

        CreateMarketOrderCommand createMarketOrderCommand = new CreateMarketOrderCommand(userId, marketId,
                 orderSide, pirce, orderTypeMarket.name());
        // when
        tradingApplicationService.createMarketOrder(createMarketOrderCommand);
        // then
        Mockito.verify(tradingOrderRepositoryPort, Mockito.times(1))
                .saveMarketOrder(Mockito.any());
    }

    @Test
    @DisplayName("시장가 주문 생성 테스트하는데 가진 금액보다 큰 주문을 할 경우 에러나는 테스트")
    public void createMarketOrderOverOwnMoney() {
        // given
        BigDecimal pirce = BigDecimal.valueOf(2250000);
        MarketItem marketItem = MarketItem.createMarketItem(marketId, new MarketKoreanName("비트코인"),
                new MarketEnglishName("BTC"), new MarketWarning(""),
                new TickPrice(BigDecimal.valueOf(1000L)),marketStatus);
        Mockito.when(tradingUserBalanceRepositoryPort.findUserBalanceByUserId(userId))
                .thenReturn(Optional.of(userBalance));
        Mockito.when(tradingMarketDataRepositoryPort.findMarketItemByMarketId(marketId)).thenReturn(
                Optional.of(marketItem));

        CreateMarketOrderCommand createMarketOrderCommand = new CreateMarketOrderCommand(userId, marketId,
                orderSide, pirce, orderTypeMarket.name());
        // when
        TradingDomainException tradingDomainException = Assertions.assertThrows(TradingDomainException.class, () -> {
            tradingApplicationService.createMarketOrder(createMarketOrderCommand);
        });
        // then
        Assertions.assertNotNull(tradingDomainException);
        Assertions.assertEquals("Order amount 2252250.00000000 exceeds available balance " +
                userBalance.getAvailableMoney().getValue(),tradingDomainException.getMessage());
    }

    @Test
    @DisplayName("음수 수량으로 시장가 주문 생성 시 예외 발생 테스트")
    public void createMarketOrderWithNegativeQuantity() {
        // given
        BigDecimal wrongQuantity = BigDecimal.valueOf(-2L);
        // when
        IllegalArgumentException illegalArgumentException = Assertions.assertThrows(IllegalArgumentException.class, () ->
                new LimitOrder(
                        new OrderId(UUID.randomUUID().toString()),
                        new UserId(userId),
                        new MarketId(marketId),
                        OrderSide.BUY,
                        new Quantity(wrongQuantity),
                        new Quantity(wrongQuantity),
                        new OrderPrice(orderPrice),
                        OrderType.LIMIT,
                        CreatedAt.now(),
                        OrderStatus.OPEN
                ));
        // then
        Assertions.assertTrue(illegalArgumentException.getMessage().contains("Quantity must be positive"));
    }

    @Test
    @DisplayName("잘못된 주문 가격으로 지정가 주문 생성 시 예외 발생 테스트")
    public void createLimitOrderWithInvalidPrice() {
        // given
        BigDecimal wrongQuantity = BigDecimal.valueOf(-2L);
        // when
        IllegalArgumentException illegalArgumentException = Assertions.assertThrows(IllegalArgumentException.class, () ->
                new LimitOrder(
                        new OrderId(UUID.randomUUID().toString()),
                        new UserId(userId),
                        new MarketId(marketId),
                        OrderSide.BUY,
                        new Quantity(wrongQuantity),
                        new Quantity(wrongQuantity),
                        new OrderPrice(orderPrice),
                        OrderType.LIMIT,
                        CreatedAt.now(),
                        OrderStatus.OPEN));
        // then
        Assertions.assertTrue(illegalArgumentException.getMessage().contains("Quantity must be positive"));
    }

    @Test
    @DisplayName("예약 매수 주문 생성 테스트")
    public void createReservationOrderBuyTest() {
        // given
        BigDecimal price = BigDecimal.valueOf(1_010_000.0);
        LocalDateTime scheduledTime = LocalDateTime.now(ZoneOffset.UTC).plusDays(1);
        LocalDateTime expireAt = LocalDateTime.now(ZoneOffset.UTC).plusMonths(1);
        Mockito.when(tradingUserBalanceRepositoryPort.findUserBalanceByUserId(userId))
                .thenReturn(Optional.of(userBalance));
        CreateReservationOrderCommand command = new CreateReservationOrderCommand(
                userId,marketId,"BUY",BigDecimal.valueOf(1L),
                "RESERVATION","ABOVE", price, scheduledTime,
                expireAt
                ,true);
        ReservationOrder reservationOrder = ReservationOrder.
                createReservationOrder(new UserId(userId), new MarketId(marketId), OrderSide.BUY
                        , new Quantity(BigDecimal.valueOf(1)), OrderType.RESERVATION, TriggerCondition.of(TriggerType.ABOVE,
                                new OrderPrice(price)), ScheduledTime.of(scheduledTime), new ExpireAt(expireAt),
                        IsRepeatable.of(true));
        Mockito.when(tradingOrderRepositoryPort.saveReservationOrder(Mockito.any()))
                .thenReturn(reservationOrder);
        MarketItem marketItem = MarketItem.createMarketItem(marketId, new MarketKoreanName("비트코인"),
                new MarketEnglishName("BTC"), new MarketWarning(""),
                new TickPrice(BigDecimal.valueOf(1000L)),marketStatus);
        Mockito.when(tradingMarketDataRepositoryPort.findMarketItemByMarketId(marketId)).thenReturn(
                Optional.of(marketItem));
        // when
        CreateReservationResponse response = tradingApplicationService.createReservationOrder(command);
        // then
        Mockito.verify(tradingOrderRedisPort, Mockito.times(1)).
                saveReservationOrder(Mockito.any(), Mockito.any());
        Mockito.verify(tradingOrderRepositoryPort, Mockito.times(1))
                .saveReservationOrder(Mockito.any());
        Assertions.assertNotNull(response);
        Assertions.assertEquals(scheduledTime, response.getScheduledTime());
        Assertions.assertEquals(expireAt, response.getExpireAt());
        Assertions.assertEquals(reservationOrder.getId().getValue(),response.getOrderId());
    }

    @Test
    @DisplayName("예약 매도 주문 생성 테스트")
    public void createReservationOrderSellTest() {
        // given
        BigDecimal price = BigDecimal.valueOf(910_000.0);
        LocalDateTime scheduledTime = LocalDateTime.now(ZoneOffset.UTC).plusDays(1);
        LocalDateTime expireAt = LocalDateTime.now(ZoneOffset.UTC).plusMonths(1);
        Mockito.when(tradingUserBalanceRepositoryPort.findUserBalanceByUserId(userId))
                .thenReturn(Optional.of(userBalance));
        CreateReservationOrderCommand command = new CreateReservationOrderCommand(
                userId,marketId,"SELL",BigDecimal.valueOf(1L),
                "RESERVATION","ABOVE", price, scheduledTime,
                expireAt
                ,true);
        ReservationOrder reservationOrder = ReservationOrder.
                createReservationOrder(new UserId(userId), new MarketId(marketId), OrderSide.SELL
                        , new Quantity(BigDecimal.valueOf(1)), OrderType.RESERVATION, TriggerCondition.of(TriggerType.ABOVE,
                                new OrderPrice(price)), ScheduledTime.of(scheduledTime), new ExpireAt(expireAt),
                        IsRepeatable.of(true));
        Mockito.when(tradingOrderRepositoryPort.saveReservationOrder(Mockito.any()))
                .thenReturn(reservationOrder);
        MarketItem marketItem = MarketItem.createMarketItem(marketId, new MarketKoreanName("비트코인"),
                new MarketEnglishName("BTC"), new MarketWarning(""),
                new TickPrice(BigDecimal.valueOf(1000L)),marketStatus);
        Mockito.when(tradingMarketDataRepositoryPort.findMarketItemByMarketId(marketId)).thenReturn(
                Optional.of(marketItem));
        // when
        CreateReservationResponse response = tradingApplicationService.createReservationOrder(command);
        // then
        Mockito.verify(tradingOrderRedisPort, Mockito.times(1)).
                saveReservationOrder(Mockito.any(), Mockito.any());
        Mockito.verify(tradingOrderRepositoryPort, Mockito.times(1))
                .saveReservationOrder(Mockito.any());
        Assertions.assertNotNull(response);
        Assertions.assertEquals(scheduledTime, response.getScheduledTime());
        Assertions.assertEquals(expireAt, response.getExpireAt());
        Assertions.assertEquals(reservationOrder.getId().getValue(),response.getOrderId());
    }

    @Test
    @DisplayName("호가창보다 많은 주문이 들어올 경우 테스트")
    public void createMarketOrderExceedPrice() {
        // given
        Mockito.when(tradingUserBalanceRepositoryPort.findUserBalanceByUserId(userId))
                .thenReturn(Optional.of(testConstants.createUserBalance(testConstants.USER_BALANCE_A_LOT_OF_MONEY)));
        CreateMarketOrderCommand createMarketOrderCommand =
                new CreateMarketOrderCommand(userId, marketId, OrderSide.BUY.toString(),
                        BigDecimal.valueOf(10000000.0), OrderType.MARKET.name());
        Mockito.when(tradingMarketDataRepositoryPort.findMarketItemByMarketId(marketId)).thenReturn(
                Optional.of(marketItem));
        // when
        tradingApplicationService.createMarketOrder(createMarketOrderCommand);


    }

    @Test
    @DisplayName("시장가 매수 주문 시 호가 부족으로 요청을 막는 테스트")
    public void createMarketSellOrderWithPartialMatchDueToInsufficientBids() {
        // given
        CreateMarketOrderCommand createMarketOrderCommand = new CreateMarketOrderCommand(userId, marketId
                , OrderSide.BUY.getValue(), BigDecimal.valueOf(1_040_000_000), OrderType.MARKET.name());
        Mockito.when(tradingMarketDataRepositoryPort.findMarketItemByMarketId(marketId))
                .thenReturn(Optional.of(marketItem));
        // when
        OrderInValidatedException orderInValidatedException = Assertions.assertThrows(OrderInValidatedException.class, () -> {
            tradingApplicationService.createMarketOrder(createMarketOrderCommand);
        });
        // then
        Assertions.assertNotNull(orderInValidatedException);
        Assertions.assertEquals("Requested buy amount exceeds available sell liquidity.",
                orderInValidatedException.getMessage());
    }

    @Test
    @DisplayName("지정가 매수 주문인데 너무 높은 가격으로 사려고 하면 에러가 나야하는 테스트(상한 10%까지)")
    public void whenLimitBuyOrderPriceExceedsUpperLimit_thenThrowsOrderInValidatedException() {
        // given
        BigDecimal price = BigDecimal.valueOf(1_550_000.0);
        BigDecimal quantity1 = BigDecimal.valueOf(1L);
        CreateLimitOrderCommand createLimitOrderCommand = new CreateLimitOrderCommand(userId, marketId
                , OrderSide.BUY.getValue(), price,
                quantity1, OrderType.LIMIT.name());
        LimitOrder limitOrder = LimitOrder.createLimitOrder(new UserId(userId), new MarketId(marketId), OrderSide.BUY, new Quantity(quantity1)
                , new OrderPrice(price), OrderType.LIMIT);
        Mockito.when(tradingMarketDataRepositoryPort.findMarketItemByMarketId(marketId))
                .thenReturn(Optional.of(marketItem));
        Mockito.when(tradingOrderRepositoryPort.saveLimitOrder(Mockito.any(LimitOrder.class)))
                .thenReturn(limitOrder);
        // when
        OrderInValidatedException orderInValidatedException = Assertions.assertThrows(OrderInValidatedException.class, () -> {
            tradingApplicationService.createLimitOrder(createLimitOrderCommand);
        });
        // then
        Assertions.assertNotNull(orderInValidatedException);
        Assertions.assertEquals("Limit buy order price is more than 10% above best ask.",
                orderInValidatedException.getMessage());
    }

    @Test
    @DisplayName("지정가 매수 주문인데 너무 높은 가격으로 사려고 하면 에러가 나야하는 테스트(상한 10% 경계선) 경계선까지는 포함 됌")
    public void whenLimitBuyOrderPriceExceedsUpperExactlyTenPercentLimit_thenThrowsOrderInValidatedException() {
        // given
        BigDecimal price = BigDecimal.valueOf(1_155_001.0);
        BigDecimal quantity1 = BigDecimal.valueOf(1L);
        CreateLimitOrderCommand createLimitOrderCommand = new CreateLimitOrderCommand(userId, marketId
                , OrderSide.BUY.getValue(), price,
                quantity1, OrderType.LIMIT.name());
        LimitOrder limitOrder = LimitOrder.createLimitOrder(new UserId(userId), new MarketId(marketId), OrderSide.BUY, new Quantity(quantity1)
                , new OrderPrice(price), OrderType.LIMIT);
        Mockito.when(tradingMarketDataRepositoryPort.findMarketItemByMarketId(marketId))
                .thenReturn(Optional.of(marketItem));
        Mockito.when(tradingOrderRepositoryPort.saveLimitOrder(Mockito.any(LimitOrder.class)))
                .thenReturn(limitOrder);
        // when
        OrderInValidatedException orderInValidatedException = Assertions.assertThrows(OrderInValidatedException.class, () -> {
            tradingApplicationService.createLimitOrder(createLimitOrderCommand);
        });
        // then
        Assertions.assertNotNull(orderInValidatedException);
        Assertions.assertEquals("Limit buy order price is more than 10% above best ask.",
                orderInValidatedException.getMessage());
    }

    @Test
    @DisplayName("지정가 매도 주문인데 너무 낮은 가격으로 팔려고 하면 에러가 나야하는 테스트(하한 10% 경계선)")
    public void whenLimitSellOrderPriceExceedsUpperExactlyTenPercentLimit_thenThrowsOrderInValidatedException() {
        // given
        BigDecimal price = BigDecimal.valueOf(800_000.0);
        BigDecimal quantity1 = BigDecimal.valueOf(1L);
        CreateLimitOrderCommand createLimitOrderCommand = new CreateLimitOrderCommand(userId, marketId
                , OrderSide.SELL.getValue(), price,
                quantity1, OrderType.LIMIT.name());
        LimitOrder limitOrder = LimitOrder.createLimitOrder(new UserId(userId), new MarketId(marketId), OrderSide.SELL,
                new Quantity(quantity1), new OrderPrice(price), OrderType.LIMIT);
        Mockito.when(tradingMarketDataRepositoryPort.findMarketItemByMarketId(marketId))
                .thenReturn(Optional.of(marketItem));
        Mockito.when(tradingOrderRepositoryPort.saveLimitOrder(Mockito.any(LimitOrder.class)))
                .thenReturn(limitOrder);
        // when
        OrderInValidatedException orderInValidatedException = Assertions.assertThrows(OrderInValidatedException.class, () -> {
            tradingApplicationService.createLimitOrder(createLimitOrderCommand);
        });
        // then
        Assertions.assertNotNull(orderInValidatedException);
        Assertions.assertEquals("Limit sell order price is more than 10% below best bid.",
                orderInValidatedException.getMessage());
    }


    @Test
    @DisplayName("예약 매수 주문 생성 테스트인데, 구매가가 높은 경우 에러나는 테스트(가격과는 상관없음)")
    public void whenReservationBuyOrderPriceExceedsUpperExactlyTenPercentLimit_thenThrowsOrderInValidatedException() {
        // given
        Mockito.when(tradingUserBalanceRepositoryPort.findUserBalanceByUserId(userId))
                .thenReturn(Optional.of(testConstants.createUserBalance(testConstants.USER_BALANCE_1_900_000)));
        BigDecimal price = BigDecimal.valueOf(1_510_000.0);
        LocalDateTime scheduledTime = LocalDateTime.now(ZoneOffset.UTC).plusDays(1);
        LocalDateTime expireAt = LocalDateTime.now(ZoneOffset.UTC).plusMonths(1);
        CreateReservationOrderCommand command = new CreateReservationOrderCommand(
                userId,marketId,"BUY",BigDecimal.valueOf(1L),
                "RESERVATION","ABOVE", price, scheduledTime,
                expireAt
                ,true);
        ReservationOrder reservationOrder = ReservationOrder.
                createReservationOrder(new UserId(userId), new MarketId(marketId), OrderSide.BUY
                        , new Quantity(BigDecimal.valueOf(1)), OrderType.RESERVATION, TriggerCondition.of(TriggerType.ABOVE,
                                new OrderPrice(price)), ScheduledTime.of(scheduledTime), new ExpireAt(expireAt),
                        IsRepeatable.of(true));
        Mockito.when(tradingOrderRepositoryPort.saveReservationOrder(Mockito.any()))
                .thenReturn(reservationOrder);
        MarketItem marketItem = MarketItem.createMarketItem(marketId, new MarketKoreanName("비트코인"),
                new MarketEnglishName("BTC"), new MarketWarning(""),
                new TickPrice(BigDecimal.valueOf(1000L)),marketStatus);
        Mockito.when(tradingMarketDataRepositoryPort.findMarketItemByMarketId(marketId)).thenReturn(
                Optional.of(marketItem));
        // when
        CreateReservationResponse response = tradingApplicationService.createReservationOrder(command);
        // then
        Assertions.assertNotNull(response);
        Assertions.assertEquals(reservationOrder.getId().getValue(),response.getOrderId());
    }

    @Test
    @DisplayName("예약 매수 주문 생성 테스트인데, 호가 수량을 넘어서면 에러나는 테스트")
    public void whenReservationBuyOrderPriceExceedsOrderBookTotalSize_thenThrowsOrderInValidatedException() {
        // given
        BigDecimal price = BigDecimal.valueOf(1_010_000.0);
        LocalDateTime scheduledTime = LocalDateTime.now(ZoneOffset.UTC).plusDays(1);
        LocalDateTime expireAt = LocalDateTime.now(ZoneOffset.UTC).plusMonths(1);
        CreateReservationOrderCommand command = new CreateReservationOrderCommand(
                userId,marketId,"BUY",BigDecimal.valueOf(2000L),
                "RESERVATION","ABOVE", price, scheduledTime,
                expireAt
                ,true);
        ReservationOrder reservationOrder = ReservationOrder.
                createReservationOrder(new UserId(userId), new MarketId(marketId), OrderSide.BUY
                        , new Quantity(BigDecimal.valueOf(2000L)), OrderType.RESERVATION, TriggerCondition.of(TriggerType.ABOVE,
                                new OrderPrice(price)), ScheduledTime.of(scheduledTime), new ExpireAt(expireAt),
                        IsRepeatable.of(true));
        Mockito.when(tradingOrderRepositoryPort.saveReservationOrder(Mockito.any()))
                .thenReturn(reservationOrder);
        MarketItem marketItem = MarketItem.createMarketItem(marketId, new MarketKoreanName("비트코인"),
                new MarketEnglishName("BTC"), new MarketWarning(""),
                new TickPrice(BigDecimal.valueOf(1000L)),marketStatus);
        Mockito.when(tradingMarketDataRepositoryPort.findMarketItemByMarketId(marketId)).thenReturn(
                Optional.of(marketItem));
        // when
        OrderInValidatedException orderInValidatedException = Assertions.assertThrows(OrderInValidatedException.class, () -> {
            tradingApplicationService.createReservationOrder(command);

        });
        // then
        Assertions.assertNotNull(orderInValidatedException);
        Assertions.assertEquals("Buy order quantity exceeds available sell liquidity.",
                orderInValidatedException.getMessage());
    }

    @Test
    @DisplayName("예약 매도 주문 생성 테스트인데, 호가 수량을 넘어서면 에러나는 테스트")
    public void whenReservationSellOrderPriceExceedsOrderBookTotalSize_thenThrowsOrderInValidatedException() {
        // given
        BigDecimal price = BigDecimal.valueOf(1_010_000.0);
        LocalDateTime scheduledTime = LocalDateTime.now(ZoneOffset.UTC).plusDays(1);
        LocalDateTime expireAt = LocalDateTime.now(ZoneOffset.UTC).plusMonths(1);
        CreateReservationOrderCommand command = new CreateReservationOrderCommand(
                userId,marketId,"SELL",BigDecimal.valueOf(2000L),
                "RESERVATION","ABOVE", price, scheduledTime,
                expireAt
                ,true);
        ReservationOrder reservationOrder = ReservationOrder.
                createReservationOrder(new UserId(userId), new MarketId(marketId), OrderSide.SELL
                        , new Quantity(BigDecimal.valueOf(2000L)), OrderType.RESERVATION, TriggerCondition.of(TriggerType.ABOVE,
                                new OrderPrice(price)), ScheduledTime.of(scheduledTime), new ExpireAt(expireAt),
                        IsRepeatable.of(true));
        Mockito.when(tradingOrderRepositoryPort.saveReservationOrder(Mockito.any()))
                .thenReturn(reservationOrder);
        MarketItem marketItem = MarketItem.createMarketItem(marketId, new MarketKoreanName("비트코인"),
                new MarketEnglishName("BTC"), new MarketWarning(""),
                new TickPrice(BigDecimal.valueOf(1000L)),marketStatus);
        Mockito.when(tradingMarketDataRepositoryPort.findMarketItemByMarketId(marketId)).thenReturn(
                Optional.of(marketItem));
        // when
        OrderInValidatedException orderInValidatedException2 = Assertions.assertThrows(OrderInValidatedException.class, () -> {
            tradingApplicationService.createReservationOrder(command);

        });
        // then
        Assertions.assertNotNull(orderInValidatedException2);
        Assertions.assertEquals("Sell order quantity exceeds available buy liquidity.",
                orderInValidatedException2.getMessage());
    }

}
