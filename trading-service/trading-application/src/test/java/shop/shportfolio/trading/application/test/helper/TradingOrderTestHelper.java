package shop.shportfolio.trading.application.test.helper;

import shop.shportfolio.trading.application.TradingApplicationServiceImpl;
import shop.shportfolio.trading.application.ports.output.marketdata.BithumbApiPort;
import shop.shportfolio.trading.application.usecase.ExecuteOrderMatchingUseCaseImpl;
import shop.shportfolio.trading.application.usecase.TradingCreateOrderUseCaseImpl;
import shop.shportfolio.trading.application.usecase.TradingTrackUseCaseImpl;
import shop.shportfolio.trading.application.usecase.TradingUpdateUseCaseImpl;
import shop.shportfolio.trading.application.handler.OrderBookManager;
import shop.shportfolio.trading.application.handler.UserBalanceHandler;
import shop.shportfolio.trading.application.handler.create.TradingCreateHandler;
import shop.shportfolio.trading.application.handler.matching.OrderExecutionChecker;
import shop.shportfolio.trading.application.handler.matching.OrderMatchProcessor;
import shop.shportfolio.trading.application.handler.matching.strategy.LimitOrderMatchingStrategy;
import shop.shportfolio.trading.application.handler.matching.strategy.MarketOrderMatchingStrategy;
import shop.shportfolio.trading.application.handler.matching.strategy.OrderMatchingStrategy;
import shop.shportfolio.trading.application.handler.matching.strategy.ReservationOrderMatchingStrategy;
import shop.shportfolio.trading.application.handler.CouponInfoHandler;
import shop.shportfolio.trading.application.handler.track.MarketDataTrackHandler;
import shop.shportfolio.trading.application.handler.track.TradingTrackHandler;
import shop.shportfolio.trading.application.handler.update.TradingUpdateHandler;
import shop.shportfolio.trading.application.mapper.TradingDataMapper;
import shop.shportfolio.trading.application.mapper.TradingDtoMapper;
import shop.shportfolio.trading.application.policy.*;
import shop.shportfolio.trading.application.ports.input.*;
import shop.shportfolio.trading.application.ports.output.kafka.TradeKafkaPublisher;
import shop.shportfolio.trading.application.ports.output.kafka.UserBalanceKafkaPublisher;
import shop.shportfolio.trading.application.ports.output.redis.TradingMarketDataRedisPort;
import shop.shportfolio.trading.application.ports.output.redis.TradingOrderRedisPort;
import shop.shportfolio.trading.application.ports.output.repository.*;
import shop.shportfolio.trading.application.handler.matching.FeeRateResolver;
import shop.shportfolio.trading.application.validator.LimitOrderValidator;
import shop.shportfolio.trading.application.validator.MarketOrderValidator;
import shop.shportfolio.trading.application.validator.OrderValidator;
import shop.shportfolio.trading.application.validator.ReservationOrderValidator;
import shop.shportfolio.trading.domain.*;
import shop.shportfolio.trading.domain.entity.Order;

import java.util.List;

public class TradingOrderTestHelper {


    public UserBalanceDomainService userBalanceDomainService;
    public TradeDomainService tradeDomainService;
    public TradingUpdateUseCase tradingUpdateUseCase;
    public OrderDomainService orderDomainService;
    public CouponInfoHandler couponInfo;
    public FeeRateResolver feeRateResolver;
    public OrderExecutionChecker orderExecutionChecker;
    public OrderMatchProcessor orderMatchProcessor;
    public UserBalanceHandler userBalanceHandler;
    public OrderBookManager orderBookManager;
    public List<OrderMatchingStrategy<? extends Order>> strategies;
    public TradingApplicationService createTradingApplicationService(
            TradingOrderRepositoryPort orderRepo,
            TradingTradeRecordRepositoryPort tradeRecordRepo,
            TradingOrderRedisPort orderRedis,
            TradingMarketDataRepositoryPort marketRepo,
            TradingMarketDataRedisPort marketDataRedis,
            TradingCouponRepositoryPort couponRepo,
            TradeKafkaPublisher kafkaPublisher,
            TradingUserBalanceRepositoryPort tradingUserBalanceRepository,
            UserBalanceKafkaPublisher userBalanceKafkaPublisher,
            BithumbApiPort bithumbApiPort
    ) {
        userBalanceDomainService = new UserBalanceDomainServiceImpl();
        TradingDtoMapper dtoMapper = new TradingDtoMapper();
        TradingDataMapper dataMapper = new TradingDataMapper();
         orderDomainService = new OrderDomainServiceImpl();
        FeePolicy feePolicy = new DefaultFeePolicy();
        LiquidityPolicy liquidityPolicy = new DefaultLiquidityPolicy();
        PriceLimitPolicy priceLimitPolicy = new DefaultPriceLimitPolicy();
        tradeDomainService = new TradeDomainServiceImpl();
        orderBookManager = new OrderBookManager(orderDomainService,
                dtoMapper, orderRedis, marketDataRedis, tradeRecordRepo, marketRepo, tradeDomainService);

        TradingTrackHandler trackHandler = new TradingTrackHandler(orderRepo, tradeRecordRepo, marketRepo);
        TradingCreateHandler createHandler = new TradingCreateHandler(orderRepo, marketRepo, orderDomainService);
        TradingUpdateHandler updateHandler = new TradingUpdateHandler(orderRepo, orderDomainService, orderRedis);

        MarketDataTrackHandler marketDataTrackHandler = new MarketDataTrackHandler(
                marketRepo, tradeRecordRepo);
        CouponInfoHandler couponInfoHandler = new CouponInfoHandler(couponRepo,userBalanceDomainService);
        couponInfo = couponInfoHandler;
        List<OrderValidator<? extends Order>> validators = List.of(
                new LimitOrderValidator(orderBookManager, priceLimitPolicy, liquidityPolicy),
                new MarketOrderValidator(orderBookManager),
                new ReservationOrderValidator(orderBookManager, liquidityPolicy)
        );
        userBalanceHandler = new UserBalanceHandler(tradingUserBalanceRepository, userBalanceDomainService);
        TradingCreateOrderUseCase createOrderUseCase = new TradingCreateOrderUseCaseImpl(createHandler,
                validators, userBalanceHandler, couponInfoHandler, feePolicy,orderRedis);
        TradingTrackUseCase trackUseCase = new TradingTrackUseCaseImpl(trackHandler
                , orderBookManager, marketDataTrackHandler,dtoMapper,bithumbApiPort);
        TradingUpdateUseCase updateUseCase = new TradingUpdateUseCaseImpl(updateHandler, trackHandler);

        tradingUpdateUseCase = updateUseCase;

        orderMatchProcessor = new OrderMatchProcessor(orderDomainService, tradeDomainService,
                tradeRecordRepo, userBalanceHandler);
        feeRateResolver = new FeeRateResolver(feePolicy, couponInfoHandler);
        orderExecutionChecker = new OrderExecutionChecker(orderDomainService);
        strategies = List.of(
                new LimitOrderMatchingStrategy(feeRateResolver, userBalanceHandler, orderExecutionChecker,
                        orderMatchProcessor, orderRepo, orderRedis),
                new MarketOrderMatchingStrategy(feeRateResolver, userBalanceHandler,
                        orderMatchProcessor, orderRepo),
                new ReservationOrderMatchingStrategy(feeRateResolver, orderExecutionChecker, userBalanceHandler,
                        orderMatchProcessor, orderRepo, orderRedis)
        );


        ExecuteOrderMatchingUseCase executeUseCase =
                new ExecuteOrderMatchingUseCaseImpl(orderBookManager, kafkaPublisher, strategies,userBalanceKafkaPublisher);

        return new TradingApplicationServiceImpl(
                createOrderUseCase, trackUseCase, dataMapper, updateUseCase, executeUseCase
        );
    }
}
