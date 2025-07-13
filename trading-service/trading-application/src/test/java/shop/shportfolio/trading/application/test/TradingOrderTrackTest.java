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
import shop.shportfolio.trading.application.TradingApplicationServiceImpl;
import shop.shportfolio.trading.application.command.track.LimitOrderTrackQuery;
import shop.shportfolio.trading.application.command.track.LimitOrderTrackResponse;
import shop.shportfolio.trading.application.exception.OrderNotFoundException;
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
import shop.shportfolio.trading.application.validator.LimitOrderValidator;
import shop.shportfolio.trading.application.validator.MarketOrderValidator;
import shop.shportfolio.trading.application.validator.ReservationOrderValidator;
import shop.shportfolio.trading.domain.TradingDomainService;
import shop.shportfolio.trading.domain.TradingDomainServiceImpl;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.MarketOrder;
import shop.shportfolio.trading.domain.entity.Order;
import shop.shportfolio.trading.domain.entity.Trade;
import shop.shportfolio.trading.domain.valueobject.OrderSide;
import shop.shportfolio.trading.domain.valueobject.OrderType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@ExtendWith(MockitoExtension.class)
public class TradingOrderTrackTest {

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
    }

    private final UUID userId = UUID.randomUUID();
    private final String marketId = "BTC-KRW";
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
        LimitOrderTrackQuery limitOrderTrackQuery = new LimitOrderTrackQuery(limitOrder.getId().getValue(),userId);
        Mockito.when(tradingOrderRepositoryPort.findLimitOrderByOrderIdAndUserId(limitOrder.getId().getValue(),
                        limitOrder.getUserId().getValue()))
                .thenReturn(Optional.of(limitOrder));
        // when
        LimitOrderTrackResponse track = tradingApplicationService.findLimitOrderTrackByOrderIdAndUserId(limitOrderTrackQuery);
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
        LimitOrderTrackQuery limitOrderTrackQuery = new LimitOrderTrackQuery("anonymous",userId);
        // when
        OrderNotFoundException orderNotFoundException = Assertions.assertThrows(OrderNotFoundException.class, () ->
                tradingApplicationService.findLimitOrderTrackByOrderIdAndUserId(limitOrderTrackQuery));
        // then
        Assertions.assertNotNull(orderNotFoundException);
        Assertions.assertEquals("Order with id " +
                "anonymous" + " not found", orderNotFoundException.getMessage());
    }

}
