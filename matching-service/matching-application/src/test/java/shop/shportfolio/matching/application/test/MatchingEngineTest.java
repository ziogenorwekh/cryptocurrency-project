package shop.shportfolio.matching.application.test;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.matching.application.dto.orderbook.OrderBookAsksBithumbDto;
import shop.shportfolio.matching.application.dto.orderbook.OrderBookBidsBithumbDto;
import shop.shportfolio.matching.application.dto.orderbook.OrderBookBithumbDto;
import shop.shportfolio.matching.application.handler.OrderBookManager;
import shop.shportfolio.matching.application.handler.matching.MatchingEngine;
import shop.shportfolio.matching.application.memorystore.ExternalOrderBookMemoryStore;
import shop.shportfolio.matching.application.memorystore.OrderMemoryStore;
import shop.shportfolio.matching.application.ports.output.kafka.MatchedPublisher;
import shop.shportfolio.matching.application.ports.output.socket.BithumbSocketClient;
import shop.shportfolio.matching.application.ports.output.socket.OrderBookSender;
import shop.shportfolio.matching.application.test.helper.OrderBookTestHelper;
import shop.shportfolio.matching.application.test.helper.TestComponents;
import shop.shportfolio.matching.domain.entity.MatchingOrderBook;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.MarketOrder;
import shop.shportfolio.trading.domain.entity.ReservationOrder;
import shop.shportfolio.trading.domain.valueobject.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@ExtendWith(MockitoExtension.class)
public class MatchingEngineTest {

    private ExternalOrderBookMemoryStore externalOrderBookMemoryStore;
    private OrderMemoryStore orderMemoryStore;
    private TestComponents testComponents;
    @Mock
    private BithumbSocketClient bithumbSocketClient;
    @Mock
    private MatchedPublisher matchedPublisher;

    @Mock
    private OrderBookSender orderBookSender;

    private MatchingEngine matchingEngine;

    private OrderBookManager orderBookManager;

    private static LimitOrder createLimitOrder(UUID userId, String marketId, OrderSide side, double quantity, double price) {
        return new LimitOrder(OrderId.anonymous(), new UserId(userId), new MarketId(marketId), side, Quantity.of(BigDecimal.valueOf(quantity)), Quantity.of(BigDecimal.valueOf(quantity)), OrderPrice.of(BigDecimal.valueOf(price)), OrderType.LIMIT, CreatedAt.now(), OrderStatus.OPEN);
    }

    private static MarketOrder createMarketOrder(UUID userId, String marketId, OrderSide side, double price) {
        return MarketOrder.createMarketOrder(new UserId(userId), new MarketId(marketId), side,
                Quantity.of(BigDecimal.valueOf(3L)),
                OrderPrice.of(BigDecimal.valueOf(price)), OrderType.MARKET);
    }

    private static ReservationOrder createReservationOrder(UUID userId, String marketId, OrderSide side, double quantity, TriggerType triggerType, double triggerPrice) {
        return ReservationOrder.createReservationOrder(new UserId(userId), new MarketId(marketId), side,
                Quantity.of(BigDecimal.valueOf(quantity)), OrderType.RESERVATION,
                TriggerCondition.of(triggerType, OrderPrice.of(BigDecimal.valueOf(triggerPrice))),
                new ScheduledTime(LocalDateTime.now(ZoneOffset.UTC).minusMinutes(1)),
                new ExpireAt(LocalDateTime.now().plusMonths(1)), IsRepeatable.of(true));
    }


    @BeforeEach
    public void setUp() {
        externalOrderBookMemoryStore = new ExternalOrderBookMemoryStore();
        orderMemoryStore = new OrderMemoryStore();

        // 기존 테스트 데이터를 매번 새로 생성해서 추가
        orderMemoryStore.addLimitOrder(createLimitOrder(UUID.randomUUID(), "KRW-BTC", OrderSide.SELL, 1.0, 1_000_000.0));
        orderMemoryStore.addLimitOrder(createLimitOrder(UUID.randomUUID(), "KRW-BTC", OrderSide.SELL, 1.2, 1_030_000.0));
        orderMemoryStore.addLimitOrder(createLimitOrder(UUID.randomUUID(), "KRW-BTC", OrderSide.SELL, 0.3, 1_020_000.0));
        orderMemoryStore.addLimitOrder(createLimitOrder(UUID.randomUUID(), "KRW-BTC", OrderSide.SELL, 0.4, 1_030_000.0));
        orderMemoryStore.addLimitOrder(createLimitOrder(UUID.randomUUID(), "KRW-BTC", OrderSide.BUY, 1.2, 1_000_000.0));
        orderMemoryStore.addLimitOrder(createLimitOrder(UUID.randomUUID(), "KRW-BTC", OrderSide.BUY, 1.5, 1_010_000.0));
        orderMemoryStore.addLimitOrder(createLimitOrder(UUID.randomUUID(), "KRW-BTC", OrderSide.BUY, 1.3, 1_020_000.0));

        OrderBookTestHelper.createOrderBook(externalOrderBookMemoryStore);
        testComponents = new TestComponents(bithumbSocketClient, matchedPublisher,
                externalOrderBookMemoryStore, orderMemoryStore, orderBookSender);
        matchingEngine = testComponents.getMatchingEngine();
        orderBookManager = testComponents.getOrderBookManager();
    }

    @AfterEach
    public void tearDown() {
        orderMemoryStore.clear();
        externalOrderBookMemoryStore.clear();
    }

    @Test
    @DisplayName("지정가 주문 매수 테스트")
    public void limitOrderBuyTest() {
        // given
        LimitOrder processLimitOrder = createLimitOrder(UUID.randomUUID(), "KRW-BTC",
                OrderSide.BUY, 2.0, 1_050_000.0);
        // when
        matchingEngine.executeLimitOrder(processLimitOrder);
        // then
        Mockito.verify(matchedPublisher, Mockito.times(3)).publish(Mockito.any());
    }

    @Test
    @DisplayName("지정가 주문 매도 테스트")
    public void limitOrderSellTest() {
        // given
        LimitOrder processLimitOrder = createLimitOrder(UUID.randomUUID(), "KRW-BTC", OrderSide.SELL, 1.0, 1_000_000.0);
        // when
        matchingEngine.executeLimitOrder(processLimitOrder);
        // then
        Mockito.verify(matchedPublisher, Mockito.times(1)).publish(Mockito.any());
    }

    @Test
    @DisplayName("시장가 주문 매수 테스트")
    public void marketOrderBuyTest() {
        // given
        MarketOrder marketOrderBuy = createMarketOrder(UUID.randomUUID(), "KRW-BTC", OrderSide.BUY, 1_030_000);
        // when
        matchingEngine.executeMarketOrder(marketOrderBuy);
        // then
        Mockito.verify(matchedPublisher, Mockito.times(2)).publish(Mockito.any());
    }

    @Test
    @DisplayName("시장가 주문 매도 테스트")
    public void marketOrderSellTest() {
        // given
        MarketOrder marketOrderSell = createMarketOrder(UUID.randomUUID(), "KRW-BTC", OrderSide.SELL, 1_020_000);
        // when
        matchingEngine.executeMarketOrder(marketOrderSell);
        // then
        Mockito.verify(matchedPublisher, Mockito.times(3)).publish(Mockito.any());
    }

    @Test
    @DisplayName("예약가 주문 매수 테스트")
    public void reservationBuyTest() {
        // given
        ReservationOrder reservationOrder = createReservationOrder(UUID.randomUUID(), "KRW-BTC", OrderSide.BUY, 1, TriggerType.BELOW, 1_030_000.0);
        // when
        matchingEngine.executeReservationOrder(reservationOrder);
        // then
        Mockito.verify(matchedPublisher, Mockito.times(1)).publish(Mockito.any());
    }

    @Test
    @DisplayName("예약가 주문 매도 테스트")
    public void reservationSellTest() {
        // given
        ReservationOrder reservationOrder = createReservationOrder(UUID.randomUUID(), "KRW-BTC", OrderSide.SELL, 2, TriggerType.ABOVE, 1_000_000.0);
        // when
        matchingEngine.executeReservationOrder(reservationOrder);
        // then
        Mockito.verify(matchedPublisher, Mockito.times(2)).publish(Mockito.any());
    }

    @Test
    @DisplayName("외부 OrderBook DTO를 OrderBookManager에 적재 테스트")
    public void orderBookManagerIntegrationTest() {
        OrderBookBithumbDto dto = new OrderBookBithumbDto(
                "KRW-BTC", System.currentTimeMillis(), 10.0, 12.0,
                List.of(new OrderBookAsksBithumbDto(1_030_000.0, 0.5)),
                List.of(new OrderBookBidsBithumbDto(1_020_000.0, 0.3))
        );

        orderBookManager.onOrderBookReceived(dto);
        Assertions.assertNotNull(externalOrderBookMemoryStore.getOrderBook("KRW-BTC"));
        Assertions.assertEquals(3, externalOrderBookMemoryStore.getOrderBook("KRW-BTC")
                .getSellPriceLevels().size());
        Assertions.assertEquals(3, externalOrderBookMemoryStore.getOrderBook("KRW-BTC")
                .getBuyPriceLevels().size());
    }

    @Test
    @DisplayName("LimitOrder가 없는 상태에서 loadAdjustedOrderBook 호출 시 정상 동작")
    public void loadAdjustedOrderBookEmptyLimitOrderTest() {
        externalOrderBookMemoryStore.clear();
        orderMemoryStore.clear();
        OrderBookBithumbDto dto = new OrderBookBithumbDto(
                "KRW-BTC", System.currentTimeMillis(), 10.0, 12.0,
                List.of(new OrderBookAsksBithumbDto(1_030_000.0, 0.5)),
                List.of(new OrderBookBidsBithumbDto(1_020_000.0, 0.3))
        );
        orderBookManager.onOrderBookReceived(dto);

        MatchingOrderBook adjustedBook = orderBookManager.loadAdjustedOrderBook("KRW-BTC");
        Assertions.assertEquals(1, adjustedBook.getSellPriceLevels().size());
        Assertions.assertEquals(1, adjustedBook.getBuyPriceLevels().size());
    }

    @Test
    @DisplayName("LimitOrder가 존재하면 loadAdjustedOrderBook에서 OrderBook에 붙는지 테스트")
    public void loadAdjustedOrderBookWithLimitOrderTest() {
        LimitOrder limitOrder = createLimitOrder(UUID.randomUUID(), "KRW-BTC", OrderSide.BUY, 1.0, 1_025_000.0);
        orderMemoryStore.addLimitOrder(limitOrder);

        OrderBookBithumbDto dto = new OrderBookBithumbDto(
                "KRW-BTC", System.currentTimeMillis(), 10.0, 12.0,
                List.of(new OrderBookAsksBithumbDto(1_030_000.0, 0.5)),
                List.of(new OrderBookBidsBithumbDto(1_020_000.0, 0.3))
        );
        orderBookManager.onOrderBookReceived(dto);

        MatchingOrderBook adjustedBook = orderBookManager.loadAdjustedOrderBook("KRW-BTC");
        Assertions.assertTrue(adjustedBook.getBuyPriceLevels().values().stream()
                .flatMap(level -> level.getOrders().stream())
                .anyMatch(order -> order.getId().getValue().equals(limitOrder.getId().getValue())));
    }

    @Test
    @DisplayName("LimitOrder 중복 제거 후 loadAdjustedOrderBook 테스트")
    public void loadAdjustedOrderBookNoDuplicateLimitOrderTest() {
        LimitOrder limitOrder = createLimitOrder(UUID.randomUUID(), "KRW-BTC", OrderSide.BUY, 1.0, 1_020_000.0);
        orderMemoryStore.addLimitOrder(limitOrder);
        orderMemoryStore.addLimitOrder(limitOrder); // 의도적 중복 추가

        OrderBookBithumbDto dto = new OrderBookBithumbDto(
                "KRW-BTC", System.currentTimeMillis(), 10.0, 12.0,
                List.of(new OrderBookAsksBithumbDto(1_030_000.0, 0.5)),
                List.of(new OrderBookBidsBithumbDto(1_020_000.0, 0.3))
        );
        orderBookManager.onOrderBookReceived(dto);

        MatchingOrderBook adjustedBook = orderBookManager.loadAdjustedOrderBook("KRW-BTC");

        long count = adjustedBook.getBuyPriceLevels().values().stream()
                .flatMap(level -> level.getOrders().stream())
                .filter(o -> o.getId().getValue().equals(limitOrder.getId().getValue()))
                .count();

        Assertions.assertEquals(1, count, "중복된 LimitOrder가 OrderBook에 두 번 붙으면 안됨");
    }

    @Test
    @DisplayName("MarketOrder 잔량 남기고 일부 체결 테스트")
    public void marketOrderPartialFillTest() {
        orderMemoryStore.clear();
        MarketOrder marketOrder = createMarketOrder(UUID.randomUUID(), "KRW-BTC", OrderSide.BUY, 1_030_000.0);

        // 일부 체결을 위해서 Sell LimitOrder 하나만 추가
        orderMemoryStore.addLimitOrder(createLimitOrder(UUID.randomUUID(), "KRW-BTC", OrderSide.SELL, 0.5, 1_030_000.0));

        matchingEngine.executeMarketOrder(marketOrder);

        Mockito.verify(matchedPublisher, Mockito.times(2)).publish(Mockito.any());
    }

    @Test
    @DisplayName("ReservationOrder 트리거 조건 미충족 시 미체결")
    public void reservationOrderNotTriggeredTest() {
        ReservationOrder reservationOrder = createReservationOrder(UUID.randomUUID(), "KRW-BTC", OrderSide.BUY, 1,
                TriggerType.ABOVE, 1_200_000.0); // 현재 호가보다 높음

        matchingEngine.executeReservationOrder(reservationOrder);

        Assertions.assertFalse(reservationOrder.isFilled(), "트리거 조건 미충족 시 체결되면 안됨");
        Mockito.verify(matchedPublisher, Mockito.never()).publish(Mockito.any());
    }

    @Test
    @DisplayName("MarketOrder 일부 체결 후 잔여 금액과 상태 확인")
    public void marketOrderPartialExecutionTest() {
        MarketOrder marketOrder = MarketOrder.createMarketOrder(
                new UserId(UUID.randomUUID()),
                new MarketId("KRW-BTC"),
                OrderSide.BUY,
                null,
                OrderPrice.of(BigDecimal.valueOf(2_000_000)),
                OrderType.MARKET
                );

        // 체결 금액 일부만 적용
        Quantity executedQty = marketOrder.applyTrade(OrderPrice.of(new BigDecimal(1_000_000)),
                Quantity.of(BigDecimal.valueOf(1.0)));

        Assertions.assertEquals(Quantity.of(BigDecimal.valueOf(1.0)), executedQty);
        Assertions.assertEquals(OrderStatus.PARTIALLY_FILLED, marketOrder.getOrderStatus());
        Assertions.assertEquals(OrderPrice.of(BigDecimal.valueOf(1_000_000)).getValue().doubleValue(),
                marketOrder.getRemainingPrice().getValue().doubleValue());
    }

    @Test
    @DisplayName("MarketOrder 전액 체결 후 상태 FILLED 확인")
    public void marketOrderFullExecutionTest() {
        MarketOrder marketOrder = MarketOrder.createMarketOrder(
                new UserId(UUID.randomUUID()),
                new MarketId("KRW-BTC"),
                OrderSide.BUY,
                null,
                OrderPrice.of(BigDecimal.valueOf(1_000_000)),
                OrderType.MARKET
        );

        marketOrder.applyTrade(OrderPrice.of(new BigDecimal(1_000_000)), Quantity.of(BigDecimal.valueOf(1.0)));

        Assertions.assertEquals(OrderStatus.FILLED, marketOrder.getOrderStatus());
        Assertions.assertEquals(BigDecimal.ZERO.doubleValue(), marketOrder.getRemainingPrice().getValue().doubleValue());
    }

    @Test
    @DisplayName("ReservationOrder 트리거 미충족 시 canExecute false 확인")
    public void reservationOrderCannotExecuteTest() {
        ReservationOrder reservationOrder = ReservationOrder.createReservationOrder(
                new UserId(UUID.randomUUID()),
                new MarketId("KRW-BTC"),
                OrderSide.BUY,
                Quantity.of(BigDecimal.valueOf(1.0)),
                OrderType.RESERVATION,
                TriggerCondition.of(TriggerType.ABOVE, OrderPrice.of(BigDecimal.valueOf(1_050_000))),
                new ScheduledTime(LocalDateTime.now().plusMinutes(1)),
                new ExpireAt(LocalDateTime.now().plusDays(1)),
                IsRepeatable.of(true)
        );

        boolean executable = reservationOrder.canExecute(OrderPrice.of(BigDecimal.valueOf(1_000_000)), LocalDateTime.now());
        Assertions.assertFalse(executable);
    }

    @Test
    @DisplayName("ReservationOrder 트리거 충족 시 canExecute true 확인")
    public void reservationOrderCanExecuteTest() {
        ReservationOrder reservationOrder = ReservationOrder.createReservationOrder(
                new UserId(UUID.randomUUID()),
                new MarketId("KRW-BTC"),
                OrderSide.BUY,
                Quantity.of(BigDecimal.valueOf(1.0)),
                OrderType.RESERVATION,
                TriggerCondition.of(TriggerType.BELOW, OrderPrice.of(BigDecimal.valueOf(1_050_000))),
                new ScheduledTime(LocalDateTime.now().minusMinutes(1)),
                new ExpireAt(LocalDateTime.now().plusDays(1)),
                IsRepeatable.of(true)
        );

        boolean executable = reservationOrder.canExecute(OrderPrice.of(BigDecimal.valueOf(1_000_000)), LocalDateTime.now());
        Assertions.assertTrue(executable);
    }

    @Test
    @DisplayName("ReservationOrder 만료 시 isExpired true 확인")
    public void reservationOrderExpiredTest() {
        ReservationOrder reservationOrder = ReservationOrder.createReservationOrder(
                new UserId(UUID.randomUUID()),
                new MarketId("KRW-BTC"),
                OrderSide.BUY,
                Quantity.of(BigDecimal.valueOf(1.0)),
                OrderType.RESERVATION,
                TriggerCondition.of(TriggerType.BELOW, OrderPrice.of(BigDecimal.valueOf(1_000_000))),
                new ScheduledTime(LocalDateTime.now().minusDays(2)),
                new ExpireAt(LocalDateTime.now().minusDays(1)),
                IsRepeatable.of(false)
        );

        Assertions.assertTrue(reservationOrder.isExpired(LocalDateTime.now()));
    }

    @Test
    @DisplayName("ReservationOrder 트리거 ABOVE 미충족 시 미체결")
    public void reservationOrderAboveNotTriggeredTest() {
        ReservationOrder reservationOrder = createReservationOrder(UUID.randomUUID(), "KRW-BTC", OrderSide.BUY, 1,
                TriggerType.ABOVE, 1_200_000.0); // 현재 최고 매도호가보다 높음

        matchingEngine.executeReservationOrder(reservationOrder);

        Assertions.assertFalse(reservationOrder.isFilled(), "트리거 조건 미충족 시 체결되면 안됨");
        Mockito.verify(matchedPublisher, Mockito.never()).publish(Mockito.any());
    }

    @Test
    @DisplayName("ReservationOrder 트리거 BELOW 미충족 시 미체결")
    public void reservationOrderBelowNotTriggeredTest() {
        ReservationOrder reservationOrder = createReservationOrder(UUID.randomUUID(), "KRW-BTC", OrderSide.SELL, 1,
                TriggerType.BELOW, 890_000.0); // 현재 최저 매수호가보다 낮음

        matchingEngine.executeReservationOrder(reservationOrder);

        Assertions.assertFalse(reservationOrder.isFilled(), "트리거 조건 미충족 시 체결되면 안됨");
        Mockito.verify(matchedPublisher, Mockito.never()).publish(Mockito.any());
    }

    @Test
    @DisplayName("ReservationOrder 트리거 ABOVE 충족 시 체결")
    public void reservationOrderAboveTriggeredTest() {
        ReservationOrder reservationOrder = createReservationOrder(UUID.randomUUID(), "KRW-BTC", OrderSide.SELL, 1,
                TriggerType.ABOVE, 1_000_000.0); // 현재 최고 매수호가보다 낮음 → 충족

        matchingEngine.executeReservationOrder(reservationOrder);

        Assertions.assertTrue(reservationOrder.isFilled(), "트리거 조건 충족 시 체결되어야 함");
        Mockito.verify(matchedPublisher, Mockito.atLeastOnce()).publish(Mockito.any());
    }

    @Test
    @DisplayName("ReservationOrder 트리거 BELOW 충족 시 체결")
    public void reservationOrderBelowTriggeredTest() {
        ReservationOrder reservationOrder = createReservationOrder(UUID.randomUUID(), "KRW-BTC", OrderSide.BUY, 1,
                TriggerType.BELOW, 1_050_000.0); // 현재 최저 매도호가보다 높음 → 충족

        matchingEngine.executeReservationOrder(reservationOrder);

        Assertions.assertTrue(reservationOrder.isFilled(), "트리거 조건 충족 시 체결되어야 함");
        Mockito.verify(matchedPublisher, Mockito.atLeastOnce()).publish(Mockito.any());
    }

    @Test
    @DisplayName("ReservationOrder 만료 시 체결 불가")
    public void reservationOrderExpiredCannotExecuteTest() {
        ReservationOrder reservationOrder = ReservationOrder.createReservationOrder(
                new UserId(UUID.randomUUID()),
                new MarketId("KRW-BTC"),
                OrderSide.BUY,
                Quantity.of(BigDecimal.valueOf(1.0)),
                OrderType.RESERVATION,
                TriggerCondition.of(TriggerType.BELOW, OrderPrice.of(BigDecimal.valueOf(1_000_000))),
                new ScheduledTime(LocalDateTime.now().minusDays(2)),
                new ExpireAt(LocalDateTime.now().minusDays(1)),
                IsRepeatable.of(false)
        );

        boolean executable = reservationOrder.canExecute(OrderPrice.of(BigDecimal.valueOf(900_000)), LocalDateTime.now());
        Assertions.assertFalse(executable, "만료된 예약 주문은 실행할 수 없어야 함");
    }

    @Test
    @DisplayName("ReservationOrder 반복 설정 시 shouldRepeat true 확인")
    public void reservationOrderRepeatableTest() {
        ReservationOrder reservationOrder = createReservationOrder(UUID.randomUUID(), "KRW-BTC", OrderSide.BUY, 1,
                TriggerType.BELOW, 950_000.0);
        Assertions.assertTrue(reservationOrder.shouldRepeat(), "반복 설정된 예약 주문은 shouldRepeat true여야 함");
    }

    @Test
    @DisplayName("ReservationOrder 스케줄 미도달 시 실행 불가")
    public void reservationOrderScheduledFutureTest() {
        ReservationOrder reservationOrder = ReservationOrder.createReservationOrder(
                new UserId(UUID.randomUUID()),
                new MarketId("KRW-BTC"),
                OrderSide.BUY,
                Quantity.of(BigDecimal.valueOf(1.0)),
                OrderType.RESERVATION,
                TriggerCondition.of(TriggerType.BELOW, OrderPrice.of(BigDecimal.valueOf(1_050_000))),
                new ScheduledTime(LocalDateTime.now().plusMinutes(10)), // 미래 시각
                new ExpireAt(LocalDateTime.now().plusDays(1)),
                IsRepeatable.of(true)
        );

        boolean executable = reservationOrder.canExecute(OrderPrice.of(BigDecimal.valueOf(1_000_000)), LocalDateTime.now());
        Assertions.assertFalse(executable, "스케줄 시각이 미래이면 실행 불가");
    }

    @Test
    @DisplayName("ReservationOrder 트리거 가격 경계값 테스트 ABOVE")
    public void reservationOrderAboveBoundaryTest() {
        ReservationOrder reservationOrder = createReservationOrder(UUID.randomUUID(), "KRW-BTC", OrderSide.SELL, 1,
                TriggerType.ABOVE, 1_020_000.0); // 최고 매수호가와 동일

        matchingEngine.executeReservationOrder(reservationOrder);

        Assertions.assertTrue(reservationOrder.isFilled(), "트리거 가격이 경계값이면 체결되어야 함");
    }

    @Test
    @DisplayName("ReservationOrder 트리거 가격 경계값 테스트 BELOW")
    public void reservationOrderBelowBoundaryTest() {
        ReservationOrder reservationOrder = createReservationOrder(UUID.randomUUID(), "KRW-BTC", OrderSide.BUY, 1,
                TriggerType.BELOW, 1_020_000.0); // 최저 매도호가와 동일

        matchingEngine.executeReservationOrder(reservationOrder);

        Assertions.assertTrue(reservationOrder.isFilled(), "트리거 가격이 경계값이면 체결되어야 함");
    }

    @Test
    @DisplayName("ReservationOrder 반복 실행 후 상태 유지 확인")
    public void reservationOrderRepeatExecutionTest() {
        ReservationOrder reservationOrder = ReservationOrder.createReservationOrder(
                new UserId(UUID.randomUUID()),
                new MarketId("KRW-BTC"),
                OrderSide.BUY,
                Quantity.of(BigDecimal.valueOf(1)),
                OrderType.RESERVATION,
                TriggerCondition.of(TriggerType.BELOW, OrderPrice.of(BigDecimal.valueOf(1_050_000))),
                new ScheduledTime(LocalDateTime.now().minusMinutes(1)),
                new ExpireAt(LocalDateTime.now().plusDays(1)),
                IsRepeatable.of(true) // 생성 시 지정
        );

        matchingEngine.executeReservationOrder(reservationOrder);

        Assertions.assertTrue(reservationOrder.shouldRepeat(), "반복 주문은 체결 후에도 반복 상태 유지");
    }

    @Test
    @DisplayName("ReservationOrder 만료 후 반복 설정 시 상태 확인")
    public void reservationOrderExpiredRepeatTest() {
        ReservationOrder reservationOrder = ReservationOrder.createReservationOrder(
                new UserId(UUID.randomUUID()),
                new MarketId("KRW-BTC"),
                OrderSide.BUY,
                Quantity.of(BigDecimal.valueOf(1.0)),
                OrderType.RESERVATION,
                TriggerCondition.of(TriggerType.BELOW, OrderPrice.of(BigDecimal.valueOf(1_000_000))),
                new ScheduledTime(LocalDateTime.now().minusDays(2)),
                new ExpireAt(LocalDateTime.now().minusDays(1)),
                IsRepeatable.of(true)
        );

        Assertions.assertTrue(reservationOrder.isExpired(LocalDateTime.now()), "만료 상태 확인");
        Assertions.assertTrue(reservationOrder.shouldRepeat(), "만료되더라도 반복 설정은 유지됨");
    }

    @Test
    @DisplayName("OrderBook 업데이트 후 예약주문 canExecute 반영 테스트")
    public void reservationOrderAfterOrderBookUpdateTest() {
        ReservationOrder reservationOrder = ReservationOrder.createReservationOrder(
                new UserId(UUID.randomUUID()),
                new MarketId("KRW-BTC"),
                OrderSide.BUY,
                Quantity.of(BigDecimal.valueOf(1)),
                OrderType.RESERVATION,
                TriggerCondition.of(TriggerType.BELOW, OrderPrice.of(BigDecimal.valueOf(1_020_000))),
                new ScheduledTime(LocalDateTime.now().minusMinutes(1)),
                new ExpireAt(LocalDateTime.now().plusDays(1)),
                IsRepeatable.of(true)
        );

        OrderBookBithumbDto dto = new OrderBookBithumbDto(
                "KRW-BTC", System.currentTimeMillis(), 5.0, 3.0,
                List.of(new OrderBookAsksBithumbDto(1_030_000.0, 0.5)),
                List.of(new OrderBookBidsBithumbDto(1_020_000.0, 0.3))
        );
        orderBookManager.onOrderBookReceived(dto);

        MatchingOrderBook book = externalOrderBookMemoryStore.getOrderBook("KRW-BTC");
        // 매수 기준 최고가
        BigDecimal bestBuyPrice = getBestBuyPrice(book);

        boolean executable = reservationOrder.canExecute(OrderPrice.of(bestBuyPrice), LocalDateTime.now());
        Assertions.assertTrue(executable, "OrderBook 최신가 반영 후 트리거 조건 판단");
    }

    public BigDecimal getBestBuyPrice(MatchingOrderBook book) {
        if (book == null || book.getBuyPriceLevels().isEmpty()) return BigDecimal.ZERO;
        return book.getBuyPriceLevels().keySet().stream()
                .map(TickPrice::getValue)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
    }

}
