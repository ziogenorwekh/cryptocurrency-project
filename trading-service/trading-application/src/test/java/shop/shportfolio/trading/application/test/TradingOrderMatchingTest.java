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
import shop.shportfolio.trading.application.command.create.CreateMarketOrderCommand;
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
import shop.shportfolio.trading.domain.TradingDomainService;
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
public class TradingOrderMatchingTest {


    @Autowired
    private TradingApplicationService tradingApplicationService;

    @Autowired
    private TradingRepositoryAdapter testTradingRepositoryAdapter;

    @Autowired
    private MarketDataRedisAdapter marketDataRedisAdapter;

    @Autowired
    private TemporaryKafkaPublisher temporaryKafkaPublisher;

    private final UUID userId = UUID.randomUUID();
    private String marketId = "BTC-KRW";
    private BigDecimal marketItemTick = BigDecimal.valueOf(100_000);
    private final BigDecimal orderPrice = BigDecimal.valueOf(1_000_000);
    private final String orderSide = "BUY";
    private final BigDecimal quantity = BigDecimal.valueOf(5L);
    private final OrderType orderTypeLimit = OrderType.LIMIT;
    private final OrderType orderTypeMarket = OrderType.MARKET;
    private OrderBookDto orderBookDto;
    private OrderBookDto copyOrderBookDto;
    private LimitOrder normalLimitOrder;
    @Autowired
    private TradingDomainService tradingDomainService;

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
        copyOrderBookDto = orderBookDto;

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
    @DisplayName("시장가 체결할건데 마찬가지로, 내 거래소에 거래 내역이 있으면 그것도 반영되어야 하는 테스트")
    public void createMarketOrderWithOurExchangeHavingTradeHistoryTest() {
        // given
        Quantity innerQuantity = new Quantity(BigDecimal.valueOf(5L));
        MarketItem marketItem = MarketItem.createMarketItem(marketId, new MarketKoreanName("비트코인"),
                new MarketEnglishName("BTC"), new MarketWarning(""),
                new TickPrice(BigDecimal.valueOf(1000L)));
        CreateMarketOrderCommand createMarketOrderCommand = new CreateMarketOrderCommand(userId, marketId,
                marketItemTick, orderSide, innerQuantity.getValue(), orderTypeMarket.name());
        MarketOrder marketOrder = MarketOrder.createMarketOrder(
                new UserId(userId),
                new MarketId(marketId),
                OrderSide.of(orderSide),
                innerQuantity,
                OrderType.MARKET);
        Mockito.when(testTradingRepositoryAdapter.findMarketItemByMarketId(marketId)).thenReturn(
                Optional.of(marketItem));
        Mockito.when(testTradingRepositoryAdapter.saveMarketOrder(Mockito.any())).thenReturn(
                marketOrder
        );
        Mockito.when(marketDataRedisAdapter.findOrderBookByMarket(marketId))
                .thenReturn(Optional.ofNullable(orderBookDto));
        tradingApplicationService.createMarketOrder(createMarketOrderCommand);
        // when
        OrderBookTrackResponse orderBook = tradingApplicationService.
                findOrderBook(new OrderBookTrackQuery(createMarketOrderCommand.getMarketId()));

        // then orderBook은 거래한 4개의 트레이드 때문에 5개의 수량이 줄어있어야 한다.
        double totalAskSize = orderBook.getOrderBookAsksResponse().stream()
                .mapToDouble(dto -> Double.parseDouble(dto.getQuantity()))
                .sum();

        Assertions.assertEquals(19.0, totalAskSize);
    }

    @Test
    @DisplayName("주문 수량이 호가 총합과 딱 일치하는 경우 체결 테스트")
    public void createMarketOrderExactQuantityMatch() {
        // given
        normalLimitOrder = LimitOrder.createLimitOrder(
                new UserId(userId),
                new MarketId(marketId),
                OrderSide.BUY,
                new Quantity(BigDecimal.valueOf(1.0)),
                new OrderPrice(BigDecimal.valueOf(1_050_000.0)),
                OrderType.LIMIT);
        // when
        tradingDomainService.applyOrder(normalLimitOrder, new Quantity(BigDecimal.valueOf(1.0)));
        // then
        Assertions.assertEquals(BigDecimal.valueOf(0.0), normalLimitOrder.getRemainingQuantity().getValue());
        Assertions.assertEquals(OrderStatus.FILLED, normalLimitOrder.getOrderStatus());
    }

    @Test
    @DisplayName("주문 수량이 호가 총합보다 초과하는 경우 처리 테스트")
    public void createMarketOrderExceedQuantity() {

    }
    @Test
    @DisplayName("동시 다중 주문 생성 시 잔량 및 체결 처리 테스트")
    public void createMultipleOrdersConcurrently() {

    }

    @Test
    @DisplayName("매칭 후 트레이드 내역 생성 및 호가 잔량 감소 검증 테스트")
    public void tradeMatchingAndOrderBookUpdate() {

    }

    @Test
    @DisplayName("초대형 주문 가격과 수량 처리 테스트")
    public void handleLargeOrderPriceAndQuantity() {

    }

    @Test
    @DisplayName("주문 생성 시 틱 단위 미준수로 인한 예외 발생 테스트")
    public void createOrderWithInvalidTickPrice() {}

    @Test
    @DisplayName("주문 생성 시 지원하지 않는 마켓 ID 입력 시 예외 발생 테스트")
    public void createOrderWithInvalidMarketId() {}

    @Test
    @DisplayName("시장가 매수 주문 시 호가 부족으로 부분 체결 후 잔량 처리 테스트")
    public void createMarketOrderWithPartialMatchDueToInsufficientAsks() {}

    @Test
    @DisplayName("시장가 매도 주문 시 호가 부족으로 부분 체결 후 잔량 처리 테스트")
    public void createMarketSellOrderWithPartialMatchDueToInsufficientBids() {}

    @Test
    @DisplayName("동일 사용자의 연속 주문 시 FIFO 순서 보장 테스트")
    public void orderExecutionOrderShouldBeFIFOForSameUser() {}

    @Test
    @DisplayName("마켓이 중단된 상태에서 주문 시도 시 예외 발생 테스트")
    public void createOrderWhenMarketIsPaused() {}

}
