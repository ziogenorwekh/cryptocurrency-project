package shop.shportfolio.trading.application.test;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.trading.application.command.create.CreateLimitOrderCommand;
import shop.shportfolio.trading.application.command.create.CreateLimitOrderResponse;
import shop.shportfolio.trading.application.command.create.CreateMarketOrderCommand;
import shop.shportfolio.trading.application.command.track.OrderBookTrackQuery;
import shop.shportfolio.trading.application.command.track.OrderBookTrackResponse;
import shop.shportfolio.trading.application.dto.OrderBookAsksDto;
import shop.shportfolio.trading.application.dto.OrderBookBidsDto;
import shop.shportfolio.trading.application.dto.OrderBookDto;
import shop.shportfolio.trading.application.exception.MarketItemNotFoundException;
import shop.shportfolio.trading.application.exception.MarketPausedException;
import shop.shportfolio.trading.application.handler.OrderBookLimitMatchingEngine;
import shop.shportfolio.trading.application.mapper.TradingDtoMapper;
import shop.shportfolio.trading.application.ports.input.TradingApplicationService;
import shop.shportfolio.trading.application.ports.output.kafka.TemporaryKafkaPublisher;
import shop.shportfolio.trading.application.ports.output.redis.MarketDataRedisAdapter;
import shop.shportfolio.trading.application.ports.output.repository.TradingRepositoryPort;
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
    private TradingRepositoryPort testTradingRepositoryPort;

    @Autowired
    private MarketDataRedisAdapter marketDataRedisAdapter;

    @Autowired
    private TemporaryKafkaPublisher temporaryKafkaPublisher;

    @Autowired
    private TradingDtoMapper tradingDtoMapper;

    @Autowired
    private TradingDomainService tradingDomainService;

    private final UUID userId = UUID.randomUUID();
    private final String marketId = "BTC-KRW";
    private final String orderSide = "BUY";
    private final OrderType orderTypeMarket = OrderType.MARKET;
    private OrderBookDto orderBookDto;
    private LimitOrder normalLimitOrder;
    private final MarketStatus marketStatus = MarketStatus.ACTIVE;
    private final MarketItem marketItem = MarketItem.createMarketItem(marketId, new MarketKoreanName("비트코인"),
            new MarketEnglishName("BTC"), new MarketWarning(""),
            new TickPrice(BigDecimal.valueOf(1000L)), marketStatus);
    private final BigDecimal orderPrice = BigDecimal.valueOf(1_050_000.0);
    private final BigDecimal quantity = BigDecimal.valueOf(2.2);

    @Autowired
    private OrderBookLimitMatchingEngine orderBookLimitMatchingEngine;
    List<Trade> trades = new ArrayList<>();

    @BeforeEach
    public void setUp() {

        Mockito.reset(testTradingRepositoryPort, marketDataRedisAdapter, temporaryKafkaPublisher);
        trades.add(new Trade(new TradeId(UUID.randomUUID()),
                new UserId(userId),
                OrderId.anonymous(),
                OrderId.anonymous(),
                new OrderPrice(BigDecimal.valueOf(1_050_200.0)),
                new CreatedAt(LocalDateTime.now().plusMinutes(1L)),
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
        CreateMarketOrderCommand createMarketOrderCommand = new CreateMarketOrderCommand(userId, marketId,
                orderSide, innerQuantity.getValue(), orderTypeMarket.name());
        MarketOrder marketOrder = MarketOrder.createMarketOrder(
                new UserId(userId),
                new MarketId(marketId),
                OrderSide.of(orderSide),
                innerQuantity,
                OrderType.MARKET);
        Mockito.when(testTradingRepositoryPort.findMarketItemByMarketId(marketId)).thenReturn(
                Optional.of(marketItem));
        Mockito.when(testTradingRepositoryPort.saveMarketOrder(Mockito.any())).thenReturn(
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
        Mockito.when(testTradingRepositoryPort.findMarketItemByMarketId(marketId)).thenReturn(
                Optional.of(marketItem));
        Mockito.when(marketDataRedisAdapter.findOrderBookByMarket(marketId))
                .thenReturn(Optional.ofNullable(orderBookDto));
        // when
        tradingApplicationService.createMarketOrder(createMarketOrderCommand);
        // then
        Mockito.verify(testTradingRepositoryPort, Mockito.times(1)).saveMarketOrder(Mockito.any());
        Mockito.verify(temporaryKafkaPublisher, Mockito.times(10))
                .publish(Mockito.any());
    }

    @Test
    @DisplayName("매칭 후 트레이드 내역 생성 및 호가 잔량 감소 검증 테스트")
    public void tradeMatchingAndOrderBookUpdate() {
        // given
        Mockito.when(testTradingRepositoryPort.findMarketItemByMarketId(marketId))
                .thenReturn(Optional.of(marketItem));
        Mockito.when(testTradingRepositoryPort.findTradesByMarketId(marketId)).thenReturn(trades);
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
    @DisplayName("지정가 주문 생성 테스트하는데, 바로 매칭을 시도하는지 확인하는 테스트, 그러나 완전히 해당 주문가를 소화하지 못한 경우")
    public void createLimitOrderAndImmediatelyMatchingAndPartialFilled() {
        // given
        CreateLimitOrderCommand createLimitOrderCommand = new CreateLimitOrderCommand(userId, marketId,
                orderSide, orderPrice, quantity, OrderType.LIMIT.name());
        Mockito.when(testTradingRepositoryPort.saveLimitOrder(Mockito.any())).thenReturn(
                LimitOrder.createLimitOrder(
                        new UserId(userId),
                        new MarketId(marketId),
                        OrderSide.of(orderSide),
                        new Quantity(quantity),
                        new OrderPrice(orderPrice),
                        OrderType.LIMIT
                ));
        Mockito.when(marketDataRedisAdapter.findOrderBookByMarket(marketId))
                .thenReturn(Optional.ofNullable(orderBookDto));
        Mockito.when(testTradingRepositoryPort.findMarketItemByMarketId(marketId))
                .thenReturn(Optional.of(marketItem));
        // when
        CreateLimitOrderResponse createLimitOrderResponse = tradingApplicationService.
                createLimitOrder(createLimitOrderCommand);
        // then
        Mockito.verify(marketDataRedisAdapter, Mockito.times(1))
                .saveLimitOrder(Mockito.any(), Mockito.any());
        Mockito.verify(temporaryKafkaPublisher, Mockito.times(1)).publish(Mockito.any());
        Assertions.assertEquals(createLimitOrderResponse.getUserId(), userId);
        Mockito.verify(testTradingRepositoryPort, Mockito.times(2)).
                saveLimitOrder(Mockito.any());
    }

    @Test
    @DisplayName("지정가 주문 생성 테스트하는데, 바로 매칭을 시도하는지 확인하는 테스트, 완전히 해당 주문가를 소화한 경우")
    public void createLimitOrderAndImmediatelyMatchingAndFilled() {
        // given
        Quantity smallQuantity = new Quantity(BigDecimal.valueOf(0.5));
        CreateLimitOrderCommand createLimitOrderCommand = new CreateLimitOrderCommand(userId, marketId,
                orderSide, orderPrice, smallQuantity.getValue(), OrderType.LIMIT.name());
        Mockito.when(testTradingRepositoryPort.saveLimitOrder(Mockito.any())).thenReturn(
                LimitOrder.createLimitOrder(
                        new UserId(userId),
                        new MarketId(marketId),
                        OrderSide.of(orderSide),
                        smallQuantity,
                        new OrderPrice(orderPrice),
                        OrderType.LIMIT
                ));
        Mockito.when(marketDataRedisAdapter.findOrderBookByMarket(marketId))
                .thenReturn(Optional.ofNullable(orderBookDto));
        Mockito.when(testTradingRepositoryPort.findMarketItemByMarketId(marketId))
                .thenReturn(Optional.of(marketItem));
        // when
        CreateLimitOrderResponse limitOrder =
                tradingApplicationService.createLimitOrder(createLimitOrderCommand);
        // then
        Assertions.assertNotNull(limitOrder);
        Mockito.verify(temporaryKafkaPublisher, Mockito.times(1)).publish(Mockito.any());
        // 레디스에서 지워지는것까지

        Mockito.verify(marketDataRedisAdapter, Mockito.times(1))
                .deleteLimitOrder(Mockito.any());
        Mockito.verify(testTradingRepositoryPort, Mockito.times(2)).
                saveLimitOrder(Mockito.any());
    }

    @Test
    @DisplayName("주문 생성 시 지원하지 않는 마켓 ID 입력 시 예외 발생 테스트")
    public void createOrderWithInvalidMarketId() {
        // given
        CreateLimitOrderCommand createLimitOrderCommand = new CreateLimitOrderCommand(userId, "anonymous",
                OrderSide.BUY.getValue(),
                BigDecimal.valueOf(10_500_500), BigDecimal.ONE, OrderType.LIMIT.name());
        // when
        MarketItemNotFoundException marketItemNotFoundException = Assertions.assertThrows(
                MarketItemNotFoundException.class, () -> {
                    tradingApplicationService.createLimitOrder(createLimitOrderCommand);
                });
        // then
        Assertions.assertNotNull(marketItemNotFoundException);
        Assertions.assertEquals("marketId not found", marketItemNotFoundException.getMessage());
    }

    @Test
    @DisplayName("시장가 매도 주문 시 호가 부족으로 부분 체결 후 잔량 처리 테스트")
    public void createMarketSellOrderWithPartialMatchDueToInsufficientBids() {
        // given
        CreateMarketOrderCommand createMarketOrderCommand = new CreateMarketOrderCommand(userId, marketId
                , OrderSide.SELL.getValue(), BigDecimal.valueOf(1000L), OrderType.MARKET.name());
        Mockito.when(testTradingRepositoryPort.findMarketItemByMarketId(marketId)).thenReturn(
                Optional.of(marketItem));
        Mockito.when(marketDataRedisAdapter.findOrderBookByMarket(marketId))
                .thenReturn(Optional.ofNullable(orderBookDto));
        // when
        tradingApplicationService.createMarketOrder(createMarketOrderCommand);
        // then
        Mockito.verify(marketDataRedisAdapter, Mockito.times(1)).findOrderBookByMarket(marketId);
        Mockito.verify(testTradingRepositoryPort, Mockito.times(1))
                .saveMarketOrder(Mockito.any());
    }

//    다중 스레드 혹은 비동기 환경에서 주문 요청 처리
//    주문 잔량(remaining quantity) 정확성 유지
//    체결 내역이 올바르게 생성되고, 중복 혹은 누락 없는지
//    동시성 문제(예: race condition, deadlock) 여부
    @Test
    @DisplayName("동시 다중 주문 생성 시 잔량 및 체결 처리 테스트")
    public void createMultipleOrdersConcurrently() {
        // given

        // when

        // then
    }

//    매우 큰 수량과 높은 가격으로 주문 생성 명령 생성
//    주문 생성 및 매칭 호출
//    주문 잔량 및 체결 결과 검증
//    예외 발생 시 테스트 실패 처리
    @Test
    @DisplayName("초대형 주문 가격과 수량 처리 테스트")
    public void handleLargeOrderPriceAndQuantity() {
        // given

        // when

        // then
    }

    @Test
    @DisplayName("동일 사용자의 연속 주문 시 FIFO 순서 보장 테스트")
    public void orderExecutionOrderShouldBeFIFOForSameUser() {
        // given
        UUID sameUserId = this.userId;
        CreateMarketOrderCommand order1 = new CreateMarketOrderCommand(
                sameUserId, marketId, OrderSide.BUY.getValue(), BigDecimal.valueOf(1.2), OrderType.MARKET.name());
        CreateMarketOrderCommand order2 = new CreateMarketOrderCommand(
                sameUserId, marketId, OrderSide.BUY.getValue(), BigDecimal.valueOf(1.4), OrderType.MARKET.name());
        CreateMarketOrderCommand order3 = new CreateMarketOrderCommand(
                sameUserId, marketId, OrderSide.BUY.getValue(), BigDecimal.valueOf(0.8), OrderType.MARKET.name());

        Mockito.when(testTradingRepositoryPort.findMarketItemByMarketId(marketId))
                .thenReturn(Optional.of(marketItem));
        Mockito.when(marketDataRedisAdapter.findOrderBookByMarket(marketId))
                .thenReturn(Optional.ofNullable(orderBookDto));

        // when
        tradingApplicationService.createMarketOrder(order1);
        tradingApplicationService.createMarketOrder(order2);
        tradingApplicationService.createMarketOrder(order3);

        // then
        // 순서대로 저장/처리됐는지 검증
        Mockito.verify(testTradingRepositoryPort, Mockito.times(3)).saveMarketOrder(Mockito.any());

        InOrder inOrder = Mockito.inOrder(testTradingRepositoryPort);
        inOrder.verify(testTradingRepositoryPort).saveMarketOrder(Mockito.argThat(o -> {
            MarketOrder m = (MarketOrder) o;
            return m.getUserId().getValue().equals(sameUserId);
        }));
        inOrder.verify(testTradingRepositoryPort).saveMarketOrder(Mockito.argThat(o -> {
            MarketOrder m = (MarketOrder) o;
            return m.getUserId().getValue().equals(sameUserId);
        }));
        inOrder.verify(testTradingRepositoryPort).saveMarketOrder(Mockito.argThat(o -> {
            MarketOrder m = (MarketOrder) o;
            return m.getUserId().getValue().equals(sameUserId);
        }));
    }

    @Test
    @DisplayName("마켓이 중단된 상태에서 주문 시도 시 예외 발생 테스트")
    public void createOrderWhenMarketIsPaused() {
        // given
        MarketItem pausedMarketItem = MarketItem.createMarketItem(marketId, new MarketKoreanName("비트코인"),
                new MarketEnglishName("BTC"), new MarketWarning(""),
                new TickPrice(BigDecimal.valueOf(1000L)), MarketStatus.PAUSED);
        CreateMarketOrderCommand createMarketOrderCommand = new CreateMarketOrderCommand(
                userId, marketId, OrderSide.BUY.getValue(), BigDecimal.valueOf(1.2), OrderType.MARKET.name());
        Mockito.when(testTradingRepositoryPort.findMarketItemByMarketId(marketId)).thenReturn(
                Optional.of(pausedMarketItem));
        // when
        MarketPausedException marketPausedException = Assertions.assertThrows(MarketPausedException.class, () ->
                tradingApplicationService.createMarketOrder(createMarketOrderCommand));
        // then
        Assertions.assertNotNull(marketPausedException);
        Assertions.assertEquals(String.format("MarketItem with id %s is not active", marketId),
                marketPausedException.getMessage());
    }

}
