package shop.shportfolio.trading.application.test;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.trading.application.handler.OrderBookManager;
import shop.shportfolio.trading.application.handler.UserBalanceHandler;
import shop.shportfolio.trading.application.handler.matching.OrderExecutionChecker;
import shop.shportfolio.trading.application.handler.matching.OrderMatchProcessor;
import shop.shportfolio.trading.application.handler.matching.FeeRateResolver;
import shop.shportfolio.trading.application.handler.matching.strategy.LimitOrderMatchingStrategy;
import shop.shportfolio.trading.application.handler.matching.strategy.MarketOrderMatchingStrategy;
import shop.shportfolio.trading.application.handler.matching.strategy.ReservationOrderMatchingStrategy;
import shop.shportfolio.trading.application.handler.matching.strategy.OrderMatchingStrategy;
import shop.shportfolio.trading.application.ports.input.ExecuteOrderMatchingUseCase;
import shop.shportfolio.trading.application.ports.output.kafka.TradeKafkaPublisher;
import shop.shportfolio.trading.application.ports.output.kafka.UserBalanceKafkaPublisher;
import shop.shportfolio.trading.application.ports.output.marketdata.BithumbApiPort;
import shop.shportfolio.trading.application.ports.output.redis.TradingOrderRedisPort;
import shop.shportfolio.trading.application.ports.output.repository.*;
import shop.shportfolio.trading.application.test.helper.OrderBookTestHelper;
import shop.shportfolio.trading.application.test.helper.TestConstants;
import shop.shportfolio.trading.application.test.helper.TradingOrderTestHelper;
import shop.shportfolio.trading.application.usecase.ExecuteOrderMatchingUseCaseImpl;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.orderbook.OrderBook;
import shop.shportfolio.trading.domain.entity.orderbook.PriceLevel;
import shop.shportfolio.trading.domain.entity.userbalance.LockBalance;
import shop.shportfolio.trading.domain.entity.userbalance.UserBalance;
import shop.shportfolio.trading.domain.valueobject.LockStatus;
import shop.shportfolio.trading.domain.valueobject.OrderSide;
import shop.shportfolio.trading.domain.valueobject.OrderType;
import shop.shportfolio.trading.domain.valueobject.TickPrice;

import java.math.BigDecimal;
import java.util.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class TradingScenarioTest {

    private TradingOrderTestHelper helper;

    private ExecuteOrderMatchingUseCase executeOrderMatchingUseCase;

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
    ArgumentCaptor<UserBalance> userBalanceCaptor;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        helper = new TradingOrderTestHelper();
        helper.createTradingApplicationService(orderRepo, tradeRecordRepo, orderRedis, marketRepo,
                couponRepo, kafkaPublisher, tradingUserBalanceRepository, userBalanceKafkaPublisher, bithumbApiPort);
        executeOrderMatchingUseCase = helper.getExecuteUseCase();
        OrderBookTestHelper.createOrderBook();
    }

    @Test
    @DisplayName("지정가 주문 매칭 시나리오 테스트 && 기존에 저장된 지정가 주문도 적용되는지 테스트")
    public void limitOrderMatchingTest() {
        // given
        List<LimitOrder> limitOrders = List.of(
                TestConstants.LIMIT_ORDER2,
                TestConstants.LIMIT_ORDER3,
                TestConstants.LIMIT_ORDER4
        );
        Mockito.when(orderRedis.findLimitOrdersByMarketId(TestConstants.TEST_MARKET_ID))
                .thenReturn(limitOrders);
        // 1_030_000.0 1.6이어야 함
        LimitOrder limitOrder = TestConstants.LIMIT_ORDER;
        Mockito.when(marketRepo.findMarketItemByMarketId(TestConstants.TEST_MARKET_ID))
                .thenReturn(Optional.of(TestConstants.MARKET_ITEM));
        UserBalance userBalance = TestConstants.createUserBalance(BigDecimal.ZERO);
        userBalance.getLockBalances().add(LockBalance.createLockBalance(limitOrder.getId(),
                limitOrder.getUserId(),
                Money.of(BigDecimal.valueOf(1_030_000.0)), LockStatus.LOCKED,
                CreatedAt.now()));
        Mockito.when(tradingUserBalanceRepository.findUserBalanceByUserId(TestConstants.TEST_USER_ID))
                .thenReturn(Optional.of(userBalance));
        // when
        executeOrderMatchingUseCase.executeLimitOrder(limitOrder);
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
        OrderBook orderBook = OrderBookTestHelper.getOrderBook(TestConstants.TEST_MARKET_ID);
        System.out.println("orderBook = " + orderBook);
    }

    @Test
    @DisplayName("다중 지정가 주문 매칭 시나리오 테스트")
    public void multiLimitOrderMatchingTest() throws InterruptedException {
        List<LimitOrder> limitOrders = List.of(
                TestConstants.LIMIT_ORDER2,
                TestConstants.LIMIT_ORDER3,
                TestConstants.LIMIT_ORDER4
        );
        Mockito.when(orderRedis.findLimitOrdersByMarketId(TestConstants.TEST_MARKET_ID))
                .thenReturn(limitOrders);

        LimitOrder limitOrder1 = TestConstants.LIMIT_ORDER;
        LimitOrder limitOrder2 = LimitOrder.createLimitOrder(new UserId(UUID.randomUUID()),
                new MarketId(TestConstants.TEST_MARKET_ID),
                OrderSide.BUY, new Quantity(BigDecimal.valueOf(1.0)),
                new OrderPrice(BigDecimal.valueOf(1_030_000.0)), OrderType.LIMIT);
        LimitOrder limitOrder3 = LimitOrder.createLimitOrder(new UserId(UUID.randomUUID()),
                new MarketId(TestConstants.TEST_MARKET_ID),
                OrderSide.BUY, new Quantity(BigDecimal.valueOf(0.5)),
                new OrderPrice(BigDecimal.valueOf(1_020_000.0)), OrderType.LIMIT);

        // 각 쓰레드마다 별도의 UserBalance 객체 생성
        UserBalance userBalance1 = TestConstants.createUserBalance(BigDecimal.ZERO);
        userBalance1.getLockBalances().add(LockBalance.createLockBalance(limitOrder1.getId(),
                limitOrder1.getUserId(),
                Money.of(BigDecimal.valueOf(1_050_000.0)), LockStatus.LOCKED,
                CreatedAt.now()));

        UserBalance userBalance2 = TestConstants.createUserBalance(BigDecimal.ZERO);
        userBalance2.getLockBalances().add(LockBalance.createLockBalance(
                limitOrder2.getId(), // 반드시 limitOrder2의 ID 사용
                limitOrder2.getUserId(),
                Money.of(BigDecimal.valueOf(1_050_000.0)), LockStatus.LOCKED,
                CreatedAt.now()));

        UserBalance userBalance3 = TestConstants.createUserBalance(BigDecimal.ZERO);
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
                .thenReturn(Optional.of(TestConstants.MARKET_ITEM));

        Thread thread1 = new Thread(() -> executeOrderMatchingUseCase.executeLimitOrder(limitOrder1));
        Thread thread2 = new Thread(() -> executeOrderMatchingUseCase.executeLimitOrder(limitOrder2));
        Thread thread3 = new Thread(() -> executeOrderMatchingUseCase.executeLimitOrder(limitOrder3));
        thread1.start();
        thread2.start();
        thread3.start();
        thread1.join();
        thread2.join();
        thread3.join();
        // then 0.3은 1_020_000.0에 체결되고
        // 0.7이 먼저 남고 이건 1_030_000.0에 체결됨
        // 지금 코파야
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