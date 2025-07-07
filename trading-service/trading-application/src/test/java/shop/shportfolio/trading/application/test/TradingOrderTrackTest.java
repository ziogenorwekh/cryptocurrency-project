package shop.shportfolio.trading.application.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import shop.shportfolio.common.domain.valueobject.MarketId;
import shop.shportfolio.common.domain.valueobject.OrderPrice;
import shop.shportfolio.common.domain.valueobject.Quantity;
import shop.shportfolio.common.domain.valueobject.UserId;
import shop.shportfolio.trading.application.command.track.LimitOrderTrackQuery;
import shop.shportfolio.trading.application.command.track.LimitOrderTrackResponse;
import shop.shportfolio.trading.application.exception.OrderNotFoundException;
import shop.shportfolio.trading.application.ports.input.TradingApplicationService;
import shop.shportfolio.trading.application.ports.output.repository.TradingOrderRepositoryPort;
import shop.shportfolio.trading.application.test.bean.TradingApplicationServiceMockBean;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.valueobject.OrderSide;
import shop.shportfolio.trading.domain.valueobject.OrderType;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;


@SpringBootTest(classes = {TradingApplicationServiceMockBean.class})
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@ExtendWith(MockitoExtension.class)
public class TradingOrderTrackTest {

    private final UUID userId = UUID.randomUUID();
    private final String marketId = "BTC-KRW";

    @Autowired
    private TradingApplicationService tradingApplicationService;

    @Autowired
    private TradingOrderRepositoryPort tradingOrderRepositoryPort;

    private final LimitOrder limitOrder = LimitOrder.createLimitOrder(
            new UserId(userId),
            new MarketId(marketId),
            OrderSide.BUY,
            new Quantity(BigDecimal.valueOf(1.0)),
            new OrderPrice(BigDecimal.valueOf(1_050_000.0)),
            OrderType.LIMIT);

    @Test
    @DisplayName("오더 아이디로 주문 조회 테스트")
    public void cancelNonExistingOrderThrowsException() {
        // given
        LimitOrderTrackQuery limitOrderTrackQuery = new LimitOrderTrackQuery(limitOrder.getId().getValue());
        Mockito.when(tradingOrderRepositoryPort.findLimitOrderByOrderId(limitOrder.getId().getValue()))
                .thenReturn(Optional.of(limitOrder));
        // when
        LimitOrderTrackResponse track = tradingApplicationService.findLimitOrderTrackByOrderId(limitOrderTrackQuery);
        // then
        Assertions.assertNotNull(track);
        Assertions.assertEquals(track.getOrderPrice(), limitOrder.getOrderPrice().getValue());
        Assertions.assertEquals(track.getOrderStatus(), limitOrder.getOrderStatus());
        Assertions.assertEquals(track.getUserId(), userId);
        Assertions.assertEquals(track.getMarketId(), marketId);
    }

    @Test
    @DisplayName("존재하지 않는 주문 ID로 주문 취소 시 예외 처리 테스트")
    public void trackOrderButNotFoundThrowsException() {
        // given
        LimitOrderTrackQuery limitOrderTrackQuery = new LimitOrderTrackQuery("anonymous");
        // when
        OrderNotFoundException orderNotFoundException = Assertions.assertThrows(OrderNotFoundException.class, () ->
                tradingApplicationService.findLimitOrderTrackByOrderId(limitOrderTrackQuery));
        // then
        Assertions.assertNotNull(orderNotFoundException);
        Assertions.assertEquals("Order with id " +
                "anonymous" + " not found", orderNotFoundException.getMessage());
    }

}
