package shop.shportfolio.trading.application.test;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.trading.application.ports.input.kafka.PredicatedTradeListener;
import shop.shportfolio.trading.application.ports.input.kafka.impl.PredicatedTradeListenerImpl;
import shop.shportfolio.trading.application.command.create.CreateLimitOrderCommand;
import shop.shportfolio.trading.application.dto.trade.PredicatedTradeKafkaResponse;
import shop.shportfolio.trading.application.ports.input.TradingApplicationService;
import shop.shportfolio.trading.application.ports.output.kafka.*;
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
import shop.shportfolio.trading.domain.entity.trade.Trade;
import shop.shportfolio.trading.domain.entity.userbalance.LockBalance;
import shop.shportfolio.trading.domain.entity.userbalance.UserBalance;
import shop.shportfolio.trading.domain.event.TradeCreatedEvent;
import shop.shportfolio.trading.domain.exception.TradingDomainException;
import shop.shportfolio.trading.domain.valueobject.LockStatus;
import shop.shportfolio.trading.domain.valueobject.OrderStatus;
import shop.shportfolio.trading.domain.valueobject.OrderType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@ExtendWith(MockitoExtension.class)
public class TradingScenarioTest {

    private TradingOrderTestHelper helper;

//    private OrderMatchingExecutor orderMatchingExecutor;

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
    private TradePublisher kafkaPublisher;
    @Mock
    private TradingUserBalanceRepositoryPort tradingUserBalanceRepository;
    @Mock
    private UserBalancePublisher userBalancePublisher;
    @Mock
    private BithumbApiPort bithumbApiPort;
    @Mock
    private LimitOrderCreatedPublisher limitOrderPublisher;
    @Mock
    private MarketOrderCreatedPublisher marketOrderPublisher;
    @Mock
    private ReservationOrderCreatedPublisher reservationOrderPublisher;
    @Mock
    private LimitOrderCancelledPublisher limitOrderCancelledPublisher;
    @Mock
    private ReservationOrderCancelledPublisher reservationOrderCancelledPublisher;
    @Captor
    private ArgumentCaptor<UserBalance> userBalanceCaptor;

    @Captor
    private ArgumentCaptor<UserBalance> userBalanceCaptor2;

    @Captor
    private ArgumentCaptor<ReservationOrder> reservationOrderCaptor;

    private TradingApplicationService tradingApplicationService;

    private TestMapper testMapper;


    private PredicatedTradeListener listener2;

    private TestConstants testConstants;

    @Captor
    private ArgumentCaptor<Trade> tradeCaptor;


    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        helper = new TradingOrderTestHelper();
        testConstants = new TestConstants();
        OrderBookTestHelper.createOrderBook(); // 완전 초기화
        tradingApplicationService = helper.createTradingApplicationService(orderRepo,
                tradeRecordRepo, orderRedis, marketRepo,
                couponRepo, kafkaPublisher, tradingUserBalanceRepository, userBalancePublisher, bithumbApiPort,
                limitOrderPublisher, marketOrderPublisher, reservationOrderPublisher,
                limitOrderCancelledPublisher,
                reservationOrderCancelledPublisher
        );
//        orderMatchingExecutor = helper.getExecuteUseCase();
        testMapper = new TestMapper();
        listener2 = new PredicatedTradeListenerImpl(helper.getUserBalanceHandler(), kafkaPublisher,
                userBalancePublisher, orderRepo, helper.getFeeRateResolver(), helper.getTradeDomainService(),
                tradeRecordRepo, helper.getOrderDomainService());
    }

    @Disabled
    @Test
    @DisplayName("지정가로 사고, 시장가로 팔 때")
    public void buyLimitAndSellMarketOrder() {
        // given
        Mockito.when(marketRepo.findMarketItemByMarketId(TestConstants.TEST_MARKET_ID))
                .thenReturn(Optional.of(testConstants.MARKET_ITEM));
        LimitOrder limitOrder = testConstants.LIMIT_ORDER_BUY;
        PredicatedTradeKafkaResponse predicatedTradeKafkaResponse = PredicatedTradeKafkaResponse.builder()
                .tradeId(UUID.randomUUID().toString())
                .userId(limitOrder.getUserId().getValue().toString())
                .marketId("KRW-BTC")
                .buyOrderId(limitOrder.getId().getValue())        // 지정가 주문
                .sellOrderId("anonymous-123")                    // 시장가 판매
                .orderPrice("50000")                             // 임의 가격
                .quantity("0.01")                                // 임의 수량
                .createdAt(Instant.now())
                .transactionType(TransactionType.TRADE_BUY)
                .messageType(MessageType.CREATE)
                .buyOrderType(OrderType.LIMIT)
                .sellOrderType(OrderType.MARKET)
                .build();
        // when
        listener2.process(predicatedTradeKafkaResponse);
        // then
    }


    @Disabled
    @Test
    @DisplayName("지정가 주문 매칭 시나리오 테스트 && 매칭 엔진만 분리하여 리스너로 받을 때 기존의 로직과 같은지 확인하는 테스트")
    public void limitOrderBuyTest() {
        // given
        List<LimitOrder> limitOrders = List.of(
                testConstants.limitOrder2Sell(),
                testConstants.limitOrder3Sell(),
                testConstants.limitOrder4Sell()
        );
//        Mockito.when(orderRedis.findLimitOrdersByMarketId(TestConstants.TEST_MARKET_ID))
//                .thenReturn(limitOrders);
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
//        List<TradeCreatedEvent> tradeCreatedEvents = orderMatchingExecutor.executeLimitOrder(limitOrder);


        // then 0.3은 1_020_000.0에 체결되고
        // 0.7이 먼저 남고 이건 1_030_000.0에 체결됨
        Mockito.verify(kafkaPublisher, Mockito.times(2)).publish(Mockito.any());
        Mockito.verify(userBalancePublisher, Mockito.times(1)).publish(Mockito.any());
        Mockito.verify(tradingUserBalanceRepository, Mockito.times(3)).saveUserBalance(Mockito.any());
        Mockito.verify(tradeRecordRepo, Mockito.times(2)).saveTrade(Mockito.any());
        Mockito.verify(orderRepo, Mockito.times(1)).saveLimitOrder(Mockito.any());
        Mockito.verify(tradingUserBalanceRepository, Mockito.times(3)).saveUserBalance(userBalanceCaptor.capture());
        List<UserBalance> capturedUserBalances = userBalanceCaptor.getAllValues();
        Assertions.assertEquals(capturedUserBalances.get(2).getAvailableMoney(), userBalance.getAvailableMoney());
        System.out.println("userBalance.getAvailableMoney() = " + userBalance.getAvailableMoney().getValue());
        Mockito.reset(kafkaPublisher, userBalancePublisher, orderRedis,
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
//        tradeCreatedEvents.forEach(event -> {
//            Trade trade = event.getDomainType(); // <- 이거 안 됨, event 안에 trade를 직접 꺼내야 함
//            Mockito.when(orderRepo.findLimitOrderByOrderId(trade.getBuyOrderId().getValue()))
//                    .thenReturn(Optional.of(limitOrder2));
//            PredicatedTradeKafkaResponse response =
//                    testMapper.reservationOrderToPredicatedTradeKafkaResponse(trade, event.getMessageType(),
//                            limitOrder2.getOrderType(), OrderType.LIMIT);
//            listener2.process(response);
//        });
        // then
        Mockito.verify(tradingUserBalanceRepository, Mockito.times(3)).saveUserBalance(userBalanceCaptor2.capture());
        UserBalance newValues = userBalanceCaptor2.getValue();
        Assertions.assertEquals(newValues.getAvailableMoney().getValue().doubleValue(), BigDecimal.valueOf(1973).doubleValue());
        Mockito.verify(kafkaPublisher, Mockito.times(2)).publish(Mockito.any());
        Mockito.verify(userBalancePublisher, Mockito.times(2)).publish(Mockito.any());
        Mockito.verify(tradingUserBalanceRepository, Mockito.times(3)).saveUserBalance(Mockito.any());
        Mockito.verify(tradeRecordRepo, Mockito.times(2)).saveTrade(Mockito.any());
        Mockito.verify(orderRepo, Mockito.times(2)).saveLimitOrder(Mockito.any());
    }

    @Disabled
    @Test
    @DisplayName("지정가 매도 시나리오 테스트 && 매칭 엔진만 분리하여 리스너로 받을 때 기존의 로직과 같은지 확인하는 테스트")
    public void limitOrderSellTest() {
        // given
        List<LimitOrder> limitOrders = List.of(
                testConstants.limitOrder2Buy(),
                testConstants.limitOrder3Buy(),
                testConstants.limitOrder4Buy()
        );
//        Mockito.when(orderRedis.findLimitOrdersByMarketId(TestConstants.TEST_MARKET_ID))
//                .thenReturn(limitOrders);
        LimitOrder limitOrder = testConstants.LIMIT_ORDER_SELL;
        UserBalance userBalance = testConstants.createUserBalance(limitOrder.getUserId(), BigDecimal.ZERO);
        Mockito.when(tradingUserBalanceRepository.findUserBalanceByUserId(TestConstants.TEST_USER_ID))
                .thenReturn(Optional.of(userBalance));
        Mockito.when(marketRepo.findMarketItemByMarketId(TestConstants.TEST_MARKET_ID))
                .thenReturn(Optional.of(testConstants.MARKET_ITEM));
        // when
//        List<TradeCreatedEvent> tradeCreatedEvents = orderMatchingExecutor.executeLimitOrder(limitOrder);
        // then
        Mockito.verify(tradingUserBalanceRepository, Mockito.times(1)).saveUserBalance(userBalanceCaptor.capture());
        UserBalance balance = userBalanceCaptor.getValue();
        Assertions.assertEquals(BigDecimal.valueOf(1_020_000.0).doubleValue(),
                balance.getAvailableMoney().getValue().doubleValue());
        // given
        Mockito.reset(kafkaPublisher, userBalancePublisher, orderRedis,
                tradingUserBalanceRepository, tradeRecordRepo, orderRepo);
        System.out.println("-".repeat(200));
        testConstants = new TestConstants();
        LimitOrder limitOrder2 = testConstants.LIMIT_ORDER_SELL;
        UserBalance userBalance2 = testConstants.createUserBalance(BigDecimal.ZERO);
        Mockito.when(tradingUserBalanceRepository.findUserBalanceByUserId(limitOrder2.getUserId().getValue()))
                .thenReturn(Optional.of(userBalance2));
        // when
//        tradeCreatedEvents.forEach(tradeCreatedEvent -> {
//            Trade trade = tradeCreatedEvent.getDomainType();
//            Mockito.when(orderRepo.findLimitOrderByOrderId(trade.getSellOrderId().getValue()))
//                    .thenReturn(Optional.of(limitOrder2));
//            PredicatedTradeKafkaResponse response =
//                    testMapper.reservationOrderToPredicatedTradeKafkaResponse(trade,
//                            tradeCreatedEvent.getMessageType(), limitOrder2.getOrderType(), OrderType.LIMIT);
//            listener2.process(response);
//        });
        // then
        Assertions.assertEquals(BigDecimal.valueOf(1_020_000.0).doubleValue(),
                userBalance2.getAvailableMoney().getValue().doubleValue());
    }

    @Disabled
    @Test
    @DisplayName("예약가 구매 주문 매칭 시나리오 테스트 && 매칭 엔진만 분리하여 리스너로 받을 때 기존의 로직과 같은지 확인하는 테스트")
    public void reservationOrderBuyScenarioTest() {
        // given
        List<LimitOrder> limitOrders = List.of(
                testConstants.limitOrder2Sell(),
                testConstants.limitOrder3Sell(),
                testConstants.limitOrder4Sell()
        );
//        Mockito.when(orderRedis.findLimitOrdersByMarketId(TestConstants.TEST_MARKET_ID))
//                .thenReturn(limitOrders);
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
//        List<TradeCreatedEvent> tradeCreatedEvents = orderMatchingExecutor.executeReservationOrder(reservationOrder);
        // then
        Mockito.verify(userBalancePublisher, Mockito.times(1)).publish(Mockito.any());
        // given
        Mockito.reset(kafkaPublisher, userBalancePublisher, orderRedis,
                tradingUserBalanceRepository, tradeRecordRepo, orderRepo);
        System.out.println("-".repeat(200));
        testConstants = new TestConstants();
        ReservationOrder reservationOrder2 = testConstants.RESERVATION_ORDER_BUY;
        UserBalance userBalance2 = testConstants.createUserBalance(BigDecimal.ZERO);
        userBalance2.getLockBalances().add(LockBalance.createLockBalance(reservationOrder2.getId(),
                reservationOrder2.getUserId(),
                Money.of(BigDecimal.valueOf(2_000_000.0)), LockStatus.LOCKED,
                CreatedAt.now()));
        Mockito.when(tradingUserBalanceRepository.findUserBalanceByUserId(reservationOrder2.getUserId().getValue()))
                .thenReturn(Optional.of(userBalance2));
        // when
//        tradeCreatedEvents.forEach(tradeCreatedEvent -> {
//            Trade trade = tradeCreatedEvent.getDomainType();
//            Mockito.when(orderRepo.findReservationOrderByOrderId(trade.getBuyOrderId().getValue()))
//                    .thenReturn(Optional.of(reservationOrder2));
//            PredicatedTradeKafkaResponse response =
//                    testMapper.reservationOrderToPredicatedTradeKafkaResponse(trade,
//                            tradeCreatedEvent.getMessageType(), reservationOrder2.getOrderType(), OrderType.LIMIT);
//            listener2.process(response);
//        });
        // then
        Assertions.assertEquals(OrderStatus.PARTIALLY_FILLED, reservationOrder2.getOrderStatus());
        Assertions.assertEquals(BigDecimal.valueOf(3.1), reservationOrder2.getRemainingQuantity().getValue());
        Mockito.verify(userBalancePublisher, Mockito.times(3)).publish(Mockito.any());
    }

    @Disabled
    @Test
    @DisplayName("예약가 매도 시나리오 테스트 && 매칭 엔진만 분리하여 리스너로 받을 때 기존의 로직과 같은지 확인하는 테스트")
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
//        Mockito.when(orderRedis.findLimitOrdersByMarketId(TestConstants.TEST_MARKET_ID))
//                .thenReturn(limitOrders);
        // 1_000_000.0에 2개 팔고,
        ReservationOrder reservationOrder = testConstants.RESERVATION_ORDER_SELL;
        Mockito.when(marketRepo.findMarketItemByMarketId(TestConstants.TEST_MARKET_ID))
                .thenReturn(Optional.of(testConstants.MARKET_ITEM));
        UserBalance userBalance = testConstants.createUserBalance(BigDecimal.ZERO);
        Mockito.when(tradingUserBalanceRepository.findUserBalanceByUserId(reservationOrder.getUserId().getValue()))
                .thenReturn(Optional.of(userBalance));
        // when
//        List<TradeCreatedEvent> tradeCreatedEvents = orderMatchingExecutor.executeReservationOrder(reservationOrder);
        // then
        Mockito.verify(tradingUserBalanceRepository, Mockito.times(2))
                .saveUserBalance(userBalanceCaptor.capture());
        UserBalance value = userBalanceCaptor.getAllValues().get(1);
        System.out.println("value.getAvailableMoney().getValue() = " + value.getAvailableMoney().getValue());
        Assertions.assertTrue(value.getAvailableMoney().getValue().compareTo(BigDecimal.valueOf(2_024_010.0)) > 0);
        // given
        Mockito.reset(kafkaPublisher, userBalancePublisher, orderRedis,
                tradingUserBalanceRepository, tradeRecordRepo, orderRepo);
        System.out.println("-".repeat(200));
        testConstants = new TestConstants();
        ReservationOrder reservationOrder2 = testConstants.RESERVATION_ORDER_SELL;
        UserBalance userBalance2 = testConstants.createUserBalance(BigDecimal.ZERO);
        Mockito.when(tradingUserBalanceRepository.findUserBalanceByUserId(reservationOrder2.getUserId().getValue()))
                .thenReturn(Optional.of(userBalance2));
        // when
//        tradeCreatedEvents.forEach(tradeCreatedEvent -> {
//            Trade trade = tradeCreatedEvent.getDomainType();
//            Mockito.when(orderRepo.findReservationOrderByOrderId(trade.getSellOrderId().getValue()))
//                    .thenReturn(Optional.of(reservationOrder2));
//            PredicatedTradeKafkaResponse response =
//                    testMapper.reservationOrderToPredicatedTradeKafkaResponse(trade,
//                            tradeCreatedEvent.getMessageType(), OrderType.LIMIT, reservationOrder2.getOrderType());
//            listener2.process(response);
//        });
        // then
        Assertions.assertTrue(userBalance2.getAvailableMoney()
                .getValue().compareTo(BigDecimal.valueOf(2_024_010.0)) > 0);
        Mockito.verify(userBalancePublisher, Mockito.times(2)).publish(Mockito.any());
        Mockito.verify(kafkaPublisher, Mockito.times(2)).publish(Mockito.any());
    }

    @Disabled
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
//        Mockito.when(orderRedis.findLimitOrdersByMarketId(TestConstants.TEST_MARKET_ID))
//                .thenReturn(limitOrders);
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
//        List<TradeCreatedEvent> tradeCreatedEvents = orderMatchingExecutor.executeMarketOrder(marketOrder);
        // then
        Mockito.verify(tradingUserBalanceRepository, Mockito.times(3))
                .saveUserBalance(userBalanceCaptor.capture());
        UserBalance value = userBalanceCaptor.getAllValues().get(2);
        Assertions.assertTrue(value.getLockBalances().isEmpty());
        // given
        Mockito.reset(kafkaPublisher, userBalancePublisher, orderRedis,
                tradingUserBalanceRepository, tradeRecordRepo, orderRepo);
        System.out.println("-".repeat(200));
        testConstants = new TestConstants();
        MarketOrder marketOrder2 = testConstants.MARKET_ORDER_BUY;
        UserBalance userBalance2 = testConstants.createUserBalance(BigDecimal.ZERO);
        userBalance2.getLockBalances().add(LockBalance.createLockBalance(marketOrder2.getId(), marketOrder2.getUserId(),
                Money.of(BigDecimal.valueOf(1_035_000.0)), LockStatus.LOCKED, CreatedAt.now()
        ));
        Mockito.when(tradingUserBalanceRepository.findUserBalanceByUserId(marketOrder2.getUserId().getValue()))
                .thenReturn(Optional.of(userBalance2));
        // when
//        tradeCreatedEvents.forEach(tradeCreatedEvent -> {
//            Trade trade = tradeCreatedEvent.getDomainType();
//            Mockito.when(orderRepo.findMarketOrderByOrderId(trade.getBuyOrderId().getValue()))
//                    .thenReturn(Optional.of(marketOrder2));
//            PredicatedTradeKafkaResponse response =
//                    testMapper.reservationOrderToPredicatedTradeKafkaResponse(trade, tradeCreatedEvent.getMessageType(),
//                            marketOrder2.getOrderType(), OrderType.LIMIT);
//            listener2.process(response);
//        });
        // then
        Assertions.assertTrue(userBalance2.getLockBalances().isEmpty());
        Mockito.verify(userBalancePublisher, Mockito.times(2)).publish(Mockito.any());
        Mockito.verify(kafkaPublisher, Mockito.times(2)).publish(Mockito.any());
    }

    @Disabled("지금은 테스트 안함 -> 매칭 서비스로 분리했기 때문")
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
//        Mockito.when(orderRedis.findLimitOrdersByMarketId(TestConstants.TEST_MARKET_ID))
//                .thenReturn(limitOrders);
        // 1_000_000.0에 2개 팔고,
        Mockito.when(marketRepo.findMarketItemByMarketId(TestConstants.TEST_MARKET_ID))
                .thenReturn(Optional.of(testConstants.MARKET_ITEM));
        UserBalance userBalance = testConstants.createUserBalance(BigDecimal.ZERO);
        Mockito.when(tradingUserBalanceRepository.findUserBalanceByUserId(marketOrder.getUserId().getValue()))
                .thenReturn(Optional.of(userBalance));
        // then
//        List<TradeCreatedEvent> tradeCreatedEvents = orderMatchingExecutor.executeMarketOrder(marketOrder);
        Mockito.verify(tradingUserBalanceRepository, Mockito.times(1)).saveUserBalance(userBalanceCaptor.capture());
        UserBalance value = userBalanceCaptor.getValue();
        Assertions.assertEquals(BigDecimal.valueOf(1_020_000L).doubleValue(),
                value.getAvailableMoney().getValue().doubleValue()
        );
        // given
        Mockito.reset(kafkaPublisher, userBalancePublisher, orderRedis,
                tradingUserBalanceRepository, tradeRecordRepo, orderRepo);
        System.out.println("-".repeat(200));
        testConstants = new TestConstants();
        MarketOrder marketOrder2 = testConstants.MARKET_ORDER_SELL;
        UserBalance userBalance2 = testConstants.createUserBalance(BigDecimal.ZERO);
        Mockito.when(tradingUserBalanceRepository.findUserBalanceByUserId(marketOrder2.getUserId().getValue()))
                .thenReturn(Optional.of(userBalance2));
        // when
//        tradeCreatedEvents.forEach(tradeCreatedEvent -> {
//            Trade trade = tradeCreatedEvent.getDomainType();
//            Mockito.when(orderRepo.findMarketOrderByOrderId(trade.getSellOrderId().getValue()))
//                    .thenReturn(Optional.of(marketOrder2));
//            PredicatedTradeKafkaResponse response =
//                    testMapper.reservationOrderToPredicatedTradeKafkaResponse(trade, tradeCreatedEvent.getMessageType(),
//                            OrderType.LIMIT, marketOrder2.getOrderType());
//            listener2.process(response);
//        });
        // then
        Assertions.assertEquals(BigDecimal.valueOf(1_020_000L).doubleValue(),
                userBalance2.getAvailableMoney().getValue().doubleValue()
        );
    }

    @Disabled
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
}