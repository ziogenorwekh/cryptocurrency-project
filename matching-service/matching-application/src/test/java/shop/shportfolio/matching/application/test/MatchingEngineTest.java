package shop.shportfolio.matching.application.test;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import shop.shportfolio.matching.application.handler.matching.MatchingEngine;
import shop.shportfolio.matching.application.memorystore.OrderMemoryStore;
import shop.shportfolio.matching.application.ports.output.kafka.MatchedKafkaPublisher;
import shop.shportfolio.matching.application.ports.output.socket.BithumbSocketClient;
import shop.shportfolio.matching.application.test.helper.OrderBookTestHelper;
import shop.shportfolio.matching.application.test.helper.TestComponents;
import shop.shportfolio.matching.application.test.helper.TestConstants;
import shop.shportfolio.trading.domain.entity.LimitOrder;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@ExtendWith(MockitoExtension.class)
public class MatchingEngineTest {

    private OrderMemoryStore orderMemoryStore;
    private TestComponents testComponents;
    @Mock
    private BithumbSocketClient bithumbSocketClient;
    @Mock
    private MatchedKafkaPublisher matchedKafkaPublisher;

    private MatchingEngine matchingEngine;

    @BeforeEach
    public void setUp() {
        OrderBookTestHelper.createOrderBook();
        orderMemoryStore = OrderMemoryStore.getInstance();
        testComponents = new TestComponents(bithumbSocketClient, matchedKafkaPublisher);
        matchingEngine = testComponents.getMatchingEngine();
        orderMemoryStore.addLimitOrder(TestConstants.LIMIT_ORDER1_SELL);
        orderMemoryStore.addLimitOrder(TestConstants.LIMIT_ORDER2_SELL);
        orderMemoryStore.addLimitOrder(TestConstants.LIMIT_ORDER3_SELL);
        orderMemoryStore.addLimitOrder(TestConstants.LIMIT_ORDER4_SELL);
        orderMemoryStore.addLimitOrder(TestConstants.LIMIT_ORDER2_BUY);
        orderMemoryStore.addLimitOrder(TestConstants.LIMIT_ORDER3_BUY);
        orderMemoryStore.addLimitOrder(TestConstants.LIMIT_ORDER4_BUY);
    }

    @AfterEach
    public void tearDown() {
        OrderBookTestHelper.clear();
        orderMemoryStore.clear();
    }

    @Test
    @DisplayName("지정가 주문 매수 테스트")
    public void limitOrderBuyTest() {
        // given
        LimitOrder processLimitOrder = TestConstants.LIMIT_ORDER_BUY;
        // when
        matchingEngine.executeLimitOrder(processLimitOrder);
        // then
        Mockito.verify(matchedKafkaPublisher, Mockito.times(3)).publish(Mockito.any());
    }

    @Test
    @DisplayName("지정가 주문 매도 테스트")
    public void limitOrderSellTest() {
        // given
        LimitOrder processLimitOrder = TestConstants.LIMIT_ORDER_SELL;
        // when
        matchingEngine.executeLimitOrder(processLimitOrder);
        // then
        Mockito.verify(matchedKafkaPublisher, Mockito.times(1)).publish(Mockito.any());
    }
}
