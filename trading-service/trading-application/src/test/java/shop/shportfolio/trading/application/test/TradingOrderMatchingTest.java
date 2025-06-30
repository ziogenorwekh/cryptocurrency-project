package shop.shportfolio.trading.application.test;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.trading.application.command.create.CreateMarketOrderCommand;
import shop.shportfolio.trading.application.command.track.OrderBookTrackQuery;
import shop.shportfolio.trading.application.command.track.OrderBookTrackResponse;
import shop.shportfolio.trading.application.dto.OrderBookAsksDto;
import shop.shportfolio.trading.application.dto.OrderBookBidsDto;
import shop.shportfolio.trading.application.dto.OrderBookDto;
import shop.shportfolio.trading.application.mapper.TradingDtoMapper;
import shop.shportfolio.trading.application.ports.input.TradingApplicationService;
import shop.shportfolio.trading.application.ports.output.kafka.TemporaryKafkaPublisher;
import shop.shportfolio.trading.application.ports.output.redis.MarketDataRedisAdapter;
import shop.shportfolio.trading.application.ports.output.repository.TradingRepositoryAdapter;
import shop.shportfolio.trading.application.test.bean.TradingApplicationServiceMockBean;
import shop.shportfolio.trading.domain.TradingDomainService;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.MarketItem;
import shop.shportfolio.trading.domain.entity.MarketOrder;
import shop.shportfolio.trading.domain.entity.Trade;
import shop.shportfolio.trading.domain.valueobject.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

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

    @Autowired
    private TradingDtoMapper tradingDtoMapper;

    private final UUID userId = UUID.randomUUID();
    private String marketId = "BTC-KRW";
    private final String orderSide = "BUY";
    private final OrderType orderTypeMarket = OrderType.MARKET;
    private OrderBookDto orderBookDto;
    private LimitOrder normalLimitOrder;
    @Autowired
    private TradingDomainService tradingDomainService;
    List<Trade> trades = new ArrayList<>();

    @BeforeEach
    public void setUp() {

        Mockito.reset(testTradingRepositoryAdapter, marketDataRedisAdapter, temporaryKafkaPublisher);
        trades.add(new Trade(new TradeId(UUID.randomUUID()),
                new UserId(userId),
                OrderId.anonymous(),
                OrderId.anonymous(),
                new OrderPrice(BigDecimal.valueOf(990_010.0)),
                new CreatedAt(LocalDateTime.now().minusMinutes(1L)),
                new Quantity(BigDecimal.valueOf(1.0)),
                TransactionType.TRADE_BUY
        ));
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
                orderSide, innerQuantity.getValue(), orderTypeMarket.name());
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
        // when
        tradingApplicationService.createMarketOrder(createMarketOrderCommand);
        // then
        Mockito.verify(temporaryKafkaPublisher, Mockito.times(4)).publish(Mockito.any());
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
        // given
        CreateMarketOrderCommand createMarketOrderCommand =
                new CreateMarketOrderCommand(userId, marketId, OrderSide.BUY.toString(),
                        BigDecimal.valueOf(100.0), OrderType.MARKET.name());
        MarketItem marketItem = MarketItem.createMarketItem(marketId, new MarketKoreanName("비트코인"),
                new MarketEnglishName("BTC"), new MarketWarning(""),
                new TickPrice(BigDecimal.valueOf(1000L)));
        MarketOrder marketOrder = MarketOrder.createMarketOrder(
                new UserId(userId),
                new MarketId(marketId),
                OrderSide.BUY,
                new Quantity(BigDecimal.valueOf(100.0)),
                OrderType.MARKET);
        Mockito.when(testTradingRepositoryAdapter.findMarketItemByMarketId(marketId)).thenReturn(
                Optional.of(marketItem));
        Mockito.when(testTradingRepositoryAdapter.saveMarketOrder(Mockito.any())).thenReturn(
                marketOrder
        );
        Mockito.when(marketDataRedisAdapter.findOrderBookByMarket(marketId))
                .thenReturn(Optional.ofNullable(orderBookDto));
        // when
        tradingApplicationService.createMarketOrder(createMarketOrderCommand);
        // then
        Assertions.assertEquals(BigDecimal.valueOf(81.0), marketOrder.getRemainingQuantity().getValue());
        Assertions.assertEquals(OrderStatus.CANCELED, marketOrder.getOrderStatus());
        Mockito.verify(temporaryKafkaPublisher, Mockito.times(10))
                .publish(Mockito.any());
    }

    @Test
    @DisplayName("동시 다중 주문 생성 시 잔량 및 체결 처리 테스트")
    public void createMultipleOrdersConcurrently() {
        // given

        // when

        // then
    }

    @Test
    @DisplayName("매칭 후 트레이드 내역 생성 및 호가 잔량 감소 검증 테스트")
    public void tradeMatchingAndOrderBookUpdate() {
        // given

        MarketItem marketItem = MarketItem.createMarketItem(marketId, new MarketKoreanName("비트코인"),
                new MarketEnglishName("BTC"), new MarketWarning(""),
                new TickPrice(BigDecimal.valueOf(1000L)));
        Mockito.when(testTradingRepositoryAdapter.findMarketItemByMarketId(marketId))
                .thenReturn(Optional.of(marketItem));
        Mockito.when(testTradingRepositoryAdapter.findTradesByMarketId(marketId)).thenReturn(trades);
        Mockito.when(marketDataRedisAdapter.findOrderBookByMarket(marketId))
                .thenReturn(Optional.ofNullable(orderBookDto));
        // when
        OrderBookTrackResponse orderBook = tradingApplicationService.
                findOrderBook(new OrderBookTrackQuery(marketId));
        // then
        double size = orderBook.getOrderBookAsksResponse().stream()
                .mapToDouble(c -> Double.parseDouble(c.getQuantity()))
                .sum();
        Assertions.assertEquals(18, size);
    }

    @Test
    @DisplayName("초대형 주문 가격과 수량 처리 테스트")
    public void handleLargeOrderPriceAndQuantity() {
        // given

        // when

        // then
    }

    @Test
    @DisplayName("주문 생성 시 틱 단위 미준수로 인한 예외 발생 테스트")
    public void createOrderWithInvalidTickPrice() {
        // given

        // when

        // then
    }

    @Test
    @DisplayName("주문 생성 시 지원하지 않는 마켓 ID 입력 시 예외 발생 테스트")
    public void createOrderWithInvalidMarketId() {
        // given

        // when

        // then
    }

    @Test
    @DisplayName("시장가 매수 주문 시 호가 부족으로 부분 체결 후 잔량 처리 테스트")
    public void createMarketOrderWithPartialMatchDueToInsufficientAsks() {
        // given

        // when

        // then
    }


    @Test
    @DisplayName("시장가 매도 주문 시 호가 부족으로 부분 체결 후 잔량 처리 테스트")
    public void createMarketSellOrderWithPartialMatchDueToInsufficientBids() {
        // given

        // when

        // then
    }

    @Test
    @DisplayName("동일 사용자의 연속 주문 시 FIFO 순서 보장 테스트")
    public void orderExecutionOrderShouldBeFIFOForSameUser() {
        // given

        // when

        // then
    }


    @Test
    @DisplayName("마켓이 중단된 상태에서 주문 시도 시 예외 발생 테스트")
    public void createOrderWhenMarketIsPaused() {
        // given

        // when

        // then
    }

}
