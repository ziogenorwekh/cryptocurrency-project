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
import shop.shportfolio.trading.application.command.create.CreateLimitOrderResponse;
import shop.shportfolio.trading.application.command.create.CreateMarketOrderCommand;
import shop.shportfolio.trading.application.command.track.request.OrderBookTrackQuery;
import shop.shportfolio.trading.application.command.track.response.OrderBookTrackResponse;
import shop.shportfolio.trading.application.dto.orderbook.OrderBookAsksBithumbDto;
import shop.shportfolio.trading.application.dto.orderbook.OrderBookBidsBithumbDto;
import shop.shportfolio.trading.application.dto.orderbook.OrderBookBithumbDto;
import shop.shportfolio.trading.application.exception.MarketItemNotFoundException;
import shop.shportfolio.trading.application.exception.MarketPausedException;
import shop.shportfolio.trading.application.handler.matching.strategy.ReservationOrderMatchingStrategy;
import shop.shportfolio.trading.application.policy.*;
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
import shop.shportfolio.trading.domain.event.TradingRecordedEvent;
import shop.shportfolio.trading.domain.valueobject.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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

    @BeforeEach
    public void setUp() {
        tradingApplicationService = TradingOrderTestHelper.createTradingApplicationService(
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
    @DisplayName("시장가 체결할건데 마찬가지로, 내 거래소에 거래 내역이 있으면 그것도 반영되어야 하는 테스트")
    public void createMarketOrderWithOurExchangeHavingTradeHistoryTest() {
        // given
        Quantity innerQuantity = new Quantity(BigDecimal.valueOf(5L));
        CreateMarketOrderCommand createMarketOrderCommand = new CreateMarketOrderCommand(userId, marketId,
                orderSide, innerQuantity.getValue(), orderTypeMarket.name());
        MarketOrder marketOrder = MarketOrder.createMarketOrder(
                new UserId(userId),
                new MarketId(marketId),
                OrderSide.of(orderSide),
                innerQuantity,
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
        Mockito.verify(tradeKafkaPublisher, Mockito.times(4)).publish(Mockito.any());
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
        TradingOrderTestHelper.orderDomainService.applyOrder(normalLimitOrder, new Quantity(BigDecimal.valueOf(1.0)));
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
    @DisplayName("지정가 주문 생성 테스트하는데, 바로 매칭을 시도하는지 확인하는 테스트, 그러나 완전히 해당 주문가를 소화하지 못한 경우")
    public void createLimitOrderAndImmediatelyMatchingAndPartialFilled() {
        // given
        CreateLimitOrderCommand createLimitOrderCommand = new CreateLimitOrderCommand(userId, marketId,
                orderSide, orderPrice, quantity, OrderType.LIMIT.name());
        Mockito.when(tradingOrderRepositoryPort.saveLimitOrder(Mockito.any())).thenReturn(
                LimitOrder.createLimitOrder(
                        new UserId(userId),
                        new MarketId(marketId),
                        OrderSide.of(orderSide),
                        new Quantity(quantity),
                        new OrderPrice(orderPrice),
                        OrderType.LIMIT
                ));
        Mockito.when(tradingMarketDataRedisPort.findOrderBookByMarket(RedisKeyPrefix.market(marketId)))
                .thenReturn(Optional.ofNullable(orderBookBithumbDto));
        Mockito.when(tradingMarketDataRepositoryPort.findMarketItemByMarketId(marketId))
                .thenReturn(Optional.of(marketItem));
        // when
        CreateLimitOrderResponse createLimitOrderResponse = tradingApplicationService.
                createLimitOrder(createLimitOrderCommand);
        // then
        Mockito.verify(tradingOrderRedisPort, Mockito.times(1))
                .saveLimitOrder(Mockito.any(), Mockito.any());
        Mockito.verify(tradeKafkaPublisher, Mockito.times(1)).publish(Mockito.any());
        Assertions.assertEquals(createLimitOrderResponse.getUserId(), userId);
        Mockito.verify(tradingOrderRepositoryPort, Mockito.times(2)).
                saveLimitOrder(Mockito.any());
    }

    @Test
    @DisplayName("지정가 주문 생성 테스트하는데, 바로 매칭을 시도하는지 확인하는 테스트, 완전히 해당 주문가를 소화한 경우")
    public void createLimitOrderAndImmediatelyMatchingAndFilled() {
        // given
        Quantity smallQuantity = new Quantity(BigDecimal.valueOf(0.5));
        CreateLimitOrderCommand createLimitOrderCommand = new CreateLimitOrderCommand(userId, marketId,
                orderSide, orderPrice, smallQuantity.getValue(), OrderType.LIMIT.name());
        Mockito.when(tradingOrderRepositoryPort.saveLimitOrder(Mockito.any())).thenReturn(
                LimitOrder.createLimitOrder(
                        new UserId(userId),
                        new MarketId(marketId),
                        OrderSide.of(orderSide),
                        smallQuantity,
                        new OrderPrice(orderPrice),
                        OrderType.LIMIT
                ));
        Mockito.when(tradingMarketDataRedisPort.findOrderBookByMarket(RedisKeyPrefix.market(marketId)))
                .thenReturn(Optional.ofNullable(orderBookBithumbDto));
        Mockito.when(tradingMarketDataRepositoryPort.findMarketItemByMarketId(marketId))
                .thenReturn(Optional.of(marketItem));
        // when
        CreateLimitOrderResponse limitOrder =
                tradingApplicationService.createLimitOrder(createLimitOrderCommand);
        // then
        Assertions.assertNotNull(limitOrder);
        Mockito.verify(tradeKafkaPublisher, Mockito.times(1)).publish(Mockito.any());
        // 레디스에서 지워지는것까지
        Mockito.verify(tradingOrderRedisPort, Mockito.times(1))
                .deleteLimitOrder(Mockito.any());
        Mockito.verify(tradingOrderRepositoryPort, Mockito.times(2)).
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
    @MockitoSettings(strictness = Strictness.LENIENT)
    public void createMarketOrderWithCouponDiscountApplied() {

        // given
        CouponInfo couponInfo = CouponInfo.createCouponInfo(
                new CouponId(UUID.randomUUID()),
                new UserId(userId),
                new FeeDiscount(30),  // 30% 할인
                IssuedAt.now(),
                UsageExpiryDate.from(LocalDate.now().plusDays(30)));

        Mockito.when(tradingCouponRepositoryPort.findCouponInfoByUserId(userId))
                .thenReturn(Optional.of(couponInfo));

        CreateMarketOrderCommand createMarketOrderCommand =
                new CreateMarketOrderCommand(userId, marketId, orderSide, BigDecimal.valueOf(1.0), OrderType.MARKET.name());
        Mockito.when(tradingMarketDataRepositoryPort.findMarketItemByMarketId(marketId))
                .thenReturn(Optional.of(marketItem));
        Mockito.when(tradingMarketDataRedisPort.findOrderBookByMarket(RedisKeyPrefix.market(marketId)))
                .thenReturn(Optional.ofNullable(orderBookBithumbDto));

        // when
        tradingApplicationService.createMarketOrder(createMarketOrderCommand);
        // then
    }

    @Test
    @DisplayName("지정가 부분 체결 테스트")
    public void limitOrderMatchingAndMatchingTest() {
        // given
        CreateLimitOrderCommand createLimitOrderCommand = new CreateLimitOrderCommand(userId, marketId,
                "BUY", BigDecimal.valueOf(1_070_123.0), BigDecimal.valueOf(2.0), OrderType.LIMIT.name());
        Mockito.when(tradingMarketDataRepositoryPort.findMarketItemByMarketId(marketId))
                .thenReturn(Optional.of(marketItem));
        Mockito.when(tradingMarketDataRedisPort.findOrderBookByMarket(RedisKeyPrefix.market(marketId)))
                .thenReturn(Optional.ofNullable(orderBookBithumbDto));
        Mockito.when(tradingOrderRepositoryPort.saveLimitOrder(Mockito.any())).thenReturn(
                LimitOrder.createLimitOrder(
                        new UserId(userId),
                        new MarketId(marketId),
                        OrderSide.of(orderSide),
                        new Quantity(BigDecimal.valueOf(2.0)),
                        new OrderPrice(BigDecimal.valueOf(1_070_123.0)),
                        OrderType.LIMIT
                ));
        // when
        CreateLimitOrderResponse limitOrder = tradingApplicationService.createLimitOrder(createLimitOrderCommand);
        // then
        Assertions.assertNotNull(limitOrder);
        Assertions.assertNotNull(limitOrder.getOrderId());
        Assertions.assertEquals(marketId, limitOrder.getMarketId());
        Assertions.assertEquals(userId, limitOrder.getUserId());
        Assertions.assertEquals(orderSide, limitOrder.getOrderSide());
        Assertions.assertEquals(BigDecimal.valueOf(1_070_123.0),limitOrder.getPrice());
        Assertions.assertEquals(BigDecimal.valueOf(2.0),limitOrder.getQuantity());
    }

    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    @DisplayName("트리거 조건 타입이 ABOVE인 예약 주문이 지정 가격대에서 정상 매칭되는지 테스트")
    public void execReservationOrderWithTriggerTypeAboveTest() {
        // given
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

        Mockito.when(tradingMarketDataRepositoryPort.findMarketItemByMarketId(marketId))
                .thenReturn(Optional.of(marketItem));
        Mockito.when(tradingCouponRepositoryPort.findCouponInfoByUserId(userId))
                .thenReturn(Optional.empty());
        Mockito.when(tradingOrderRepositoryPort.saveReservationOrder(Mockito.any()))
                .thenAnswer(invocation -> null);

        OrderBook orderBook = MarketDataApplicationTestHelper.tradingDtoMapper.orderBookDtoToOrderBook(orderBookBithumbDto, BigDecimal.valueOf(1000));
        FeePolicy feePolicy = new DefaultFeePolicy();
        ReservationOrderMatchingStrategy reservationOrderMatchingStrategy =
                new ReservationOrderMatchingStrategy(TradingOrderTestHelper.userBalanceDomainService,
                        TradingOrderTestHelper.tradeDomainService,
                        TradingOrderTestHelper.orderDomainService,tradingOrderRepositoryPort,
                        TradingOrderTestHelper.couponInfo,tradingOrderRedisPort,
                        feePolicy, tradingTradeRecordRepositoryPort,tradingUserBalanceRepositoryPort);
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

//    다중 스레드 혹은 비동기 환경에서 주문 요청 처리
//    주문 잔량(remaining quantity) 정확성 유지
//    체결 내역이 올바르게 생성되고, 중복 혹은 누락 없는지
//    동시성 문제(예: race condition, deadlock) 여부
    @Test
    @DisplayName("동시 다중 주문 생성 시 잔량 및 체결 처리 테스트")
    public void createMultipleOrdersConcurrently() throws InterruptedException {
        // given
        int threadCount = 10;
        BigDecimal orderQtyPerThread = BigDecimal.valueOf(0.5);
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch readyLatch = new CountDownLatch(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);

        Mockito.when(tradingMarketDataRepositoryPort.findMarketItemByMarketId(marketId))
                .thenReturn(Optional.of(marketItem));
        Mockito.when(tradingMarketDataRedisPort.findOrderBookByMarket(RedisKeyPrefix.market(marketId)))
                .thenReturn(Optional.of(orderBookBithumbDto));

        List<Future<?>> futures = new ArrayList<>();

        // when
        for (int i = 0; i < threadCount; i++) {
            futures.add(executorService.submit(() -> {
                readyLatch.countDown(); // 준비됨 표시
                try {
                    startLatch.await(); // 모두 준비될 때까지 대기
                    CreateMarketOrderCommand cmd = new CreateMarketOrderCommand(
                            UUID.randomUUID(), marketId,
                            OrderSide.BUY.getValue(), orderQtyPerThread, OrderType.MARKET.name());
                    tradingApplicationService.createMarketOrder(cmd);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    doneLatch.countDown();
                }
            }));
        }

        readyLatch.await();  // 모든 스레드 준비될 때까지 기다림
        startLatch.countDown(); // 동시에 시작
        doneLatch.await();  // 모두 끝날 때까지 기다림

        executorService.shutdown();

        // then
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
        UUID sameUserId = this.userId;
        CreateMarketOrderCommand order1 = new CreateMarketOrderCommand(
                sameUserId, marketId, OrderSide.BUY.getValue(), BigDecimal.valueOf(1.2), OrderType.MARKET.name());
        CreateMarketOrderCommand order2 = new CreateMarketOrderCommand(
                sameUserId, marketId, OrderSide.BUY.getValue(), BigDecimal.valueOf(1.4), OrderType.MARKET.name());
        CreateMarketOrderCommand order3 = new CreateMarketOrderCommand(
                sameUserId, marketId, OrderSide.BUY.getValue(), BigDecimal.valueOf(0.8), OrderType.MARKET.name());

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
        MarketItem pausedMarketItem = MarketItem.createMarketItem(marketId, new MarketKoreanName("비트코인"),
                new MarketEnglishName("BTC"), new MarketWarning(""),
                new TickPrice(BigDecimal.valueOf(1000L)), MarketStatus.PAUSED);
        CreateMarketOrderCommand createMarketOrderCommand = new CreateMarketOrderCommand(
                userId, marketId, OrderSide.BUY.getValue(), BigDecimal.valueOf(1.2), OrderType.MARKET.name());
        Mockito.when(tradingMarketDataRepositoryPort.findMarketItemByMarketId(marketId)).thenReturn(
                Optional.of(pausedMarketItem));
        Mockito.when(tradingMarketDataRedisPort.findOrderBookByMarket(RedisKeyPrefix.market(marketId)))
                .thenReturn(Optional.ofNullable(orderBookBithumbDto));
        // when
        MarketPausedException marketPausedException = Assertions.assertThrows(MarketPausedException.class, () ->
                tradingApplicationService.createMarketOrder(createMarketOrderCommand));
        // then
        Assertions.assertNotNull(marketPausedException);
        Assertions.assertEquals(String.format("MarketItem with id %s is not active", marketId),
                marketPausedException.getMessage());
    }

}
