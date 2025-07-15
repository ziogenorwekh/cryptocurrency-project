package shop.shportfolio.trading.application.test;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import shop.shportfolio.common.domain.valueobject.MarketId;
import shop.shportfolio.common.domain.valueobject.OrderPrice;
import shop.shportfolio.common.domain.valueobject.Quantity;
import shop.shportfolio.common.domain.valueobject.UserId;
import shop.shportfolio.trading.application.command.create.*;
import shop.shportfolio.trading.application.dto.orderbook.OrderBookAsksBithumbDto;
import shop.shportfolio.trading.application.dto.orderbook.OrderBookBidsBithumbDto;
import shop.shportfolio.trading.application.dto.orderbook.OrderBookBithumbDto;
import shop.shportfolio.trading.application.exception.OrderInValidatedException;
import shop.shportfolio.trading.application.ports.input.*;
import shop.shportfolio.trading.application.ports.output.kafka.TradeKafkaPublisher;
import shop.shportfolio.trading.application.ports.output.marketdata.BithumbApiPort;
import shop.shportfolio.trading.application.ports.output.redis.TradingMarketDataRedisPort;
import shop.shportfolio.trading.application.ports.output.redis.TradingOrderRedisPort;
import shop.shportfolio.trading.application.ports.output.repository.TradingCouponRepositoryPort;
import shop.shportfolio.trading.application.ports.output.repository.TradingMarketDataRepositoryPort;
import shop.shportfolio.trading.application.ports.output.repository.TradingOrderRepositoryPort;
import shop.shportfolio.trading.application.ports.output.repository.TradingTradeRecordRepositoryPort;
import shop.shportfolio.trading.application.support.RedisKeyPrefix;
import shop.shportfolio.trading.application.test.helper.TestConstants;
import shop.shportfolio.trading.application.test.helper.TradingOrderTestHelper;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.MarketItem;
import shop.shportfolio.trading.domain.entity.ReservationOrder;
import shop.shportfolio.trading.domain.valueobject.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@ExtendWith(MockitoExtension.class)
public class TradingOrderCreationTest {


    private TradingApplicationService tradingApplicationService;
    @Mock private TradingOrderRepositoryPort tradingOrderRepositoryPort;
    @Mock private TradingTradeRecordRepositoryPort tradingTradeRecordRepositoryPort;
    @Mock private TradingOrderRedisPort tradingOrderRedisPort;
    @Mock private TradingMarketDataRedisPort tradingMarketDataRedisPort;
    @Mock private TradeKafkaPublisher tradeKafkaPublisher;
    @Mock private TradingCouponRepositoryPort tradingCouponRepositoryPort;
    @Mock private TradingMarketDataRepositoryPort tradingMarketDataRepositoryPort;
    @Mock private BithumbApiPort bithumbApiPort;

    private final MarketStatus marketStatus = TestConstants.MARKET_STATUS;
    private final UUID userId = TestConstants.TEST_USER_ID;
    private final String marketId = TestConstants.TEST_MARKET_ID;
    private final BigDecimal orderPrice = TestConstants.ORDER_PRICE;
    private final String orderSide = TestConstants.ORDER_SIDE;
    private final BigDecimal quantity = TestConstants.QUANTITY;
    private final OrderType orderTypeLimit = TestConstants.ORDER_TYPE_LIMIT;
    private final OrderType orderTypeMarket = TestConstants.ORDER_TYPE_MARKET;
    private final MarketItem marketItem = TestConstants.MARKET_ITEM;


    private OrderBookBithumbDto orderBookBithumbDto;

    @BeforeEach
    public void setUp() {

        tradingApplicationService = TradingOrderTestHelper.createTradingApplicationService(
                tradingOrderRepositoryPort,
                tradingTradeRecordRepositoryPort,
                tradingOrderRedisPort,
                tradingMarketDataRepositoryPort,
                tradingMarketDataRedisPort,
                tradingCouponRepositoryPort,
                tradeKafkaPublisher,
                bithumbApiPort
        );
        orderBookBithumbDto = new OrderBookBithumbDto();
        orderBookBithumbDto.setMarket(marketId);
        orderBookBithumbDto.setTimestamp(System.currentTimeMillis());
        orderBookBithumbDto.setTotalAskSize(5.0);
        orderBookBithumbDto.setTotalBidSize(3.0);

        // 매도 호가 리스트 (가격 상승 순으로)
        List<OrderBookAsksBithumbDto> asks = List.of(
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
        orderBookBithumbDto.setAsks(asks);

        // 매수 호가 리스트 (가격 하락 순으로)
        List<OrderBookBidsBithumbDto> bids = List.of(
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
        orderBookBithumbDto.setBids(bids);
        orderBookBithumbDto.setBids(bids);
    }

    // 편의 메서드
    private OrderBookAsksBithumbDto createAsk(Double price, Double size) {
        OrderBookAsksBithumbDto ask = new OrderBookAsksBithumbDto();
        ask.setAskPrice(price);
        ask.setAskSize(size);
        return ask;
    }

    private OrderBookBidsBithumbDto createBid(Double price, Double size) {
        OrderBookBidsBithumbDto bid = new OrderBookBidsBithumbDto();
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
        Mockito.when(tradingOrderRepositoryPort.saveLimitOrder(Mockito.any())).thenReturn(
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
        Mockito.when(tradingMarketDataRedisPort.findOrderBookByMarket(RedisKeyPrefix.market(marketId))).thenReturn(
                Optional.of(orderBookBithumbDto));
        Mockito.when(tradingMarketDataRepositoryPort.findMarketItemByMarketId(marketId)).thenReturn(
                Optional.of(marketItem)
        );
        // when
        CreateLimitOrderResponse createLimitOrderResponse = tradingApplicationService.
                createLimitOrder(createLimitOrderCommand);
        // then
        Mockito.verify(tradingOrderRedisPort, Mockito.times(1)).
                saveLimitOrder(Mockito.any(), Mockito.any());
        Mockito.verify(tradingOrderRepositoryPort, Mockito.times(2))
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
        Mockito.when(tradingMarketDataRepositoryPort.findMarketItemByMarketId(marketId)).thenReturn(
                Optional.of(marketItem));
        CreateMarketOrderCommand createMarketOrderCommand = new CreateMarketOrderCommand(userId, marketId,
                 orderSide, innerQuantity.getValue(), orderTypeMarket.name());
        Mockito.when(tradingMarketDataRedisPort.findOrderBookByMarket(RedisKeyPrefix.market(marketId)))
                .thenReturn(Optional.ofNullable(orderBookBithumbDto));
        // when
        tradingApplicationService.createMarketOrder(createMarketOrderCommand);
        // then
        Mockito.verify(tradingOrderRepositoryPort, Mockito.times(1))
                .saveMarketOrder(Mockito.any());
        Mockito.verify(tradeKafkaPublisher, Mockito.times(4))
                .publish(Mockito.any());
        Mockito.verify(tradingMarketDataRedisPort, Mockito.times(2))
                .findOrderBookByMarket(RedisKeyPrefix.market(marketId));
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

    @Test
    @DisplayName("예약 주문 생성 테스트")
    public void createReservationOrderTest() {
        // given
        BigDecimal price = BigDecimal.valueOf(1_010_000.0);
        LocalDateTime scheduledTime = LocalDateTime.now().plusDays(1);
        LocalDateTime expireAt = LocalDateTime.now().plusMonths(1);
        CreateReservationOrderCommand command = new CreateReservationOrderCommand(
                userId,marketId,"BUY",BigDecimal.valueOf(2L),
                "RESERVATION","ABOVE", price, scheduledTime,
                expireAt
                ,true);
        ReservationOrder reservationOrder = ReservationOrder.
                createReservationOrder(new UserId(userId), new MarketId(marketId), OrderSide.BUY
                        , new Quantity(BigDecimal.valueOf(2)), OrderType.RESERVATION, TriggerCondition.of(TriggerType.ABOVE,
                                new OrderPrice(price)), ScheduledTime.of(scheduledTime), new ExpireAt(expireAt),
                        IsRepeatable.of(true));
        Mockito.when(tradingOrderRepositoryPort.saveReservationOrder(Mockito.any()))
                .thenReturn(reservationOrder);
        MarketItem marketItem = MarketItem.createMarketItem(marketId, new MarketKoreanName("비트코인"),
                new MarketEnglishName("BTC"), new MarketWarning(""),
                new TickPrice(BigDecimal.valueOf(1000L)),marketStatus);
        Mockito.when(tradingMarketDataRepositoryPort.findMarketItemByMarketId(marketId)).thenReturn(
                Optional.of(marketItem));
        Mockito.when(tradingMarketDataRedisPort.findOrderBookByMarket(Mockito.any()))
                .thenReturn(Optional.of(orderBookBithumbDto));
        // when
        CreateReservationResponse response = tradingApplicationService.createReservationOrder(command);
        // then

        Assertions.assertNotNull(response);
        Assertions.assertEquals(scheduledTime, response.getScheduledTime());
        Assertions.assertEquals(expireAt, response.getExpireAt());
        Assertions.assertEquals(reservationOrder.getId().getValue(),response.getOrderId());
    }

    @Test
    @DisplayName("주문 수량이 호가 총합보다 초과하는 경우 처리 테스트 <- 호가 총합보다 많이 주문 할 수 없도록 검증 로직 생겨서 테스트 안됨")
    public void createMarketOrderExceedQuantity() {
        // given
        CreateMarketOrderCommand createMarketOrderCommand =
                new CreateMarketOrderCommand(userId, marketId, OrderSide.BUY.toString(),
                        BigDecimal.valueOf(100.0), OrderType.MARKET.name());
        Mockito.when(tradingMarketDataRepositoryPort.findMarketItemByMarketId(marketId)).thenReturn(
                Optional.of(marketItem));
        Mockito.when(tradingMarketDataRedisPort.findOrderBookByMarket(RedisKeyPrefix.market(marketId)))
                .thenReturn(Optional.ofNullable(orderBookBithumbDto));
        // when
        OrderInValidatedException orderInValidatedException = Assertions.assertThrows(
                OrderInValidatedException.class, () -> {
                    tradingApplicationService.createMarketOrder(createMarketOrderCommand);
                });
        Assertions.assertNotNull(orderInValidatedException);
        Assertions.assertEquals("Buy order quantity exceeds available sell liquidity.",
                orderInValidatedException.getMessage());
        // then
//        Mockito.verify(tradingOrderRepositoryPort, Mockito.times(1)).saveMarketOrder(Mockito.any());
//        Mockito.verify(tradeKafkaPublisher, Mockito.times(10))
//                .publish(Mockito.any());
    }

    @Test
    @DisplayName("시장가 매도 주문 시 호가 부족으로 부분 체결 후 잔량 처리 테스트 <- 이상거래 주문으로 감지되서 테스트 안됨 수정")
    public void createMarketSellOrderWithPartialMatchDueToInsufficientBids() {
        // given
        CreateMarketOrderCommand createMarketOrderCommand = new CreateMarketOrderCommand(userId, marketId
                , OrderSide.SELL.getValue(), BigDecimal.valueOf(1000L), OrderType.MARKET.name());
        Mockito.when(tradingMarketDataRedisPort.findOrderBookByMarket(RedisKeyPrefix.market(marketId)))
                .thenReturn(Optional.ofNullable(orderBookBithumbDto));
        Mockito.when(tradingMarketDataRepositoryPort.findMarketItemByMarketId(marketId))
                .thenReturn(Optional.of(marketItem));
        // when
        OrderInValidatedException orderInValidatedException = Assertions.assertThrows(OrderInValidatedException.class, () -> {
            tradingApplicationService.createMarketOrder(createMarketOrderCommand);
        });

        // then
        Assertions.assertNotNull(orderInValidatedException);
        Assertions.assertEquals("Sell order quantity exceeds available buy liquidity.",
                orderInValidatedException.getMessage());
    }

    @Test
    @DisplayName("지정가 매수 주문인데 너무 높은 가격으로 사려고 하면 에러가 나야하는 테스트(상한 10%까지)")
    public void whenLimitBuyOrderPriceExceedsUpperLimit_thenThrowsOrderInValidatedException() {
        // given
        BigDecimal price = BigDecimal.valueOf(1_550_000.0);
        BigDecimal quantity1 = BigDecimal.valueOf(1L);
        CreateLimitOrderCommand createLimitOrderCommand = new CreateLimitOrderCommand(userId, marketId
                , OrderSide.BUY.getValue(), price,
                quantity1, OrderType.LIMIT.name());
        LimitOrder limitOrder = LimitOrder.createLimitOrder(new UserId(userId), new MarketId(marketId), OrderSide.BUY, new Quantity(quantity1)
                , new OrderPrice(price), OrderType.LIMIT);
        Mockito.when(tradingMarketDataRedisPort.findOrderBookByMarket(RedisKeyPrefix.market(marketId)))
                .thenReturn(Optional.ofNullable(orderBookBithumbDto));
        Mockito.when(tradingMarketDataRepositoryPort.findMarketItemByMarketId(marketId))
                .thenReturn(Optional.of(marketItem));
        Mockito.when(tradingOrderRepositoryPort.saveLimitOrder(Mockito.any(LimitOrder.class)))
                .thenReturn(limitOrder);
        // when
        OrderInValidatedException orderInValidatedException = Assertions.assertThrows(OrderInValidatedException.class, () -> {
            tradingApplicationService.createLimitOrder(createLimitOrderCommand);
        });
        // then
        Assertions.assertNotNull(orderInValidatedException);
        Assertions.assertEquals("Limit buy order price is more than 10% above best ask.",
                orderInValidatedException.getMessage());
    }

    @Test
    @DisplayName("지정가 매수 주문인데 너무 높은 가격으로 사려고 하면 에러가 나야하는 테스트(상한 10% 경계선) 경계선까지는 포함 됌")
    public void whenLimitBuyOrderPriceExceedsUpperExactlyTenPercentLimit_thenThrowsOrderInValidatedException() {
        // given
        BigDecimal price = BigDecimal.valueOf(1_155_001.0);
        BigDecimal quantity1 = BigDecimal.valueOf(1L);
        CreateLimitOrderCommand createLimitOrderCommand = new CreateLimitOrderCommand(userId, marketId
                , OrderSide.BUY.getValue(), price,
                quantity1, OrderType.LIMIT.name());
        LimitOrder limitOrder = LimitOrder.createLimitOrder(new UserId(userId), new MarketId(marketId), OrderSide.BUY, new Quantity(quantity1)
                , new OrderPrice(price), OrderType.LIMIT);
        Mockito.when(tradingMarketDataRedisPort.findOrderBookByMarket(RedisKeyPrefix.market(marketId)))
                .thenReturn(Optional.ofNullable(orderBookBithumbDto));
        Mockito.when(tradingMarketDataRepositoryPort.findMarketItemByMarketId(marketId))
                .thenReturn(Optional.of(marketItem));
        Mockito.when(tradingOrderRepositoryPort.saveLimitOrder(Mockito.any(LimitOrder.class)))
                .thenReturn(limitOrder);
        // when
        OrderInValidatedException orderInValidatedException = Assertions.assertThrows(OrderInValidatedException.class, () -> {
            tradingApplicationService.createLimitOrder(createLimitOrderCommand);
        });
        // then
        Assertions.assertNotNull(orderInValidatedException);
        Assertions.assertEquals("Limit buy order price is more than 10% above best ask.",
                orderInValidatedException.getMessage());
    }

    @Test
    @DisplayName("지정가 매도 주문인데 너무 낮은 가격으로 팔려고 하면 에러가 나야하는 테스트(하한 10% 경계선)")
    public void whenLimitSellOrderPriceExceedsUpperExactlyTenPercentLimit_thenThrowsOrderInValidatedException() {
        // given
        BigDecimal price = BigDecimal.valueOf(800_000.0);
        BigDecimal quantity1 = BigDecimal.valueOf(1L);
        CreateLimitOrderCommand createLimitOrderCommand = new CreateLimitOrderCommand(userId, marketId
                , OrderSide.SELL.getValue(), price,
                quantity1, OrderType.LIMIT.name());
        LimitOrder limitOrder = LimitOrder.createLimitOrder(new UserId(userId), new MarketId(marketId), OrderSide.SELL,
                new Quantity(quantity1), new OrderPrice(price), OrderType.LIMIT);
        Mockito.when(tradingMarketDataRedisPort.findOrderBookByMarket(RedisKeyPrefix.market(marketId)))
                .thenReturn(Optional.ofNullable(orderBookBithumbDto));
        Mockito.when(tradingMarketDataRepositoryPort.findMarketItemByMarketId(marketId))
                .thenReturn(Optional.of(marketItem));
        Mockito.when(tradingOrderRepositoryPort.saveLimitOrder(Mockito.any(LimitOrder.class)))
                .thenReturn(limitOrder);
        // when
        OrderInValidatedException orderInValidatedException = Assertions.assertThrows(OrderInValidatedException.class, () -> {
            tradingApplicationService.createLimitOrder(createLimitOrderCommand);
        });
        // then
        Assertions.assertNotNull(orderInValidatedException);
        Assertions.assertEquals("Limit sell order price is more than 10% below best bid.",
                orderInValidatedException.getMessage());
    }


    @Test
    @DisplayName("예약 매수 주문 생성 테스트인데, 구매가가 높은 경우 에러나는 테스트(가격과는 상관없음)")
    public void whenReservationBuyOrderPriceExceedsUpperExactlyTenPercentLimit_thenThrowsOrderInValidatedException() {
        // given
        BigDecimal price = BigDecimal.valueOf(1_510_000.0);
        LocalDateTime scheduledTime = LocalDateTime.now().plusDays(1);
        LocalDateTime expireAt = LocalDateTime.now().plusMonths(1);
        CreateReservationOrderCommand command = new CreateReservationOrderCommand(
                userId,marketId,"BUY",BigDecimal.valueOf(2L),
                "RESERVATION","ABOVE", price, scheduledTime,
                expireAt
                ,true);
        ReservationOrder reservationOrder = ReservationOrder.
                createReservationOrder(new UserId(userId), new MarketId(marketId), OrderSide.BUY
                        , new Quantity(BigDecimal.valueOf(2)), OrderType.RESERVATION, TriggerCondition.of(TriggerType.ABOVE,
                                new OrderPrice(price)), ScheduledTime.of(scheduledTime), new ExpireAt(expireAt),
                        IsRepeatable.of(true));
        Mockito.when(tradingOrderRepositoryPort.saveReservationOrder(Mockito.any()))
                .thenReturn(reservationOrder);
        MarketItem marketItem = MarketItem.createMarketItem(marketId, new MarketKoreanName("비트코인"),
                new MarketEnglishName("BTC"), new MarketWarning(""),
                new TickPrice(BigDecimal.valueOf(1000L)),marketStatus);
        Mockito.when(tradingMarketDataRepositoryPort.findMarketItemByMarketId(marketId)).thenReturn(
                Optional.of(marketItem));
        Mockito.when(tradingMarketDataRedisPort.findOrderBookByMarket(Mockito.any()))
                .thenReturn(Optional.of(orderBookBithumbDto));
        // when
        CreateReservationResponse response = tradingApplicationService.createReservationOrder(command);
        // then
        Assertions.assertNotNull(response);
        Assertions.assertEquals(reservationOrder.getId().getValue(),response.getOrderId());
    }

    @Test
    @DisplayName("예약 매수 주문 생성 테스트인데, 호가 수량을 넘어서면 에러나는 테스트")
    public void whenReservationBuyOrderPriceExceedsOrderBookTotalSize_thenThrowsOrderInValidatedException() {
        // given
        BigDecimal price = BigDecimal.valueOf(1_010_000.0);
        LocalDateTime scheduledTime = LocalDateTime.now().plusDays(1);
        LocalDateTime expireAt = LocalDateTime.now().plusMonths(1);
        CreateReservationOrderCommand command = new CreateReservationOrderCommand(
                userId,marketId,"BUY",BigDecimal.valueOf(2000L),
                "RESERVATION","ABOVE", price, scheduledTime,
                expireAt
                ,true);
        ReservationOrder reservationOrder = ReservationOrder.
                createReservationOrder(new UserId(userId), new MarketId(marketId), OrderSide.BUY
                        , new Quantity(BigDecimal.valueOf(2000L)), OrderType.RESERVATION, TriggerCondition.of(TriggerType.ABOVE,
                                new OrderPrice(price)), ScheduledTime.of(scheduledTime), new ExpireAt(expireAt),
                        IsRepeatable.of(true));
        Mockito.when(tradingOrderRepositoryPort.saveReservationOrder(Mockito.any()))
                .thenReturn(reservationOrder);
        MarketItem marketItem = MarketItem.createMarketItem(marketId, new MarketKoreanName("비트코인"),
                new MarketEnglishName("BTC"), new MarketWarning(""),
                new TickPrice(BigDecimal.valueOf(1000L)),marketStatus);
        Mockito.when(tradingMarketDataRepositoryPort.findMarketItemByMarketId(marketId)).thenReturn(
                Optional.of(marketItem));
        Mockito.when(tradingMarketDataRedisPort.findOrderBookByMarket(Mockito.any()))
                .thenReturn(Optional.of(orderBookBithumbDto));
        // when
        OrderInValidatedException orderInValidatedException = Assertions.assertThrows(OrderInValidatedException.class, () -> {
            tradingApplicationService.createReservationOrder(command);

        });
        // then
        Assertions.assertNotNull(orderInValidatedException);
        Assertions.assertEquals("Buy order quantity exceeds available sell liquidity.",
                orderInValidatedException.getMessage());
    }

    @Test
    @DisplayName("예약 매도 주문 생성 테스트인데, 호가 수량을 넘어서면 에러나는 테스트")
    public void whenReservationSellOrderPriceExceedsOrderBookTotalSize_thenThrowsOrderInValidatedException() {
        // given
        BigDecimal price = BigDecimal.valueOf(1_010_000.0);
        LocalDateTime scheduledTime = LocalDateTime.now().plusDays(1);
        LocalDateTime expireAt = LocalDateTime.now().plusMonths(1);
        CreateReservationOrderCommand command = new CreateReservationOrderCommand(
                userId,marketId,"SELL",BigDecimal.valueOf(2000L),
                "RESERVATION","ABOVE", price, scheduledTime,
                expireAt
                ,true);
        ReservationOrder reservationOrder = ReservationOrder.
                createReservationOrder(new UserId(userId), new MarketId(marketId), OrderSide.SELL
                        , new Quantity(BigDecimal.valueOf(2000L)), OrderType.RESERVATION, TriggerCondition.of(TriggerType.ABOVE,
                                new OrderPrice(price)), ScheduledTime.of(scheduledTime), new ExpireAt(expireAt),
                        IsRepeatable.of(true));
        Mockito.when(tradingOrderRepositoryPort.saveReservationOrder(Mockito.any()))
                .thenReturn(reservationOrder);
        MarketItem marketItem = MarketItem.createMarketItem(marketId, new MarketKoreanName("비트코인"),
                new MarketEnglishName("BTC"), new MarketWarning(""),
                new TickPrice(BigDecimal.valueOf(1000L)),marketStatus);
        Mockito.when(tradingMarketDataRepositoryPort.findMarketItemByMarketId(marketId)).thenReturn(
                Optional.of(marketItem));
        Mockito.when(tradingMarketDataRedisPort.findOrderBookByMarket(Mockito.any()))
                .thenReturn(Optional.of(orderBookBithumbDto));
        // when
        OrderInValidatedException orderInValidatedException2 = Assertions.assertThrows(OrderInValidatedException.class, () -> {
            tradingApplicationService.createReservationOrder(command);

        });
        // then
        Assertions.assertNotNull(orderInValidatedException2);
        Assertions.assertEquals("Sell order quantity exceeds available buy liquidity.",
                orderInValidatedException2.getMessage());
    }

}
