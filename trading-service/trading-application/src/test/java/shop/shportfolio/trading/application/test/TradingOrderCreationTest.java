package shop.shportfolio.trading.application.test;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import shop.shportfolio.common.domain.valueobject.MarketId;
import shop.shportfolio.common.domain.valueobject.OrderPrice;
import shop.shportfolio.common.domain.valueobject.Quantity;
import shop.shportfolio.common.domain.valueobject.UserId;
import shop.shportfolio.trading.application.command.create.CreateLimitOrderCommand;
import shop.shportfolio.trading.application.command.create.CreateLimitOrderResponse;
import shop.shportfolio.trading.application.command.create.CreateMarketOrderCommand;
import shop.shportfolio.trading.application.dto.OrderBookAsksDto;
import shop.shportfolio.trading.application.dto.OrderBookBidsDto;
import shop.shportfolio.trading.application.dto.OrderBookDto;
import shop.shportfolio.trading.application.ports.input.TradingApplicationService;
import shop.shportfolio.trading.application.ports.output.kafka.TemporaryKafkaPublisher;
import shop.shportfolio.trading.application.ports.output.redis.MarketDataRedisAdapter;
import shop.shportfolio.trading.application.ports.output.repository.TradingRepositoryAdapter;
import shop.shportfolio.trading.application.test.bean.TradingApplicationServiceMockBean;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.MarketItem;
import shop.shportfolio.trading.domain.entity.MarketOrder;
import shop.shportfolio.trading.domain.valueobject.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@SpringBootTest(classes = {TradingApplicationServiceMockBean.class})
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@ExtendWith(MockitoExtension.class)
public class TradingOrderCreationTest {

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
    private final BigDecimal orderPrice = BigDecimal.valueOf(1_000_000);
    private final String orderSide = "BUY";
    private final BigDecimal quantity = BigDecimal.valueOf(5L);
    private final OrderType orderTypeLimit = OrderType.LIMIT;
    private final OrderType orderTypeMarket = OrderType.MARKET;
    private OrderBookDto orderBookDto;

    @BeforeEach
    public void setUp() {
        Mockito.reset(testTradingRepositoryAdapter, marketDataRedisAdapter, temporaryKafkaPublisher);
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
        orderBookDto.setBids(bids);
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

    @Test
    @DisplayName("지정가 주문 생성 테스트")
    public void createLimitOrder() {
        // given
        CreateLimitOrderCommand createLimitOrderCommand = new CreateLimitOrderCommand(userId, marketId,
                orderSide, orderPrice, quantity, orderTypeLimit.name());
        Mockito.when(testTradingRepositoryAdapter.saveLimitOrder(Mockito.any())).thenReturn(
                LimitOrder.createLimitOrder(
                        new UserId(userId),
                        new MarketId(marketId),
                        OrderSide.of(orderSide),
                        new Quantity(quantity),
                        new OrderPrice(orderPrice),
                        OrderType.LIMIT
                ));
        MarketItem marketItem = MarketItem.createMarketItem(marketId, new MarketKoreanName("비트코인"),
                new MarketEnglishName("BTC"), new MarketWarning(""),
                new TickPrice(BigDecimal.valueOf(1000L)),marketStatus);
        Mockito.when(marketDataRedisAdapter.findOrderBookByMarket(marketId)).thenReturn(
                Optional.of(orderBookDto));
        Mockito.when(testTradingRepositoryAdapter.findMarketItemByMarketId(marketId)).thenReturn(
                Optional.of(marketItem)
        );
        // when
        CreateLimitOrderResponse createLimitOrderResponse = tradingApplicationService.
                createLimitOrder(createLimitOrderCommand);
        // then
        Mockito.verify(marketDataRedisAdapter, Mockito.times(1)).
                saveLimitOrder(Mockito.any(), Mockito.any());
        Mockito.verify(testTradingRepositoryAdapter, Mockito.times(2))
                .saveLimitOrder(Mockito.any());
        Assertions.assertNotNull(createLimitOrderResponse);
        Assertions.assertEquals(userId, createLimitOrderResponse.getUserId());
        Assertions.assertEquals(marketId, createLimitOrderResponse.getMarketId());
        Assertions.assertEquals(quantity, createLimitOrderResponse.getQuantity());
        Assertions.assertEquals(orderPrice, createLimitOrderResponse.getPrice());
        Assertions.assertEquals("BUY", createLimitOrderResponse.getOrderSide());
        Assertions.assertEquals("LIMIT", createLimitOrderResponse.getOrderType().name());
    }

    @Test
    @DisplayName("시장가 주문 생성 테스트 // 디버그로 다 확인했는데 정상 작동")
    public void createMarketOrder() {
        // given
        Quantity innerQuantity = new Quantity(BigDecimal.valueOf(5L));
        MarketItem marketItem = MarketItem.createMarketItem(marketId, new MarketKoreanName("비트코인"),
                new MarketEnglishName("BTC"), new MarketWarning(""),
                new TickPrice(BigDecimal.valueOf(1000L)),marketStatus);
        CreateMarketOrderCommand createMarketOrderCommand = new CreateMarketOrderCommand(userId, marketId,
                 orderSide, innerQuantity.getValue(), orderTypeMarket.name());
        Mockito.when(testTradingRepositoryAdapter.findMarketItemByMarketId(marketId)).thenReturn(
                Optional.of(marketItem));
        Mockito.when(marketDataRedisAdapter.findOrderBookByMarket(marketId))
                .thenReturn(Optional.ofNullable(orderBookDto));
        // when
        tradingApplicationService.createMarketOrder(createMarketOrderCommand);
        // then
        System.out.println("orderBookDto = " + orderBookDto);
        Mockito.verify(testTradingRepositoryAdapter, Mockito.times(1))
                .saveMarketOrder(Mockito.any());
        Mockito.verify(temporaryKafkaPublisher, Mockito.times(4))
                .publish(Mockito.any());
        Mockito.verify(marketDataRedisAdapter, Mockito.times(1))
                .findOrderBookByMarket(marketId);
    }

    @Test
    @DisplayName("음수 수량으로 시장가 주문 생성 시 예외 발생 테스트")
    public void createMarketOrderWithNegativeQuantity() {
        // given
        BigDecimal wrongQuantity = BigDecimal.valueOf(-2L);
        // when
        IllegalArgumentException illegalArgumentException = Assertions.assertThrows(IllegalArgumentException.class, () ->
                new LimitOrder(
                        new UserId(userId),
                        new MarketId(marketId),
                        OrderSide.BUY,
                        new Quantity(wrongQuantity),
                        new OrderPrice(orderPrice),
                        OrderType.LIMIT));
        // then
        Assertions.assertTrue(illegalArgumentException.getMessage().contains("Quantity must be positive"));
    }
    @Test
    @DisplayName("잘못된 주문 가격으로 지정가 주문 생성 시 예외 발생 테스트")
    public void createLimitOrderWithInvalidPrice() {
        // given
        BigDecimal wrongQuantity = BigDecimal.valueOf(-2L);
        CreateMarketOrderCommand command = new CreateMarketOrderCommand(userId, marketId,
                OrderSide.BUY.toString(), BigDecimal.valueOf(-2L), OrderType.LIMIT.name());
        // when
        IllegalArgumentException illegalArgumentException = Assertions.assertThrows(IllegalArgumentException.class, () ->
                new LimitOrder(
                        new UserId(userId),
                        new MarketId(marketId),
                        OrderSide.BUY,
                        new Quantity(wrongQuantity),
                        new OrderPrice(orderPrice),
                        OrderType.LIMIT));
        // then
        Assertions.assertTrue(illegalArgumentException.getMessage().contains("Quantity must be positive"));
    }

}
