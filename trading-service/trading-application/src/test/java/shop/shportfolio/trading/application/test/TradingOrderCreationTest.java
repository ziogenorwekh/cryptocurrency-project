package shop.shportfolio.trading.application.test;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import shop.shportfolio.common.domain.valueobject.MarketId;
import shop.shportfolio.common.domain.valueobject.OrderPrice;
import shop.shportfolio.common.domain.valueobject.Quantity;
import shop.shportfolio.common.domain.valueobject.UserId;
import shop.shportfolio.trading.application.TradingApplicationServiceImpl;
import shop.shportfolio.trading.application.command.create.*;
import shop.shportfolio.trading.application.dto.orderbook.OrderBookAsksBithumbDto;
import shop.shportfolio.trading.application.dto.orderbook.OrderBookBidsBithumbDto;
import shop.shportfolio.trading.application.dto.orderbook.OrderBookBithumbDto;
import shop.shportfolio.trading.application.facade.ExecuteOrderMatchingFacade;
import shop.shportfolio.trading.application.facade.TradingCreateOrderFacade;
import shop.shportfolio.trading.application.facade.TradingTrackFacade;
import shop.shportfolio.trading.application.facade.TradingUpdateFacade;
import shop.shportfolio.trading.application.handler.OrderBookManager;
import shop.shportfolio.trading.application.handler.create.TradingCreateHandler;
import shop.shportfolio.trading.application.handler.matching.strategy.LimitOrderMatchingStrategy;
import shop.shportfolio.trading.application.handler.matching.strategy.MarketOrderMatchingStrategy;
import shop.shportfolio.trading.application.handler.matching.strategy.OrderMatchingStrategy;
import shop.shportfolio.trading.application.handler.matching.strategy.ReservationOrderMatchingStrategy;
import shop.shportfolio.trading.application.handler.track.CouponInfoTrackHandler;
import shop.shportfolio.trading.application.handler.track.TradingTrackHandler;
import shop.shportfolio.trading.application.handler.update.TradingUpdateHandler;
import shop.shportfolio.trading.application.mapper.TradingDataMapper;
import shop.shportfolio.trading.application.mapper.TradingDtoMapper;
import shop.shportfolio.trading.application.policy.DefaultFeePolicy;
import shop.shportfolio.trading.application.policy.FeePolicy;
import shop.shportfolio.trading.application.ports.input.*;
import shop.shportfolio.trading.application.ports.output.kafka.TradeKafkaPublisher;
import shop.shportfolio.trading.application.ports.output.redis.TradingMarketDataRedisPort;
import shop.shportfolio.trading.application.ports.output.redis.TradingOrderRedisPort;
import shop.shportfolio.trading.application.ports.output.repository.TradingCouponRepositoryPort;
import shop.shportfolio.trading.application.ports.output.repository.TradingMarketDataRepositoryPort;
import shop.shportfolio.trading.application.ports.output.repository.TradingOrderRepositoryPort;
import shop.shportfolio.trading.application.ports.output.repository.TradingTradeRecordRepositoryPort;
import shop.shportfolio.trading.application.support.RedisKeyPrefix;
import shop.shportfolio.trading.application.validator.LimitOrderValidator;
import shop.shportfolio.trading.application.validator.MarketOrderValidator;
import shop.shportfolio.trading.application.validator.ReservationOrderValidator;
import shop.shportfolio.trading.domain.TradingDomainService;
import shop.shportfolio.trading.domain.TradingDomainServiceImpl;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.MarketItem;
import shop.shportfolio.trading.domain.entity.Order;
import shop.shportfolio.trading.domain.entity.ReservationOrder;
import shop.shportfolio.trading.domain.valueobject.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@ExtendWith(MockitoExtension.class)
public class TradingOrderCreationTest {

    private TradingApplicationService tradingApplicationService;

    @Mock
    private TradingOrderRepositoryPort tradingOrderRepositoryPort;

    @Mock
    private TradingTradeRecordRepositoryPort tradingTradeRecordRepositoryPort;

    @Mock
    private TradingOrderRedisPort tradingOrderRedisPort;

    @Mock
    private TradingMarketDataRedisPort tradingMarketDataRedisPort;

    @Mock
    private TradeKafkaPublisher tradeKafkaPublisher;

    @Mock
    private TradingCouponRepositoryPort testTradingCouponRepositoryPort;

    private TradingDtoMapper tradingDtoMapper;

    private TradingDomainService tradingDomainService;

    private CouponInfoTrackHandler couponInfoTrackHandler;

    @Mock
    private TradingCouponRepositoryPort tradingCouponRepositoryPort;

    @Mock
    private TradingMarketDataRepositoryPort tradingMarketDataRepositoryPort;

    private TradingCreateOrderUseCase tradingCreateOrderUseCase;
    private TradingDataMapper tradingDataMapper;
    private TradingCreateHandler tradingCreateHandler;
    private TradingTrackUseCase tradingTrackUseCase;
    private TradingUpdateUseCase tradingUpdateUseCase;
    private TradingUpdateHandler tradingUpdateHandler;
    private ExecuteOrderMatchingUseCase executeOrderMatchingUseCase;
    private LimitOrderMatchingStrategy limitOrderMatchingStrategy;
    private MarketOrderMatchingStrategy marketOrderMatchingStrategy;
    private ReservationOrderMatchingStrategy reservationOrderMatchingStrategy;
    private FeePolicy feePolicy;

    private final MarketStatus marketStatus = MarketStatus.ACTIVE;
    private final UUID userId = UUID.randomUUID();
    private final String marketId = "BTC-KRW";
    private final BigDecimal orderPrice = BigDecimal.valueOf(1_050_000);
    private final String orderSide = "BUY";
    private final BigDecimal quantity = BigDecimal.valueOf(5L);
    private final OrderType orderTypeLimit = OrderType.LIMIT;
    private final OrderType orderTypeMarket = OrderType.MARKET;
    private OrderBookBithumbDto orderBookBithumbDto;
    private List<OrderValidator<? extends Order>> orderValidators;
    private LimitOrderValidator limitOrderValidator;
    private MarketOrderValidator marketOrderValidator;
    private ReservationOrderValidator reservationOrderValidator;
    @BeforeEach
    public void setUp() {
        feePolicy = new DefaultFeePolicy();
        tradingUpdateHandler = new TradingUpdateHandler(tradingOrderRepositoryPort, tradingDomainService, tradingOrderRedisPort);
        tradingDtoMapper = new TradingDtoMapper();
        tradingDataMapper = new TradingDataMapper();
        tradingDomainService = new TradingDomainServiceImpl();
        couponInfoTrackHandler = new CouponInfoTrackHandler(tradingCouponRepositoryPort);
        OrderBookManager orderBookManager = new OrderBookManager(tradingDomainService,
                tradingOrderRepositoryPort, tradingDtoMapper, tradingOrderRedisPort, tradingMarketDataRedisPort
                , tradingTradeRecordRepositoryPort, tradingMarketDataRepositoryPort);
        TradingTrackHandler tradingTrackHandler = new TradingTrackHandler(tradingOrderRepositoryPort,
                tradingTradeRecordRepositoryPort, tradingMarketDataRepositoryPort);
        tradingCreateHandler = new TradingCreateHandler(tradingOrderRepositoryPort,
                tradingMarketDataRepositoryPort, tradingDomainService);
        orderValidators = new ArrayList<>();
        limitOrderValidator = new LimitOrderValidator(orderBookManager, tradingMarketDataRepositoryPort);
        marketOrderValidator = new MarketOrderValidator(orderBookManager, tradingMarketDataRepositoryPort);
        reservationOrderValidator = new ReservationOrderValidator(orderBookManager, tradingMarketDataRepositoryPort);
        orderValidators.add(limitOrderValidator);
        orderValidators.add(marketOrderValidator);
        orderValidators.add(reservationOrderValidator);
        tradingCreateOrderUseCase = new TradingCreateOrderFacade(tradingCreateHandler,orderValidators);
        limitOrderMatchingStrategy = new LimitOrderMatchingStrategy(tradingDomainService,
                tradingOrderRepositoryPort, tradingTradeRecordRepositoryPort,
                tradingOrderRedisPort, couponInfoTrackHandler,feePolicy);
        marketOrderMatchingStrategy = new MarketOrderMatchingStrategy(tradingDomainService, tradingOrderRepositoryPort,
                tradingTradeRecordRepositoryPort, couponInfoTrackHandler, feePolicy);
        reservationOrderMatchingStrategy = new ReservationOrderMatchingStrategy(tradingDomainService,
                tradingOrderRepositoryPort, couponInfoTrackHandler
                , tradingOrderRedisPort, feePolicy, tradingTradeRecordRepositoryPort);
        List<OrderMatchingStrategy<? extends Order>> strategies = new ArrayList<>();
        strategies.add(limitOrderMatchingStrategy);
        strategies.add(marketOrderMatchingStrategy);
        strategies.add(reservationOrderMatchingStrategy);
        tradingTrackUseCase = new TradingTrackFacade(tradingTrackHandler, orderBookManager);
        tradingUpdateUseCase = new TradingUpdateFacade(tradingUpdateHandler,tradingTrackHandler);
        executeOrderMatchingUseCase = new ExecuteOrderMatchingFacade(orderBookManager, tradeKafkaPublisher, strategies);
        tradingApplicationService = new TradingApplicationServiceImpl(tradingCreateOrderUseCase
                ,tradingTrackUseCase,tradingDataMapper,tradingUpdateUseCase,executeOrderMatchingUseCase);
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
        Mockito.verify(tradingMarketDataRedisPort, Mockito.times(1))
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
        BigDecimal price = BigDecimal.valueOf(10_500_000.0);
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
        // when
        CreateReservationResponse response = tradingApplicationService.createReservationOrder(command);
        // then

        Assertions.assertNotNull(response);
        Assertions.assertEquals(scheduledTime, response.getScheduledTime());
        Assertions.assertEquals(expireAt, response.getExpireAt());
        Assertions.assertEquals(reservationOrder.getId().getValue(),response.getOrderId());
    }

}
