package shop.shportfolio.matching.application.test;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.matching.application.handler.matching.MatchingEngine;
import shop.shportfolio.matching.application.memorystore.ExternalOrderBookMemoryStore;
import shop.shportfolio.matching.application.memorystore.OrderMemoryStore;
import shop.shportfolio.matching.application.ports.output.kafka.MatchedKafkaPublisher;
import shop.shportfolio.matching.application.ports.output.socket.BithumbSocketClient;
import shop.shportfolio.matching.application.test.helper.OrderBookTestHelper;
import shop.shportfolio.matching.application.test.helper.TestComponents;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.MarketOrder;
import shop.shportfolio.trading.domain.entity.ReservationOrder;
import shop.shportfolio.trading.domain.valueobject.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
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
    private MatchedKafkaPublisher matchedKafkaPublisher;

    private MatchingEngine matchingEngine;

    private static LimitOrder createLimitOrder(UUID userId, String marketId, OrderSide side, double quantity, double price) {
        return new LimitOrder(OrderId.anonymous(), new UserId(userId), new MarketId(marketId), side, Quantity.of(BigDecimal.valueOf(quantity)), Quantity.of(BigDecimal.valueOf(quantity)), OrderPrice.of(BigDecimal.valueOf(price)), OrderType.LIMIT, CreatedAt.now(), OrderStatus.OPEN);
    }
    private static MarketOrder createMarketOrder(UUID userId, String marketId, OrderSide side, double price) {
        return MarketOrder.createMarketOrder(new UserId(userId), new MarketId(marketId), side, OrderPrice.of(BigDecimal.valueOf(price)), OrderType.MARKET);
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
        testComponents = new TestComponents(bithumbSocketClient, matchedKafkaPublisher,
                externalOrderBookMemoryStore, orderMemoryStore);
        matchingEngine = testComponents.getMatchingEngine();
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
        LimitOrder processLimitOrder = createLimitOrder(UUID.randomUUID(), "KRW-BTC", OrderSide.BUY, 2.0, 1_050_000.0);
        // when
        matchingEngine.executeLimitOrder(processLimitOrder);
        // then
        Mockito.verify(matchedKafkaPublisher, Mockito.times(3)).publish(Mockito.any());
    }

    @Test
    @DisplayName("지정가 주문 매도 테스트")
    public void limitOrderSellTest() {
        // given
        LimitOrder processLimitOrder = createLimitOrder(UUID.randomUUID(), "KRW-BTC", OrderSide.SELL, 1.0, 1_000_000.0);
        // when
        matchingEngine.executeLimitOrder(processLimitOrder);
        // then
        Mockito.verify(matchedKafkaPublisher, Mockito.times(1)).publish(Mockito.any());
    }

    @Test
    @DisplayName("시장가 주문 매수 테스트")
    public void marketOrderBuyTest() {
        // given
        MarketOrder marketOrderBuy = createMarketOrder(UUID.randomUUID(), "KRW-BTC", OrderSide.BUY, 1_030_000);
        // when
        matchingEngine.executeMarketOrder(marketOrderBuy);
        // then
        Mockito.verify(matchedKafkaPublisher, Mockito.times(2)).publish(Mockito.any());
    }

    @Test
    @DisplayName("시장가 주문 매도 테스트")
    public void marketOrderSellTest() {
        // given
        MarketOrder marketOrderSell = createMarketOrder(UUID.randomUUID(), "KRW-BTC", OrderSide.SELL, 1_020_000);
        // when
        matchingEngine.executeMarketOrder(marketOrderSell);
        // then
        Mockito.verify(matchedKafkaPublisher, Mockito.times(1)).publish(Mockito.any());
    }

    @Test
    @DisplayName("예약가 주문 매수 테스트")
    public void reservationBuyTest() {
        // given
        ReservationOrder reservationOrder = createReservationOrder(UUID.randomUUID(), "KRW-BTC", OrderSide.BUY, 1, TriggerType.BELOW, 1_030_000.0);
        // when
        matchingEngine.executeReservationOrder(reservationOrder);
        // then
        Mockito.verify(matchedKafkaPublisher, Mockito.times(1)).publish(Mockito.any());
    }

    @Test
    @DisplayName("예약가 주문 매도 테스트")
    public void reservationSellTest() {
        // given
        ReservationOrder reservationOrder = createReservationOrder(UUID.randomUUID(), "KRW-BTC", OrderSide.SELL, 2, TriggerType.ABOVE, 1_000_000.0);
        // when
        matchingEngine.executeReservationOrder(reservationOrder);
        // then
        Mockito.verify(matchedKafkaPublisher, Mockito.times(2)).publish(Mockito.any());
    }
}
