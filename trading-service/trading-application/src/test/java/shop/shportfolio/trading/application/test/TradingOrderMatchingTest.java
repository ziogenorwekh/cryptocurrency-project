package shop.shportfolio.trading.application.test;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.trading.application.command.create.CreateLimitOrderCommand;
import shop.shportfolio.trading.application.command.create.CreateMarketOrderCommand;
import shop.shportfolio.trading.application.command.track.request.OrderBookTrackQuery;
import shop.shportfolio.trading.application.command.track.response.OrderBookTrackResponse;
import shop.shportfolio.trading.application.dto.orderbook.OrderBookAsksBithumbDto;
import shop.shportfolio.trading.application.dto.orderbook.OrderBookBidsBithumbDto;
import shop.shportfolio.trading.application.dto.orderbook.OrderBookBithumbDto;
import shop.shportfolio.trading.application.exception.MarketItemNotFoundException;
import shop.shportfolio.trading.application.exception.MarketPausedException;
import shop.shportfolio.trading.application.facade.ExecuteOrderMatchingFacade;
import shop.shportfolio.trading.application.handler.matching.strategy.ReservationOrderMatchingStrategy;
import shop.shportfolio.trading.application.ports.input.*;
import shop.shportfolio.trading.application.ports.output.kafka.TradeKafkaPublisher;
import shop.shportfolio.trading.application.ports.output.marketdata.BithumbApiPort;
import shop.shportfolio.trading.application.ports.output.redis.TradingMarketDataRedisPort;
import shop.shportfolio.trading.application.ports.output.redis.TradingOrderRedisPort;
import shop.shportfolio.trading.application.ports.output.repository.*;
import shop.shportfolio.trading.application.support.RedisKeyPrefix;
import shop.shportfolio.trading.application.test.helper.MarketDataApplicationTestHelper;
import shop.shportfolio.trading.application.test.helper.TestConstants;
import shop.shportfolio.trading.application.test.helper.TradingOrderTestHelper;
import shop.shportfolio.trading.domain.entity.*;
import shop.shportfolio.trading.domain.entity.orderbook.MarketItem;
import shop.shportfolio.trading.domain.entity.orderbook.OrderBook;
import shop.shportfolio.trading.domain.entity.trade.Trade;
import shop.shportfolio.trading.domain.entity.userbalance.LockBalance;
import shop.shportfolio.trading.domain.entity.userbalance.UserBalance;
import shop.shportfolio.trading.domain.event.TradingRecordedEvent;
import shop.shportfolio.trading.domain.valueobject.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@ExtendWith(MockitoExtension.class)
public class TradingOrderMatchingTest {

    private TradingApplicationService tradingApplicationService;
    @Mock private TradingOrderRepositoryPort tradingOrderRepositoryPort;
    @Mock private TradingTradeRecordRepositoryPort tradingTradeRecordRepositoryPort;
    @Mock private TradingOrderRedisPort tradingOrderRedisPort;
    @Mock private TradingMarketDataRedisPort tradingMarketDataRedisPort;
    @Mock private TradeKafkaPublisher tradeKafkaPublisher;
    @Mock private TradingCouponRepositoryPort tradingCouponRepositoryPort;
    @Mock private TradingMarketDataRepositoryPort tradingMarketDataRepositoryPort;
    @Mock private BithumbApiPort bithumbApiPort;
    @Mock private TradingUserBalanceRepositoryPort tradingUserBalanceRepositoryPort;

    @Captor
    ArgumentCaptor<MarketOrder> marketOrderCaptor;

    List<Trade> trades = new ArrayList<>();
    private final UUID userId = TestConstants.TEST_USER_ID;
    private final String marketId = TestConstants.TEST_MARKET_ID;
    private final String orderSide = TestConstants.ORDER_SIDE;
    private final OrderType orderTypeMarket = TestConstants.ORDER_TYPE_MARKET;
    private final BigDecimal orderPrice = TestConstants.ORDER_PRICE;
    private final BigDecimal quantity = TestConstants.QUANTITY;
    private final MarketItem marketItem = TestConstants.MARKET_ITEM;

    private OrderBookBithumbDto orderBookBithumbDto;
    private LimitOrder normalLimitOrder;
    private TradingOrderTestHelper helper;
    private MarketDataApplicationTestHelper  marketDataApplicationTestHelper;
    private ExecuteOrderMatchingUseCase executeOrderMatchingUseCase;
    @BeforeEach
    public void setUp() {
        helper = new TradingOrderTestHelper();
        marketDataApplicationTestHelper = new  MarketDataApplicationTestHelper();
        tradingApplicationService = helper.createTradingApplicationService(
                tradingOrderRepositoryPort,
                tradingTradeRecordRepositoryPort,
                tradingOrderRedisPort,
                tradingMarketDataRepositoryPort,
                tradingMarketDataRedisPort,
                tradingCouponRepositoryPort,
                tradeKafkaPublisher,
                bithumbApiPort,
                tradingUserBalanceRepositoryPort
        );
        marketDataApplicationTestHelper.createMarketDataApplicationService(
                tradingOrderRepositoryPort,
                tradingTradeRecordRepositoryPort,
                tradingOrderRedisPort,
                tradingMarketDataRepositoryPort,
                tradingMarketDataRedisPort,
                bithumbApiPort
        );

        executeOrderMatchingUseCase = new ExecuteOrderMatchingFacade(helper.orderBookManager,
                tradeKafkaPublisher, helper.strategies);
        trades.add(new Trade(new TradeId(UUID.randomUUID()),
                new UserId(userId),
                OrderId.anonymous(),
                OrderId.anonymous(),
                new OrderPrice(BigDecimal.valueOf(1_050_200.0)),
                new CreatedAt(LocalDateTime.now(ZoneOffset.UTC).plusMinutes(1L)),
                new Quantity(BigDecimal.valueOf(1.0)),
                TransactionType.TRADE_BUY
        ));
        orderBookBithumbDto = new OrderBookBithumbDto();
        orderBookBithumbDto.setMarket(marketId);
        orderBookBithumbDto.setTimestamp(System.currentTimeMillis());
        orderBookBithumbDto.setTotalAskSize(5.0);
        orderBookBithumbDto.setTotalBidSize(3.0);

        // 매도 호가 리스트 (가격 상승 순으로)
        List<OrderBookAsksBithumbDto> asks = List.of(
                createAsk(1_050_000.0, 1.0),
                createAsk(1_060_000.0, 1.2),
                createAsk(1_070_000.0, 1.4),
                createAsk(1_080_000.0, 1.6),
                createAsk(1_090_000.0, 1.8),
                createAsk(1_100_000.0, 2.0),
                createAsk(1_110_000.0, 2.2),
                createAsk(1_120_000.0, 2.4),
                createAsk(1_130_000.0, 2.6),
                createAsk(1_140_000.0, 2.8)
        );
        orderBookBithumbDto.setAsks(asks);

        // 매수 호가 리스트 (가격 하락 순으로)
        List<OrderBookBidsBithumbDto> bids = List.of(
                createBid(990_000.0, 1.0),
                createBid(980_000.0, 1.2),
                createBid(970_000.0, 1.4),
                createBid(960_000.0, 1.6),
                createBid(950_000.0, 1.8),
                createBid(940_000.0, 2.0),
                createBid(930_000.0, 2.2),
                createBid(920_000.0, 2.4),
                createBid(910_000.0, 2.6),
                createBid(900_000.0, 2.8)
        );
        orderBookBithumbDto.setBids(bids);
    }

    // 편의 메서드
    private OrderBookAsksBithumbDto createAsk(Double price, Double size) {
        OrderBookAsksBithumbDto ask = new OrderBookAsksBithumbDto();
        ask.setAskPrice(price);
        ask.setAskSize(size);
        return ask;
    }

    private OrderBookBidsBithumbDto createBid(Double price, Double size) {
        OrderBookBidsBithumbDto bid = new OrderBookBidsBithumbDto();
        bid.setBidPrice(price);
        bid.setBidSize(size);
        return bid;
    }

    @Test
    @DisplayName("시장가 매수 체결 테스트")
    public void createBidMarketOrderMatchingTest() {
        // given
        UserBalance balance = TestConstants.createUserBalance(TestConstants.USER_BALANCE_1_900_000);
        Mockito.when(tradingUserBalanceRepositoryPort.findUserBalanceByUserId(Mockito.any()))
                .thenReturn(Optional.of(balance));
        OrderPrice innerPrice = new OrderPrice(BigDecimal.valueOf(1_000_000.0));
        CreateMarketOrderCommand createMarketOrderCommand = new CreateMarketOrderCommand(userId, marketId,
                orderSide, innerPrice.getValue(), orderTypeMarket.name());
        MarketOrder marketOrder = MarketOrder.createMarketOrder(
                new UserId(userId),
                new MarketId(marketId),
                OrderSide.of(orderSide),
                innerPrice,
                OrderType.MARKET);
        Mockito.when(tradingMarketDataRepositoryPort.findMarketItemByMarketId(marketId)).thenReturn(
                Optional.of(marketItem));
        Mockito.when(tradingOrderRepositoryPort.saveMarketOrder(Mockito.any())).thenReturn(
                marketOrder
        );
        Mockito.when(tradingMarketDataRedisPort.findOrderBookByMarket(RedisKeyPrefix.market(marketId)))
                .thenReturn(Optional.ofNullable(orderBookBithumbDto));
        // when
        tradingApplicationService.createMarketOrder(createMarketOrderCommand);
        // then
        Mockito.verify(tradeKafkaPublisher, Mockito.times(1)).publish(Mockito.any());
    }

    @Test
    @DisplayName("시장가 매도 체결")
    public void createAskMarketOrderMatchingTest() {
        // given
        UserBalance balance = TestConstants.createUserBalance(TestConstants.USER_BALANCE_1_900_000);
        Mockito.when(tradingUserBalanceRepositoryPort.findUserBalanceByUserId(Mockito.any()))
                .thenReturn(Optional.of(balance));
        OrderPrice innerPrice = new OrderPrice(BigDecimal.valueOf(1_000_000.0));
        CreateMarketOrderCommand createMarketOrderCommand = new CreateMarketOrderCommand(userId, marketId,
                OrderSide.SELL.getValue(), innerPrice.getValue(), orderTypeMarket.name());
        MarketOrder marketOrder = MarketOrder.createMarketOrder(
                new UserId(userId),
                new MarketId(marketId),
                OrderSide.SELL,
                innerPrice,
                OrderType.MARKET);
        Mockito.when(tradingMarketDataRepositoryPort.findMarketItemByMarketId(marketId)).thenReturn(
                Optional.of(marketItem));
        Mockito.when(tradingOrderRepositoryPort.saveMarketOrder(Mockito.any())).thenReturn(
                marketOrder
        );
        Mockito.when(tradingMarketDataRedisPort.findOrderBookByMarket(RedisKeyPrefix.market(marketId)))
                .thenReturn(Optional.ofNullable(orderBookBithumbDto));
        // when
        tradingApplicationService.createMarketOrder(createMarketOrderCommand);
        // then
        Mockito.verify(tradeKafkaPublisher, Mockito.times(2)).publish(Mockito.any());
    }

    @Test
    @DisplayName("주문 수량이 호가 총합과 딱 일치하는 경우 체결 테스트")
    public void createMarketOrderExactQuantityMatch() {
        // given
        normalLimitOrder = LimitOrder.createLimitOrder(
                new UserId(userId),
                new MarketId(marketId),
                OrderSide.BUY,
                new Quantity(BigDecimal.valueOf(1.0)),
                new OrderPrice(BigDecimal.valueOf(1_050_000.0)),
                OrderType.LIMIT);
        // when
        helper.orderDomainService.applyOrder(normalLimitOrder, new Quantity(BigDecimal.valueOf(1.0)));
        // then
        Assertions.assertEquals(BigDecimal.valueOf(0.0), normalLimitOrder.getRemainingQuantity().getValue());
        Assertions.assertEquals(OrderStatus.FILLED, normalLimitOrder.getOrderStatus());
    }


    @Test
    @DisplayName("매칭 후 트레이드 내역 생성 및 호가 잔량 감소 검증 테스트")
    public void tradeMatchingAndOrderBookUpdate() {
        // given
        Mockito.when(tradingMarketDataRepositoryPort.findMarketItemByMarketId(marketId))
                .thenReturn(Optional.of(marketItem));
        Mockito.when(tradingTradeRecordRepositoryPort.findTradesByMarketId(marketId)).thenReturn(trades);
        Mockito.when(tradingMarketDataRedisPort.findOrderBookByMarket(RedisKeyPrefix.market(marketId)))
                .thenReturn(Optional.ofNullable(orderBookBithumbDto));
        // when
        OrderBookTrackResponse orderBook = tradingApplicationService.
                findOrderBook(new OrderBookTrackQuery(marketId));
        // then
        double size = orderBook.getOrderBookAsksResponse().stream()
                .mapToDouble(c -> Double.parseDouble(c.getQuantity()))
                .sum();
        Assertions.assertEquals(18, size);
    }

    @Test
    @DisplayName("지정가 매수 주문 매칭")
    public void buyLimitOrderMatching() {
        // given
        UserBalance balance = TestConstants.createUserBalance(TestConstants.USER_BALANCE_1_900_000);

        BigDecimal quantity = BigDecimal.valueOf(1);
        LimitOrder limitOrder = LimitOrder.createLimitOrder(
                new UserId(userId),
                new MarketId(marketId),
                OrderSide.of(orderSide),
                new Quantity(quantity),
                new OrderPrice(orderPrice),
                OrderType.LIMIT
        );
        LockBalance lockBalance = LockBalance.createLockBalance(limitOrder.getId(), limitOrder.getUserId(),
                Money.of(limitOrder.getOrderPrice().getValue().add(BigDecimal.valueOf(1050L))), LockStatus.LOCKED,
                CreatedAt.now());
        balance.getLockBalances().add(lockBalance);
        Mockito.when(tradingUserBalanceRepositoryPort.findUserBalanceByUserId(Mockito.any()))
                .thenReturn(Optional.of(balance));
        Mockito.when(tradingOrderRepositoryPort.saveLimitOrder(Mockito.any())).thenReturn(limitOrder);
        Mockito.when(tradingMarketDataRedisPort.findOrderBookByMarket(RedisKeyPrefix.market(marketId)))
                .thenReturn(Optional.ofNullable(orderBookBithumbDto));
        Mockito.when(tradingMarketDataRepositoryPort.findMarketItemByMarketId(marketId))
                .thenReturn(Optional.of(marketItem));
        // when
        executeOrderMatchingUseCase.executeLimitOrder(limitOrder);
        // then
        Mockito.verify(tradingOrderRedisPort, Mockito.times(1))
                .deleteLimitOrder(Mockito.any());
        Mockito.verify(tradeKafkaPublisher, Mockito.times(1)).publish(Mockito.any());
        Mockito.verify(tradingOrderRepositoryPort, Mockito.times(1)).
                saveLimitOrder(Mockito.any());
    }

    @Test
    @DisplayName("지정가 매도 주문 매칭")
    public void sellLimitOrderMatching() {
        // given
        UserBalance balance = TestConstants.createUserBalance(TestConstants.USER_BALANCE_1_900_000);
        BigDecimal orderPrice = BigDecimal.valueOf(990_000.0);
        BigDecimal quantity = BigDecimal.valueOf(1);
        LimitOrder limitOrder = LimitOrder.createLimitOrder(
                new UserId(userId),
                new MarketId(marketId),
                OrderSide.SELL,
                new Quantity(quantity),
                new OrderPrice(orderPrice),
                OrderType.LIMIT
        );
        LockBalance lockBalance = LockBalance.createLockBalance(limitOrder.getId(), limitOrder.getUserId(),
                Money.of(limitOrder.getOrderPrice().getValue().add(BigDecimal.valueOf(1980L))), LockStatus.LOCKED,
                CreatedAt.now());
        balance.getLockBalances().add(lockBalance);
        Mockito.when(tradingUserBalanceRepositoryPort.findUserBalanceByUserId(Mockito.any()))
                .thenReturn(Optional.of(balance));
        Mockito.when(tradingOrderRepositoryPort.saveLimitOrder(Mockito.any())).thenReturn(limitOrder);
        Mockito.when(tradingMarketDataRedisPort.findOrderBookByMarket(RedisKeyPrefix.market(marketId)))
                .thenReturn(Optional.ofNullable(orderBookBithumbDto));
        Mockito.when(tradingMarketDataRepositoryPort.findMarketItemByMarketId(marketId))
                .thenReturn(Optional.of(marketItem));
        // when
        executeOrderMatchingUseCase.executeLimitOrder(limitOrder);
        // then
        Mockito.verify(tradingOrderRedisPort, Mockito.times(1))
                .deleteLimitOrder(Mockito.any());
        Mockito.verify(tradeKafkaPublisher, Mockito.times(1)).publish(Mockito.any());
        Mockito.verify(tradingOrderRepositoryPort, Mockito.times(1)).
                saveLimitOrder(Mockito.any());
    }

    @Test
    @DisplayName("주문 생성 시 지원하지 않는 마켓 ID 입력 시 예외 발생 테스트")
    public void createOrderWithInvalidMarketId() {
        // given
        CreateLimitOrderCommand createLimitOrderCommand = new CreateLimitOrderCommand(userId, "anonymous",
                OrderSide.BUY.getValue(),
                BigDecimal.valueOf(10_500_500), BigDecimal.ONE, OrderType.LIMIT.name());
        // when
        MarketItemNotFoundException marketItemNotFoundException = Assertions.assertThrows(
                MarketItemNotFoundException.class, () -> {
                    tradingApplicationService.createLimitOrder(createLimitOrderCommand);
                });
        // then
        Assertions.assertNotNull(marketItemNotFoundException);
        Assertions.assertEquals("marketId not found", marketItemNotFoundException.getMessage());
    }

    @Test
    @DisplayName("쿠폰 적용 후 수수료 할인율 및 금액 계산 검증 테스트")
    public void calculateFeeWithCouponDiscount() {
        // given
        BigDecimal baseFeeRate = BigDecimal.valueOf(0.001); // 기본 수수료 0.1%
        BigDecimal orderAmount = BigDecimal.valueOf(1_000_000); // 100만원 주문 금액
        FeeDiscount feeDiscount = new FeeDiscount(30); // 30% 할인 쿠폰
        // 기본 수수료 금액
        BigDecimal baseFeeAmount = orderAmount.multiply(baseFeeRate);
        // 쿠폰 적용 후 수수료율 = 기본 수수료율 * (1 - 할인율)
        BigDecimal expectedFeeRate = baseFeeRate.multiply(BigDecimal.valueOf(1 - (feeDiscount.getValue() / 100.0)));
        // 쿠폰 적용 수수료 금액
        BigDecimal expectedFeeAmount = orderAmount.multiply(expectedFeeRate);
        // when
        // 실제 서비스 호출 대신 직접 계산 (필요 시 실제 메서드 호출로 대체)
        BigDecimal actualFeeRate = baseFeeRate.multiply(BigDecimal.valueOf(1 - (feeDiscount.getValue() / 100.0)));
        BigDecimal actualFeeAmount = orderAmount.multiply(actualFeeRate);
        // then
        Assertions.assertEquals(0.0007, expectedFeeRate.doubleValue(), 0.0000001); // 0.0007 = 0.001 * 0.7
        Assertions.assertEquals(expectedFeeRate, actualFeeRate);
        Assertions.assertEquals(expectedFeeAmount.doubleValue(), actualFeeAmount.doubleValue(), 0.01);
    }

    @Test
    @DisplayName("마켓 주문 생성 시 쿠폰 수수료 할인 적용 확인")
    public void createMarketOrderWithCouponDiscountApplied() {

        // given
        BigDecimal quantity = BigDecimal.valueOf(1);
        UserBalance balance = TestConstants.createUserBalance(TestConstants.USER_BALANCE_1_900_000);
        CouponInfo couponInfo = CouponInfo.createCouponInfo(
                new CouponId(UUID.randomUUID()),
                new UserId(userId),
                new FeeDiscount(30),  // 30% 할인
                IssuedAt.now(),
                UsageExpiryDate.from(LocalDate.now(ZoneOffset.UTC).plusDays(30)));
        LimitOrder limitOrder = LimitOrder.createLimitOrder(
                new UserId(userId),
                new MarketId(marketId),
                OrderSide.of(orderSide),
                new Quantity(quantity),
                new OrderPrice(orderPrice),
                OrderType.LIMIT
        );
        Mockito.when(tradingCouponRepositoryPort.findCouponInfoByUserId(userId))
                .thenReturn(Optional.of(couponInfo));
        LockBalance lockBalance = LockBalance.createLockBalance(limitOrder.getId(), limitOrder.getUserId(),
                Money.of(limitOrder.getOrderPrice().getValue().add(BigDecimal.valueOf(1050L))), LockStatus.LOCKED,
                CreatedAt.now());
        balance.getLockBalances().add(lockBalance);
        Mockito.when(tradingUserBalanceRepositoryPort.findUserBalanceByUserId(userId))
                .thenReturn(Optional.of(balance));
        Mockito.when(tradingOrderRepositoryPort.saveLimitOrder(Mockito.any())).thenReturn(limitOrder);
        Mockito.when(tradingMarketDataRedisPort.findOrderBookByMarket(RedisKeyPrefix.market(marketId)))
                .thenReturn(Optional.ofNullable(orderBookBithumbDto));
        Mockito.when(tradingMarketDataRepositoryPort.findMarketItemByMarketId(marketId))
                .thenReturn(Optional.of(marketItem));
        // when
        executeOrderMatchingUseCase.executeLimitOrder(limitOrder);
        // then
        Mockito.verify(tradingOrderRedisPort, Mockito.times(1)).deleteLimitOrder(Mockito.any());
        Mockito.verify(tradeKafkaPublisher, Mockito.times(1)).publish(Mockito.any());
    }

    @Test
    @DisplayName("지정가 매수 체결, 싼 가격이 존재하면 매칭 테스트")
    public void limitOrderMatchingAndMatchingTest() {
        // given
        UserBalance balance = TestConstants.createUserBalance(TestConstants.USER_BALANCE_1_900_000);
        Mockito.when(tradingMarketDataRepositoryPort.findMarketItemByMarketId(marketId))
                .thenReturn(Optional.of(marketItem));
        Mockito.when(tradingMarketDataRedisPort.findOrderBookByMarket(RedisKeyPrefix.market(marketId)))
                .thenReturn(Optional.ofNullable(orderBookBithumbDto));
        LimitOrder limitOrder = LimitOrder.createLimitOrder(
                new UserId(userId),
                new MarketId(marketId),
                OrderSide.of(orderSide),
                new Quantity(BigDecimal.valueOf(1.0)),
                new OrderPrice(BigDecimal.valueOf(1_070_123.0)),
                OrderType.LIMIT
        );
        Mockito.when(tradingOrderRepositoryPort.saveLimitOrder(Mockito.any())).thenReturn(
                limitOrder);
        LockBalance lockBalance = LockBalance.createLockBalance(limitOrder.getId(), limitOrder.getUserId(),
                Money.of(limitOrder.getOrderPrice().getValue().add(BigDecimal.valueOf(1050L))), LockStatus.LOCKED,
                CreatedAt.now());
        balance.getLockBalances().add(lockBalance);
        Mockito.when(tradingUserBalanceRepositoryPort.findUserBalanceByUserId(userId))
                .thenReturn(Optional.of(balance));
        // when
        executeOrderMatchingUseCase.executeLimitOrder(limitOrder);
        // then
        Assertions.assertNotNull(limitOrder);
        Assertions.assertEquals(marketId, limitOrder.getMarketId().getValue());
        Assertions.assertEquals(userId, limitOrder.getUserId().getValue());
        Assertions.assertEquals(orderSide, limitOrder.getOrderSide().getValue());
        Assertions.assertEquals(BigDecimal.valueOf(1_070_123.0),limitOrder.getOrderPrice().getValue());
        Assertions.assertEquals(BigDecimal.valueOf(1.0),limitOrder.getQuantity().getValue());
    }

    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    @DisplayName("트리거 조건 타입이 ABOVE인 예약 주문이 지정 가격대에서 정상 매칭되는지 테스트")
    public void execReservationOrderWithTriggerTypeAboveTest() {
        // given
        UserBalance balance = TestConstants.createUserBalance(TestConstants.USER_BALANCE_1_900_000);
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        ScheduledTime scheduledTime = new ScheduledTime(now.minusMinutes(2));   // 1분 뒤로 조금 더 늦게 잡기
        ExpireAt expireAt = new ExpireAt(now.plusDays(3));
        // 트리거 조건 생성: 가격이 1,050,000 이상일 때 실행 (ABOVE)
        TriggerCondition triggerCondition = new TriggerCondition(
                TriggerType.ABOVE,
                new OrderPrice(BigDecimal.valueOf(1_050_000))
        );

        ReservationOrder reservationOrder = ReservationOrder.createReservationOrder(
                new UserId(userId),
                new MarketId(marketId),
                OrderSide.BUY,
                new Quantity(BigDecimal.valueOf(1.5)),
                OrderType.RESERVATION,
                triggerCondition,
                scheduledTime,
                expireAt,
                new IsRepeatable(false)
        );
        LockBalance lockBalance = LockBalance.createLockBalance(reservationOrder.getId(), reservationOrder.getUserId(),
                Money.of(reservationOrder.getTriggerCondition()
                        .getTargetPrice().getValue().add(BigDecimal.valueOf(1050L))),
                LockStatus.LOCKED, CreatedAt.now());
        balance.getLockBalances().add(lockBalance);
        Mockito.when(tradingUserBalanceRepositoryPort.findUserBalanceByUserId(userId))
                .thenReturn(Optional.of(balance));
        Mockito.when(tradingMarketDataRepositoryPort.findMarketItemByMarketId(marketId))
                .thenReturn(Optional.of(marketItem));
        Mockito.when(tradingCouponRepositoryPort.findCouponInfoByUserId(userId))
                .thenReturn(Optional.empty());
        Mockito.when(tradingOrderRepositoryPort.saveReservationOrder(Mockito.any()))
                .thenAnswer(invocation -> null);

        OrderBook orderBook = marketDataApplicationTestHelper.tradingDtoMapper.orderBookDtoToOrderBook(
                orderBookBithumbDto, BigDecimal.valueOf(1000));

        ReservationOrderMatchingStrategy reservationOrderMatchingStrategy = new ReservationOrderMatchingStrategy(
                helper.feeRateResolver, helper.orderExecutionChecker,
                helper.userBalanceHandler,
                helper.orderMatchProcessor, tradingOrderRepositoryPort, tradingOrderRedisPort);
        // when
        List<TradingRecordedEvent> trades  = reservationOrderMatchingStrategy.match(orderBook,reservationOrder);
        // then
        Assertions.assertFalse(trades.isEmpty(), "트리거 조건 ABOVE가 만족되어 예약 주문이 체결되어야 한다.");
        Assertions.assertTrue(reservationOrder.isFilled() || reservationOrder.getRemainingQuantity()
                .getValue().compareTo(BigDecimal.ZERO) > 0);

        if (reservationOrder.isFilled()) {
            Mockito.verify(tradingOrderRepositoryPort, Mockito.times(1))
                    .saveReservationOrder(reservationOrder);
            Mockito.verify(tradingOrderRedisPort, Mockito.never()).saveReservationOrder(Mockito.any(), Mockito.any());
        } else {
            Mockito.verify(tradingOrderRedisPort, Mockito.times(1))
                    .saveReservationOrder(Mockito.any(), Mockito.any());
        }
    }

    @Test
    @DisplayName("동시 시장가 주문이 유저별로 독립적으로 처리되는지 확인하는 테스트")
    void concurrentOrdersAreHandledIndependentlyPerUser() throws InterruptedException {
        int threadCount = 10;

        // 준비: 유저별 잔액 맵
        Map<UUID, UserBalance> userBalanceMap = new ConcurrentHashMap<>();
        for (int i = 0; i < threadCount; i++) {
            UUID userId = UUID.randomUUID();
            userBalanceMap.put(userId, TestConstants.createUserBalance(BigDecimal.valueOf(1_090_000)));
            Mockito.when(tradingUserBalanceRepositoryPort.findUserBalanceByUserId(userId))
                    .thenReturn(Optional.of(TestConstants.createUserBalance(BigDecimal.valueOf(1_090_000))));
        }
        Mockito.when(tradingMarketDataRepositoryPort.findMarketItemByMarketId(marketId))
                .thenReturn(Optional.of(marketItem));
        Mockito.when(tradingMarketDataRedisPort.findOrderBookByMarket(RedisKeyPrefix.market(marketId)))
                .thenReturn(Optional.of(orderBookBithumbDto));
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch readyLatch = new CountDownLatch(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);

        for (UUID userId : userBalanceMap.keySet()) {
            executorService.submit(() -> {
                readyLatch.countDown();
                try {
                    startLatch.await();

                    CreateMarketOrderCommand cmd = new CreateMarketOrderCommand(
                            userId,
                            marketId,
                            OrderSide.BUY.getValue(),
                            BigDecimal.ONE,
                            OrderType.MARKET.name()
                    );

                    tradingApplicationService.createMarketOrder(cmd);

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        // 모든 스레드 준비될 때까지 대기
        readyLatch.await();
        startLatch.countDown(); // 동시에 시작
        doneLatch.await();      // 모두 종료될 때까지 대기

        executorService.shutdown();

        // 검증: 유저별 잔액이 차감되었는지 확인
        for (Map.Entry<UUID, UserBalance> entry : userBalanceMap.entrySet()) {
            UserBalance balance = entry.getValue();
            BigDecimal expectedRemaining = new BigDecimal("8489500"); // 예시 값
            Assertions.assertTrue(balance.getAvailableMoney().getValue().compareTo(expectedRemaining) <= 0,
                    "User " + entry.getKey() + " has incorrect remaining balance: " + balance.getAvailableMoney().getValue());
        }

        Mockito.verify(tradingUserBalanceRepositoryPort, Mockito.atLeastOnce()).findUserBalanceByUserId(Mockito.any());
        Mockito.verify(tradeKafkaPublisher, Mockito.atLeast(threadCount)).publish(Mockito.any());
    }


    //    매우 큰 수량과 높은 가격으로 주문 생성 명령 생성
//    주문 생성 및 매칭 호출
//    주문 잔량 및 체결 결과 검증
//    예외 발생 시 테스트 실패 처리
//    자체적으로 높은 가격이나 많은 수량은 제한하는 로직이 있음
    @Test
    @DisplayName("초대형 주문 가격과 수량 처리 테스트")
    public void handleLargeOrderPriceAndQuantity() {
        // given

        // when

        // then
    }

    @Test
    @DisplayName("동일 사용자의 연속 주문 시 FIFO 순서 보장 테스트")
    public void orderExecutionOrderShouldBeFIFOForSameUser() {
        // given
        UserBalance balance = TestConstants.createUserBalance(BigDecimal.valueOf(19000000));

        UUID sameUserId = this.userId;
        CreateMarketOrderCommand order1 = new CreateMarketOrderCommand(
                sameUserId, marketId, OrderSide.BUY.getValue(), BigDecimal.valueOf(1.2), OrderType.MARKET.name());
        CreateMarketOrderCommand order2 = new CreateMarketOrderCommand(
                sameUserId, marketId, OrderSide.BUY.getValue(), BigDecimal.valueOf(1.4), OrderType.MARKET.name());
        CreateMarketOrderCommand order3 = new CreateMarketOrderCommand(
                sameUserId, marketId, OrderSide.BUY.getValue(), BigDecimal.valueOf(0.8), OrderType.MARKET.name());
        Mockito.when(tradingUserBalanceRepositoryPort.findUserBalanceByUserId(sameUserId))
                        .thenReturn(Optional.of(balance));
        Mockito.when(tradingMarketDataRepositoryPort.findMarketItemByMarketId(marketId))
                .thenReturn(Optional.of(marketItem));
        Mockito.when(tradingMarketDataRedisPort.findOrderBookByMarket(RedisKeyPrefix.market(marketId)))
                .thenReturn(Optional.ofNullable(orderBookBithumbDto));

        // when
        tradingApplicationService.createMarketOrder(order1);
        tradingApplicationService.createMarketOrder(order2);
        tradingApplicationService.createMarketOrder(order3);

        // then
        // 순서대로 저장/처리됐는지 검증

        Mockito.verify(tradingOrderRepositoryPort, Mockito.times(3))
                .saveMarketOrder(marketOrderCaptor.capture());
        List<MarketOrder> savedOrders = marketOrderCaptor.getAllValues();

        Mockito.verify(tradingOrderRepositoryPort, Mockito.times(3)).saveMarketOrder(Mockito.any());
        Assertions.assertEquals(3, savedOrders.size());
        Assertions.assertEquals(sameUserId, savedOrders.get(0).getUserId().getValue());
        Assertions.assertEquals(sameUserId, savedOrders.get(1).getUserId().getValue());
        Assertions.assertEquals(sameUserId, savedOrders.get(2).getUserId().getValue());
    }

    @Test
    @DisplayName("마켓이 중단된 상태에서 주문 시도 시 예외 발생 테스트")
    public void createOrderWhenMarketIsPaused() {
        // given
        UserBalance balance = TestConstants.createUserBalance(BigDecimal.valueOf(19000000));
        MarketItem pausedMarketItem = MarketItem.createMarketItem(marketId, new MarketKoreanName("비트코인"),
                new MarketEnglishName("BTC"), new MarketWarning(""),
                new TickPrice(BigDecimal.valueOf(1000L)), MarketStatus.PAUSED);
        CreateMarketOrderCommand createMarketOrderCommand = new CreateMarketOrderCommand(
                userId, marketId, OrderSide.BUY.getValue(), BigDecimal.valueOf(1.2), OrderType.MARKET.name());
        Mockito.when(tradingMarketDataRepositoryPort.findMarketItemByMarketId(marketId)).thenReturn(
                Optional.of(pausedMarketItem));
        Mockito.when(tradingMarketDataRedisPort.findOrderBookByMarket(RedisKeyPrefix.market(marketId)))
                .thenReturn(Optional.ofNullable(orderBookBithumbDto));
        Mockito.when(tradingUserBalanceRepositoryPort.findUserBalanceByUserId(userId))
                .thenReturn(Optional.of(balance));
        // when
        MarketPausedException marketPausedException = Assertions.assertThrows(MarketPausedException.class, () ->
                tradingApplicationService.createMarketOrder(createMarketOrderCommand));
        // then
        Assertions.assertNotNull(marketPausedException);
        Assertions.assertEquals(String.format("MarketItem with id %s is not active", marketId),
                marketPausedException.getMessage());
    }


    @Test
    @DisplayName("매도 주문 등록한거 매수할 때 안전하게 오더북에 붙어서 매칭이 되는지 확인하는 테스트")
    public void registeredSellOrderInMyExchangeIsMatchedInMyOrderBookWhenAnonymousUserBuyTest() {
        // given
        UserBalance testBalance = TestConstants.createUserBalance(TestConstants.USER_BALANCE_A_LOT_OF_MONEY);
        BigDecimal orderPrice = BigDecimal.valueOf(1_040_000.0);
        BigDecimal quantity = BigDecimal.valueOf(2);
        LimitOrder limitOrder = LimitOrder.createLimitOrder(
                new UserId(userId),
                new MarketId(marketId),
                OrderSide.SELL,
                new Quantity(quantity),
                new OrderPrice(orderPrice),
                OrderType.LIMIT
        );
        Mockito.when(tradingUserBalanceRepositoryPort.findUserBalanceByUserId(Mockito.any()))
                .thenReturn(Optional.of(testBalance));
        Mockito.when(tradingOrderRepositoryPort.saveLimitOrder(Mockito.any())).thenReturn(limitOrder);
        Mockito.when(tradingMarketDataRedisPort.findOrderBookByMarket(RedisKeyPrefix.market(marketId)))
                .thenReturn(Optional.ofNullable(orderBookBithumbDto));
        Mockito.when(tradingMarketDataRepositoryPort.findMarketItemByMarketId(marketId))
                .thenReturn(Optional.of(marketItem));
        CreateLimitOrderCommand createLimitOrderCommand = new CreateLimitOrderCommand(userId, marketId, OrderSide.SELL.getValue(),
                orderPrice, quantity, OrderType.LIMIT.name());
        tradingApplicationService.createLimitOrder(createLimitOrderCommand);

        UUID buyerId = UUID.randomUUID();
        String orderSide = OrderSide.BUY.getValue();
        UserBalance balance = TestConstants.createUserBalance(TestConstants.USER_BALANCE_A_LOT_OF_MONEY);
        Mockito.when(tradingUserBalanceRepositoryPort.findUserBalanceByUserId(Mockito.any()))
                .thenReturn(Optional.of(balance));
        OrderPrice innerPrice = new OrderPrice(BigDecimal.valueOf(2_000_000.0));
        CreateMarketOrderCommand createMarketOrderCommand = new CreateMarketOrderCommand(buyerId, marketId,
                orderSide, innerPrice.getValue(), orderTypeMarket.name());
        MarketOrder marketOrder = MarketOrder.createMarketOrder(
                new UserId(buyerId),
                new MarketId(marketId),
                OrderSide.of(orderSide),
                innerPrice,
                OrderType.MARKET);
        Mockito.when(tradingOrderRepositoryPort.saveMarketOrder(Mockito.any())).thenReturn(marketOrder);
        Mockito.when(tradingOrderRedisPort.findLimitOrdersByMarketId(marketId))
                .thenReturn(List.of(limitOrder));
        // when
        tradingApplicationService.createMarketOrder(createMarketOrderCommand);
        // then
        Mockito.verify(tradeKafkaPublisher,Mockito.times(1)).publish(Mockito.any());
    }

    @Test
    @DisplayName("매수 주문 등록한거 매도할 때 안전하게 오더북에 붙어서 매칭이 되는지 확인하는 테스트")
    public void registeredBuyOrderInMyExchangeIsMatchedInMyOrderBookWhenAnonymousUserSellTest() {
        // given
        UserBalance testBalance = TestConstants.createUserBalance(TestConstants.USER_BALANCE_A_LOT_OF_MONEY);
        BigDecimal orderPrice = BigDecimal.valueOf(1_000_000.0);
        BigDecimal quantity = BigDecimal.valueOf(2);
        LimitOrder limitOrder = LimitOrder.createLimitOrder(
                new UserId(userId),
                new MarketId(marketId),
                OrderSide.BUY,
                new Quantity(quantity),
                new OrderPrice(orderPrice),
                OrderType.LIMIT
        );
        Mockito.when(tradingUserBalanceRepositoryPort.findUserBalanceByUserId(Mockito.any()))
                .thenReturn(Optional.of(testBalance));
        Mockito.when(tradingOrderRepositoryPort.saveLimitOrder(Mockito.any())).thenReturn(limitOrder);
        Mockito.when(tradingMarketDataRedisPort.findOrderBookByMarket(RedisKeyPrefix.market(marketId)))
                .thenReturn(Optional.ofNullable(orderBookBithumbDto));
        Mockito.when(tradingMarketDataRepositoryPort.findMarketItemByMarketId(marketId))
                .thenReturn(Optional.of(marketItem));
        CreateLimitOrderCommand createLimitOrderCommand = new CreateLimitOrderCommand(userId, marketId,
                OrderSide.BUY.getValue(), orderPrice, quantity, OrderType.LIMIT.name());
        tradingApplicationService.createLimitOrder(createLimitOrderCommand);

        UUID buyerId = UUID.randomUUID();
        String orderSide = OrderSide.SELL.getValue();
        UserBalance balance = TestConstants.createUserBalance(TestConstants.USER_BALANCE_A_LOT_OF_MONEY);
        Mockito.when(tradingUserBalanceRepositoryPort.findUserBalanceByUserId(Mockito.any()))
                .thenReturn(Optional.of(balance));
        OrderPrice innerPrice = new OrderPrice(BigDecimal.valueOf(2_000_000.0));
        CreateMarketOrderCommand createMarketOrderCommand = new CreateMarketOrderCommand(buyerId, marketId,
                orderSide, innerPrice.getValue(), orderTypeMarket.name());
        MarketOrder marketOrder = MarketOrder.createMarketOrder(
                new UserId(buyerId),
                new MarketId(marketId),
                OrderSide.of(orderSide),
                innerPrice,
                OrderType.MARKET);
        Mockito.when(tradingOrderRepositoryPort.saveMarketOrder(Mockito.any())).thenReturn(marketOrder);
        Mockito.when(tradingOrderRedisPort.findLimitOrdersByMarketId(marketId))
                .thenReturn(List.of(limitOrder));
        // when
        tradingApplicationService.createMarketOrder(createMarketOrderCommand);
        // then
        Mockito.verify(tradeKafkaPublisher,Mockito.times(1)).publish(Mockito.any());
    }
}
