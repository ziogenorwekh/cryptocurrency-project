package shop.shportfolio.trading.application.test;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.trading.application.PredicatedTradeCreatedListenerImpl;
import shop.shportfolio.trading.application.command.create.CreateLimitOrderCommand;
import shop.shportfolio.trading.application.dto.trade.PredicatedTradeKafkaResponse;
import shop.shportfolio.trading.application.orderbook.matching.OrderMatchingExecutor;
import shop.shportfolio.trading.application.ports.input.TradingApplicationService;
import shop.shportfolio.trading.application.ports.input.kafka.PredicatedTradeCreatedListener;
import shop.shportfolio.trading.application.ports.output.kafka.TradeKafkaPublisher;
import shop.shportfolio.trading.application.ports.output.kafka.UserBalanceKafkaPublisher;
import shop.shportfolio.trading.application.ports.output.marketdata.BithumbApiPort;
import shop.shportfolio.trading.application.ports.output.redis.TradingOrderRedisPort;
import shop.shportfolio.trading.application.ports.output.repository.*;
import shop.shportfolio.trading.application.test.helper.OrderBookTestHelper;
import shop.shportfolio.trading.application.test.helper.TestConstants;
import shop.shportfolio.trading.application.test.helper.TestMapper;
import shop.shportfolio.trading.application.test.helper.TradingOrderTestHelper;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.MarketOrder;
import shop.shportfolio.trading.domain.entity.ReservationOrder;
import shop.shportfolio.trading.domain.entity.orderbook.OrderBook;
import shop.shportfolio.trading.domain.entity.trade.Trade;
import shop.shportfolio.trading.domain.entity.userbalance.LockBalance;
import shop.shportfolio.trading.domain.entity.userbalance.UserBalance;
import shop.shportfolio.trading.domain.event.TradeCreatedEvent;
import shop.shportfolio.trading.domain.exception.TradingDomainException;
import shop.shportfolio.trading.domain.valueobject.LockStatus;
import shop.shportfolio.trading.domain.valueobject.OrderSide;
import shop.shportfolio.trading.domain.valueobject.OrderStatus;
import shop.shportfolio.trading.domain.valueobject.OrderType;

import java.math.BigDecimal;
import java.util.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@ExtendWith(MockitoExtension.class)
public class TradingScenarioTest {

    private TradingOrderTestHelper helper;

    private OrderMatchingExecutor orderMatchingExecutor;

    @Mock
    private TradingOrderRepositoryPort orderRepo;
    @Mock
    private TradingTradeRecordRepositoryPort tradeRecordRepo;
    @Mock
    private TradingOrderRedisPort orderRedis;
    @Mock
    private TradingMarketDataRepositoryPort marketRepo;
    @Mock
    private TradingCouponRepositoryPort couponRepo;
    @Mock
    private TradeKafkaPublisher kafkaPublisher;
    @Mock
    private TradingUserBalanceRepositoryPort tradingUserBalanceRepository;
    @Mock
    private UserBalanceKafkaPublisher userBalanceKafkaPublisher;
    @Mock
    private BithumbApiPort bithumbApiPort;

    @Captor
    private ArgumentCaptor<UserBalance> userBalanceCaptor;
    @Captor
    private ArgumentCaptor<ReservationOrder> reservationOrderCaptor;

    private TradingApplicationService tradingApplicationService;

    private TestMapper testMapper;

    private PredicatedTradeCreatedListener listener;

    private TestConstants testConstants;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        helper = new TradingOrderTestHelper();
        testConstants = new TestConstants();
        OrderBookTestHelper.createOrderBook(); // 완전 초기화
        tradingApplicationService = helper.createTradingApplicationService(orderRepo,
                tradeRecordRepo, orderRedis, marketRepo,
                couponRepo, kafkaPublisher, tradingUserBalanceRepository, userBalanceKafkaPublisher, bithumbApiPort
        );
        orderMatchingExecutor = helper.getExecuteUseCase();
        testMapper = new TestMapper();
        listener = new PredicatedTradeCreatedListenerImpl(helper.getUserBalanceHandler(), kafkaPublisher,
                userBalanceKafkaPublisher, orderRepo, helper.getFeeRateResolver(), helper.getTradeDomainService(),
                tradeRecordRepo, helper.getOrderDomainService());

    }

    @Test
    @DisplayName("지정가 주문 매칭 시나리오 테스트 && 기존에 저장된 지정가 주문도 적용되는지 테스트")
    public void limitOrderBuyTest() {
        // given
        List<LimitOrder> limitOrders = List.of(
                testConstants.limitOrder2Sell(),
                testConstants.limitOrder3Sell(),
                testConstants.limitOrder4Sell()
        );
        Mockito.when(orderRedis.findLimitOrdersByMarketId(TestConstants.TEST_MARKET_ID))
                .thenReturn(limitOrders);
        // 1_030_000.0 1.6이어야 함
        LimitOrder limitOrder = testConstants.LIMIT_ORDER_BUY;
        Mockito.when(marketRepo.findMarketItemByMarketId(TestConstants.TEST_MARKET_ID))
                .thenReturn(Optional.of(testConstants.MARKET_ITEM));
        UserBalance userBalance = testConstants.createUserBalance(BigDecimal.ZERO);
        userBalance.getLockBalances().add(LockBalance.createLockBalance(limitOrder.getId(),
                limitOrder.getUserId(),
                Money.of(BigDecimal.valueOf(1_030_000.0)), LockStatus.LOCKED,
                CreatedAt.now()));
        Mockito.when(tradingUserBalanceRepository.findUserBalanceByUserId(TestConstants.TEST_USER_ID))
                .thenReturn(Optional.of(userBalance));
        // when
        List<TradeCreatedEvent> tradeCreatedEvents = orderMatchingExecutor.executeLimitOrder(limitOrder);


        // then 0.3은 1_020_000.0에 체결되고
        // 0.7이 먼저 남고 이건 1_030_000.0에 체결됨
        Mockito.verify(kafkaPublisher, Mockito.times(2)).publish(Mockito.any());
        Mockito.verify(userBalanceKafkaPublisher, Mockito.times(1)).publish(Mockito.any());
        Mockito.verify(orderRedis, Mockito.times(1)).deleteLimitOrder(Mockito.any());
        Mockito.verify(tradingUserBalanceRepository, Mockito.times(3)).saveUserBalance(Mockito.any());
        Mockito.verify(tradeRecordRepo, Mockito.times(2)).saveTrade(Mockito.any());
        Mockito.verify(orderRepo, Mockito.times(1)).saveLimitOrder(Mockito.any());
        Mockito.verify(tradingUserBalanceRepository, Mockito.times(3)).saveUserBalance(userBalanceCaptor.capture());
        List<UserBalance> capturedUserBalances = userBalanceCaptor.getAllValues();
        Assertions.assertEquals(capturedUserBalances.get(2).getAvailableMoney(), userBalance.getAvailableMoney());
        System.out.println("userBalance.getAvailableMoney() = " + userBalance.getAvailableMoney().getValue());
        Mockito.reset(kafkaPublisher, userBalanceKafkaPublisher, orderRedis,
                tradingUserBalanceRepository, tradeRecordRepo, orderRepo);
        // given
        System.out.println("-".repeat(200));
        testConstants = new TestConstants();
        LimitOrder limitOrder2 = testConstants.LIMIT_ORDER_BUY;
        UserBalance userBalance2 = testConstants.createUserBalance(BigDecimal.ZERO);
        userBalance2.getLockBalances().add(LockBalance.createLockBalance(limitOrder2.getId(),
                limitOrder2.getUserId(),
                Money.of(BigDecimal.valueOf(1_030_000.0)), LockStatus.LOCKED,
                CreatedAt.now()));
        Mockito.when(tradingUserBalanceRepository.findUserBalanceByUserId(limitOrder2.getUserId().getValue()))
                .thenReturn(Optional.of(userBalance2));
        // when
        tradeCreatedEvents.forEach(event -> {
            Trade trade = event.getDomainType(); // <- 이거 안 됨, event 안에 trade를 직접 꺼내야 함
            Mockito.when(orderRepo.findLimitOrderByOrderId(trade.getBuyOrderId().getValue()))
                    .thenReturn(Optional.of(limitOrder2));
            PredicatedTradeKafkaResponse response =
                    testMapper.reservationOrderToPredicatedTradeKafkaResponse(trade, event.getMessageType());
            listener.updateLimitOrder(response);
        });
        // then
        Mockito.verify(kafkaPublisher, Mockito.times(2)).publish(Mockito.any());
        Mockito.verify(userBalanceKafkaPublisher, Mockito.times(4)).publish(Mockito.any());
        Mockito.verify(tradingUserBalanceRepository, Mockito.times(3)).saveUserBalance(Mockito.any());
        Mockito.verify(tradeRecordRepo, Mockito.times(2)).saveTrade(Mockito.any());
        Mockito.verify(orderRepo, Mockito.times(2)).saveLimitOrder(Mockito.any());
    }

    @Test
    @DisplayName("지정가 매도 시나리오 테스트")
    public void limitOrderSellTest() {
        // given
        List<LimitOrder> limitOrders = List.of(
                testConstants.limitOrder2Buy(),
                testConstants.limitOrder3Buy(),
                testConstants.limitOrder4Buy()
        );
        Mockito.when(orderRedis.findLimitOrdersByMarketId(TestConstants.TEST_MARKET_ID))
                .thenReturn(limitOrders);
        LimitOrder limitOrder = testConstants.LIMIT_ORDER_SELL;
        UserBalance userBalance = testConstants.createUserBalance(limitOrder.getUserId(), BigDecimal.ZERO);
        Mockito.when(tradingUserBalanceRepository.findUserBalanceByUserId(TestConstants.TEST_USER_ID))
                .thenReturn(Optional.of(userBalance));
        Mockito.when(marketRepo.findMarketItemByMarketId(TestConstants.TEST_MARKET_ID))
                .thenReturn(Optional.of(testConstants.MARKET_ITEM));
        // when
        orderMatchingExecutor.executeLimitOrder(limitOrder);
        // then
        Mockito.verify(tradingUserBalanceRepository, Mockito.times(1)).saveUserBalance(userBalanceCaptor.capture());
        UserBalance balance = userBalanceCaptor.getValue();
        Assertions.assertEquals(BigDecimal.valueOf(1_020_000.0), balance.getAvailableMoney().getValue());
    }

    @Test
    @DisplayName("예약가 구매 주문 매칭 시나리오 테스트")
    public void reservationOrderBuyScenarioTest() {
        // given
        List<LimitOrder> limitOrders = List.of(
                testConstants.limitOrder2Sell(),
                testConstants.limitOrder3Sell(),
                testConstants.limitOrder4Sell()
        );
        Mockito.when(orderRedis.findLimitOrdersByMarketId(TestConstants.TEST_MARKET_ID))
                .thenReturn(limitOrders);
        ReservationOrder reservationOrder = testConstants.RESERVATION_ORDER_BUY;
        Mockito.when(marketRepo.findMarketItemByMarketId(TestConstants.TEST_MARKET_ID))
                .thenReturn(Optional.of(testConstants.MARKET_ITEM));
        UserBalance userBalance = testConstants.createUserBalance(BigDecimal.ZERO);
        userBalance.getLockBalances().add(LockBalance.createLockBalance(reservationOrder.getId(),
                reservationOrder.getUserId(),
                Money.of(BigDecimal.valueOf(2_000_000.0)), LockStatus.LOCKED,
                CreatedAt.now()));
        Mockito.when(tradingUserBalanceRepository.findUserBalanceByUserId(reservationOrder.getUserId().getValue()))
                .thenReturn(Optional.of(userBalance));
        // when
        orderMatchingExecutor.executeReservationOrder(reservationOrder);
        // then
        Mockito.verify(orderRedis, Mockito.times(1)).saveReservationOrder(Mockito.any(),
                reservationOrderCaptor.capture());
        ReservationOrder orderCaptorValue = reservationOrderCaptor.getValue();
        Assertions.assertEquals(OrderStatus.PARTIALLY_FILLED, orderCaptorValue.getOrderStatus());
        Assertions.assertEquals(BigDecimal.valueOf(3.1), orderCaptorValue.getRemainingQuantity().getValue());
        Mockito.verify(userBalanceKafkaPublisher, Mockito.times(1)).publish(Mockito.any());
    }

    @Test
    @DisplayName("예약가 매도 시나리오 테스트")
    public void reservationOrderSellScenarioTest() {
        // given
        List<LimitOrder> limitOrders = List.of(
                testConstants.limitOrder2Buy(),
                testConstants.limitOrder3Buy(),
                testConstants.limitOrder4Buy()
        );
        // 1_000_000.0 에 지금 1.2개 산다고 하고,
        // 1_030_000.0 에 지금 1.2개 산다고 되어있으니까,
        // 예약가 주문인 1_000_000.0 에 두개를 해당 가격 이상일 경우 판다고 했으니까,
        // 대략 캡쳐 가격은 2,020,000.0 이상일것이다.
        Mockito.when(orderRedis.findLimitOrdersByMarketId(TestConstants.TEST_MARKET_ID))
                .thenReturn(limitOrders);
        // 1_000_000.0에 2개 팔고,
        ReservationOrder reservationOrder = testConstants.RESERVATION_ORDER_SELL;
        Mockito.when(marketRepo.findMarketItemByMarketId(TestConstants.TEST_MARKET_ID))
                .thenReturn(Optional.of(testConstants.MARKET_ITEM));
        UserBalance userBalance = testConstants.createUserBalance(BigDecimal.ZERO);
        Mockito.when(tradingUserBalanceRepository.findUserBalanceByUserId(reservationOrder.getUserId().getValue()))
                .thenReturn(Optional.of(userBalance));
        // when
        orderMatchingExecutor.executeReservationOrder(reservationOrder);
        // then
        Mockito.verify(tradingUserBalanceRepository, Mockito.times(2))
                .saveUserBalance(userBalanceCaptor.capture());
        UserBalance value = userBalanceCaptor.getAllValues().get(1);
        System.out.println("value.getAvailableMoney().getValue() = " + value.getAvailableMoney().getValue());
        Assertions.assertTrue(value.getAvailableMoney().getValue().compareTo(BigDecimal.valueOf(2_024_010.0)) > 0);
    }

    @Test
    @DisplayName("시장가 구매 시나리오 테스트")
    public void marketOrderBuyTest() {
        // given
        List<LimitOrder> limitOrders = List.of(
                testConstants.limitOrder2Sell(),
                testConstants.limitOrder3Sell(),
                testConstants.limitOrder4Sell()
        );
        MarketOrder marketOrder = testConstants.MARKET_ORDER_BUY;
        Mockito.when(orderRedis.findLimitOrdersByMarketId(TestConstants.TEST_MARKET_ID))
                .thenReturn(limitOrders);
        // 1_000_000.0에 2개 팔고,
        Mockito.when(marketRepo.findMarketItemByMarketId(TestConstants.TEST_MARKET_ID))
                .thenReturn(Optional.of(testConstants.MARKET_ITEM));
        UserBalance userBalance = testConstants.createUserBalance(BigDecimal.ZERO);
        userBalance.getLockBalances().add(LockBalance.createLockBalance(marketOrder.getId(), marketOrder.getUserId(),
                Money.of(BigDecimal.valueOf(1_035_000.0)), LockStatus.LOCKED, CreatedAt.now()
        ));
        Mockito.when(tradingUserBalanceRepository.findUserBalanceByUserId(marketOrder.getUserId().getValue()))
                .thenReturn(Optional.of(userBalance));
        // when
        orderMatchingExecutor.executeMarketOrder(marketOrder);
        // then
        Mockito.verify(tradingUserBalanceRepository, Mockito.times(3))
                .saveUserBalance(userBalanceCaptor.capture());
        UserBalance value = userBalanceCaptor.getAllValues().get(2);
        Assertions.assertTrue(value.getLockBalances().isEmpty());
    }

    @Test
    @DisplayName("시장가 판매 시나리오 테스트")
    public void marketOrderSellTest() {
        // given
        List<LimitOrder> limitOrders = List.of(
                testConstants.limitOrder2Buy(),
                testConstants.limitOrder3Buy(),
                testConstants.limitOrder4Buy()
        );
        // when
        MarketOrder marketOrder = testConstants.MARKET_ORDER_SELL;
        Mockito.when(orderRedis.findLimitOrdersByMarketId(TestConstants.TEST_MARKET_ID))
                .thenReturn(limitOrders);
        // 1_000_000.0에 2개 팔고,
        Mockito.when(marketRepo.findMarketItemByMarketId(TestConstants.TEST_MARKET_ID))
                .thenReturn(Optional.of(testConstants.MARKET_ITEM));
        UserBalance userBalance = testConstants.createUserBalance(BigDecimal.ZERO);
        Mockito.when(tradingUserBalanceRepository.findUserBalanceByUserId(marketOrder.getUserId().getValue()))
                .thenReturn(Optional.of(userBalance));
        // then
        orderMatchingExecutor.executeMarketOrder(marketOrder);
        Mockito.verify(tradingUserBalanceRepository, Mockito.times(1)).saveUserBalance(userBalanceCaptor.capture());
        UserBalance value = userBalanceCaptor.getValue();
        Assertions.assertEquals(0, value.getAvailableMoney().getValue().compareTo(BigDecimal.valueOf(1_020_000.0)));
    }

    @Test
    @DisplayName("잔고 부족 예외 테스트")
    public void insufficientBalanceTest() {
        // given
        LimitOrder limitOrder = testConstants.LIMIT_ORDER_BUY;
        UserBalance insufficientBalance = testConstants.createUserBalance(BigDecimal.valueOf(1000)); // 매우 적은 잔고
        Mockito.when(tradingUserBalanceRepository.findUserBalanceByUserId(limitOrder.getUserId().getValue()))
                .thenReturn(Optional.of(insufficientBalance));
        Mockito.when(marketRepo.findMarketItemByMarketId(TestConstants.TEST_MARKET_ID))
                .thenReturn(Optional.of(testConstants.MARKET_ITEM));
        CreateLimitOrderCommand command = new CreateLimitOrderCommand(limitOrder.getUserId().getValue(), limitOrder.getMarketId().getValue()
                , limitOrder.getOrderSide().getValue(), limitOrder.getOrderPrice().getValue(), limitOrder.getQuantity().getValue(),
                limitOrder.getOrderType().name());
        Mockito.when(orderRepo.saveLimitOrder(Mockito.any())).thenReturn(limitOrder);
        // when & then
        Assertions.assertThrows(TradingDomainException.class, () -> {
            tradingApplicationService.createLimitOrder(command);
        });
        Mockito.verify(tradingUserBalanceRepository, Mockito.never()).saveUserBalance(Mockito.any());
    }

    @Disabled("It is not use")
    @Test
    @DisplayName("다중 지정가 주문 매칭 시나리오 테스트")
    public void multiLimitOrderMatchingTest() throws InterruptedException {
        List<LimitOrder> limitOrders = List.of(
                testConstants.limitOrder2Sell(),
                testConstants.limitOrder3Sell(),
                testConstants.limitOrder4Sell()
        );
        Mockito.when(orderRedis.findLimitOrdersByMarketId(TestConstants.TEST_MARKET_ID))
                .thenReturn(limitOrders);

        LimitOrder limitOrder1 = testConstants.LIMIT_ORDER_BUY;
        LimitOrder limitOrder2 = LimitOrder.createLimitOrder(new UserId(UUID.randomUUID()),
                new MarketId(TestConstants.TEST_MARKET_ID),
                OrderSide.BUY, new Quantity(BigDecimal.valueOf(1.0)),
                new OrderPrice(BigDecimal.valueOf(1_030_000.0)), OrderType.LIMIT);
        LimitOrder limitOrder3 = LimitOrder.createLimitOrder(new UserId(UUID.randomUUID()),
                new MarketId(TestConstants.TEST_MARKET_ID),
                OrderSide.BUY, new Quantity(BigDecimal.valueOf(0.5)),
                new OrderPrice(BigDecimal.valueOf(1_020_000.0)), OrderType.LIMIT);

        // 각 쓰레드마다 별도의 UserBalance 객체 생성
        UserBalance userBalance1 = testConstants.createUserBalance(BigDecimal.ZERO);
        userBalance1.getLockBalances().add(LockBalance.createLockBalance(limitOrder1.getId(),
                limitOrder1.getUserId(),
                Money.of(BigDecimal.valueOf(1_050_000.0)), LockStatus.LOCKED,
                CreatedAt.now()));

        UserBalance userBalance2 = testConstants.createUserBalance(BigDecimal.ZERO);
        userBalance2.getLockBalances().add(LockBalance.createLockBalance(
                limitOrder2.getId(), // 반드시 limitOrder2의 ID 사용
                limitOrder2.getUserId(),
                Money.of(BigDecimal.valueOf(1_050_000.0)), LockStatus.LOCKED,
                CreatedAt.now()));

        UserBalance userBalance3 = testConstants.createUserBalance(BigDecimal.ZERO);
        userBalance3.getLockBalances().add(LockBalance.createLockBalance(
                limitOrder3.getId(), // 반드시 limitOrder3의 ID 사용
                limitOrder3.getUserId(),
                Money.of(BigDecimal.valueOf(510_000.0)), LockStatus.LOCKED,
                CreatedAt.now()));

        Mockito.when(tradingUserBalanceRepository.findUserBalanceByUserId(limitOrder1.getUserId().getValue()))
                .thenReturn(Optional.of(userBalance1));
        Mockito.when(tradingUserBalanceRepository.findUserBalanceByUserId(limitOrder2.getUserId().getValue()))
                .thenReturn(Optional.of(userBalance2));
        Mockito.when(tradingUserBalanceRepository.findUserBalanceByUserId(limitOrder3.getUserId().getValue()))
                .thenReturn(Optional.of(userBalance3));
        Mockito.when(marketRepo.findMarketItemByMarketId(TestConstants.TEST_MARKET_ID))
                .thenReturn(Optional.of(testConstants.MARKET_ITEM));

        Thread thread1 = new Thread(() -> orderMatchingExecutor.executeLimitOrder(limitOrder1));
        Thread thread2 = new Thread(() -> orderMatchingExecutor.executeLimitOrder(limitOrder2));
        Thread thread3 = new Thread(() -> orderMatchingExecutor.executeLimitOrder(limitOrder3));
        thread1.start();
        thread2.start();
        thread3.start();
        thread1.join();
        thread2.join();
        thread3.join();
        // then 0.3은 1_020_000.0에 체결되고
        // 0.7이 먼저 남고 이건 1_030_000.0에 체결됨
        Mockito.verify(kafkaPublisher, Mockito.times(3)).publish(Mockito.any());
        Mockito.verify(userBalanceKafkaPublisher, Mockito.times(3)).publish(Mockito.any());
        Mockito.verify(orderRedis, Mockito.times(2)).deleteLimitOrder(Mockito.any());
        Mockito.verify(tradingUserBalanceRepository, Mockito.times(5)).saveUserBalance(Mockito.any());
        Mockito.verify(tradeRecordRepo, Mockito.times(3)).saveTrade(Mockito.any());
        Mockito.verify(orderRepo, Mockito.times(2)).saveLimitOrder(Mockito.any());
        Mockito.verify(tradingUserBalanceRepository, Mockito.times(5)).saveUserBalance(userBalanceCaptor.capture());
        OrderBook orderBook = OrderBookTestHelper.getOrderBook(TestConstants.TEST_MARKET_ID);
        System.out.println("orderBook = " + orderBook);
    }
}