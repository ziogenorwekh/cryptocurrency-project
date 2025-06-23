package shop.shportfolio.trading.application.test;


import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import shop.shportfolio.common.domain.valueobject.MarketId;
import shop.shportfolio.common.domain.valueobject.UserId;
import shop.shportfolio.trading.application.command.create.CreateLimitOrderCommand;
import shop.shportfolio.trading.application.command.create.CreateLimitOrderResponse;
import shop.shportfolio.trading.application.ports.input.TradingApplicationService;
import shop.shportfolio.trading.application.ports.output.repository.TradingRepositoryAdapter;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.valueobject.*;

import java.math.BigDecimal;
import java.util.UUID;
@SpringBootTest(classes = {TradingApplicationServiceMockBean.class})
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@ExtendWith(MockitoExtension.class)
public class TradingApplicationServiceCreateTest {


    @Autowired
    private TradingApplicationService tradingApplicationService;

    @Autowired
    private TradingRepositoryAdapter testTradingRepositoryAdapter;
    private final UUID userId = UUID.randomUUID();
    private String marketId = "BTC-KRW";
    private BigDecimal marketItemTick = BigDecimal.valueOf(100_000);
    private final BigDecimal orderPrice = BigDecimal.valueOf(1_000_000);
    private final String orderSide = "BUY";
    private final BigDecimal quantity = BigDecimal.ONE;
    private final OrderType orderType = OrderType.LIMIT;

    @BeforeEach
    public void setUp() {

    }

    @Test
    @DisplayName("지정가 주문 생성 테스트")
    public void createLimitOrder() {
        // given
        CreateLimitOrderCommand createLimitOrderCommand = new CreateLimitOrderCommand(userId, marketId, marketItemTick, orderPrice
                , orderSide, quantity, orderType.name());
        Mockito.when(testTradingRepositoryAdapter.save(Mockito.any())).thenReturn(
                LimitOrder.createLimitOrder(
                        new UserId(userId),
                        new MarketId(marketId),
                        OrderSide.of(orderSide),
                        new Quantity(quantity),
                        new OrderPrice(orderPrice),
                        OrderType.LIMIT
                ));
        // when
        CreateLimitOrderResponse createLimitOrderResponse = tradingApplicationService.
                createLimitOrder(createLimitOrderCommand);
        // then
        Mockito.verify(testTradingRepositoryAdapter, Mockito.times(1)).save(Mockito.any());
        Assertions.assertNotNull(createLimitOrderResponse);
        Assertions.assertEquals(userId, createLimitOrderResponse.getUserId());
        Assertions.assertEquals(marketId, createLimitOrderResponse.getMarketId());
        Assertions.assertEquals(quantity, createLimitOrderResponse.getQuantity());
        Assertions.assertEquals(orderPrice, createLimitOrderResponse.getPrice());
        Assertions.assertEquals("BUY", createLimitOrderResponse.getOrderSide());
        Assertions.assertEquals("LIMIT", createLimitOrderResponse.getOrderType().name());
    }

    @Test
    @DisplayName("시장가 주문 생성 테스트")
    public void createMarketOrder() {
        // given

        // when

        // then
    }
}
