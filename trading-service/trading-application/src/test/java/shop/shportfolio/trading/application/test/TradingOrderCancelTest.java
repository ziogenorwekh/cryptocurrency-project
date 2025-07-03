package shop.shportfolio.trading.application.test;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import shop.shportfolio.trading.application.command.create.CreateLimitOrderCommand;
import shop.shportfolio.trading.application.command.create.CreateLimitOrderResponse;
import shop.shportfolio.trading.application.command.track.OrderBookTrackQuery;
import shop.shportfolio.trading.application.command.track.OrderBookTrackResponse;
import shop.shportfolio.trading.application.dto.OrderBookAsksDto;
import shop.shportfolio.trading.application.dto.OrderBookBidsDto;
import shop.shportfolio.trading.application.dto.OrderBookDto;
import shop.shportfolio.trading.application.ports.input.TradingApplicationService;
import shop.shportfolio.trading.application.ports.output.kafka.TemporaryKafkaPublisher;
import shop.shportfolio.trading.application.ports.output.redis.MarketDataRedisAdapter;
import shop.shportfolio.trading.application.ports.output.repository.TradingRepositoryAdapter;
import shop.shportfolio.trading.application.test.bean.TradingApplicationServiceMockBean;
import shop.shportfolio.trading.domain.valueobject.MarketStatus;
import shop.shportfolio.trading.domain.valueobject.OrderSide;
import shop.shportfolio.trading.domain.valueobject.OrderType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@SpringBootTest(classes = {TradingApplicationServiceMockBean.class})
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@ExtendWith(MockitoExtension.class)
public class TradingOrderCancelTest {

    @Autowired
    private TradingApplicationService tradingApplicationService;

    @Autowired
    private TradingRepositoryAdapter testTradingRepositoryAdapter;

    @Autowired
    private MarketDataRedisAdapter marketDataRedisAdapter;

    @Autowired
    private TemporaryKafkaPublisher temporaryKafkaPublisher;

    private final MarketStatus marketStatus = MarketStatus.ACTIVE;
    private final UUID userId = UUID.randomUUID();
    private final String marketId = "BTC-KRW";
    private OrderBookDto orderBookDto;

    @BeforeEach
    public void setUp() {
        orderBookDto = new OrderBookDto();
        orderBookDto.setMarket(marketId);
        orderBookDto.setTimestamp(System.currentTimeMillis());
        orderBookDto.setTotalAskSize(5.0);
        orderBookDto.setTotalBidSize(3.0);

        // 매도 호가 리스트 (가격 상승 순으로)
        List<OrderBookAsksDto> asks = List.of(
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
        orderBookDto.setAsks(asks);

        // 매수 호가 리스트 (가격 하락 순으로)
        List<OrderBookBidsDto> bids = List.of(
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
        orderBookDto.setBids(bids);
    }

    @Test
    @DisplayName("주문 취소 시 해당 주문이 정상적으로 삭제되고 잔량 복구 테스트")
    public void cancelLimitOrderAndRestoreOrderBook() {
        // given
        CreateLimitOrderCommand createLimitOrderCommand =
                new CreateLimitOrderCommand(userId, marketId, OrderSide.SELL.getValue(),
                        BigDecimal.valueOf(1_000_000.0), BigDecimal.ONE, OrderType.LIMIT.name());
        Mockito.when(marketDataRedisAdapter.findOrderBookByMarket(marketId))
                .thenReturn(Optional.of(orderBookDto));
        // when
        CreateLimitOrderResponse limitOrder = tradingApplicationService.createLimitOrder(createLimitOrderCommand);
        // then 이거 Mockito 다시 재대로 설정해서 테스트해야 됌
        Assertions.assertNotNull(limitOrder);
        Assertions.assertEquals(limitOrder.getOrderSide(), OrderSide.SELL.getValue());
        Assertions.assertEquals(limitOrder.getQuantity(),BigDecimal.valueOf(1_000_000.0));

        // when
        OrderBookTrackResponse orderBook = tradingApplicationService
                .findOrderBook(new OrderBookTrackQuery(marketId));
        // then



    }



    // 편의 메서드
    private OrderBookAsksDto createAsk(Double price, Double size) {
        OrderBookAsksDto ask = new OrderBookAsksDto();
        ask.setAskPrice(price);
        ask.setAskSize(size);
        return ask;
    }

    private OrderBookBidsDto createBid(Double price, Double size) {
        OrderBookBidsDto bid = new OrderBookBidsDto();
        bid.setBidPrice(price);
        bid.setBidSize(size);
        return bid;
    }

}
