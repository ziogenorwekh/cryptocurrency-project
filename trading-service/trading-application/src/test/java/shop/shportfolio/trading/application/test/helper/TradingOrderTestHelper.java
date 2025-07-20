package shop.shportfolio.trading.application.test.helper;

import shop.shportfolio.trading.application.TradingApplicationServiceImpl;
import shop.shportfolio.trading.application.facade.ExecuteOrderMatchingFacade;
import shop.shportfolio.trading.application.facade.TradingCreateOrderFacade;
import shop.shportfolio.trading.application.facade.TradingTrackFacade;
import shop.shportfolio.trading.application.facade.TradingUpdateFacade;
import shop.shportfolio.trading.application.handler.OrderBookManager;
import shop.shportfolio.trading.application.handler.UserBalanceHandler;
import shop.shportfolio.trading.application.handler.create.TradingCreateHandler;
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
import shop.shportfolio.trading.application.ports.output.marketdata.BithumbApiPort;
import shop.shportfolio.trading.application.ports.output.redis.TradingMarketDataRedisPort;
import shop.shportfolio.trading.application.ports.output.redis.TradingOrderRedisPort;
import shop.shportfolio.trading.application.ports.output.repository.*;
import shop.shportfolio.trading.application.validator.LimitOrderValidator;
import shop.shportfolio.trading.application.validator.MarketOrderValidator;
import shop.shportfolio.trading.application.validator.ReservationOrderValidator;
import shop.shportfolio.trading.domain.*;
import shop.shportfolio.trading.domain.entity.Order;

import java.util.List;

public class TradingOrderTestHelper {


    public static UserBalanceDomainService userBalanceDomainService;
    public static TradeDomainService tradeDomainService;
    public static TradingUpdateUseCase tradingUpdateUseCase;
    public static OrderDomainService orderDomainService;
    public static CouponInfoHandler couponInfo;

    public static TradingApplicationService createTradingApplicationService(
            TradingOrderRepositoryPort orderRepo,
            TradingTradeRecordRepositoryPort tradeRecordRepo,
            TradingOrderRedisPort orderRedis,
            TradingMarketDataRepositoryPort marketRepo,
            TradingMarketDataRedisPort marketDataRedis,
            TradingCouponRepositoryPort couponRepo,
            TradeKafkaPublisher kafkaPublisher,
            BithumbApiPort bithumbApiPort,
            TradingUserBalanceRepositoryPort tradingUserBalanceRepository
    ) {
        TradingDtoMapper dtoMapper = new TradingDtoMapper();
        TradingDataMapper dataMapper = new TradingDataMapper();
        OrderDomainService domainService = new OrderDomainServiceImpl();
        orderDomainService = domainService;
        FeePolicy feePolicy = new DefaultFeePolicy();
        LiquidityPolicy liquidityPolicy = new DefaultLiquidityPolicy();
        PriceLimitPolicy priceLimitPolicy = new DefaultPriceLimitPolicy();
        tradeDomainService = new TradeDomainServiceImpl();
        OrderBookManager orderBookManager = new OrderBookManager(domainService,
                dtoMapper, orderRedis, marketDataRedis, tradeRecordRepo, marketRepo, tradeDomainService);

        TradingTrackHandler trackHandler = new TradingTrackHandler(orderRepo, tradeRecordRepo, marketRepo);

        TradingCreateHandler createHandler = new TradingCreateHandler(orderRepo, marketRepo, domainService);
        TradingUpdateHandler updateHandler = new TradingUpdateHandler(orderRepo, domainService, orderRedis);

        MarketDataTrackHandler marketDataTrackHandler = new MarketDataTrackHandler(bithumbApiPort, dtoMapper,
                marketRepo, tradeRecordRepo);
        CouponInfoHandler couponInfoHandler = new CouponInfoHandler(couponRepo);
        couponInfo = couponInfoHandler;
        List<OrderValidator<? extends Order>> validators = List.of(
                new LimitOrderValidator(orderBookManager, priceLimitPolicy, liquidityPolicy),
                new MarketOrderValidator(orderBookManager),
                new ReservationOrderValidator(orderBookManager, liquidityPolicy)
        );
        UserBalanceHandler userBalanceHandler = new UserBalanceHandler(tradingUserBalanceRepository, userBalanceDomainService);
        TradingCreateOrderUseCase createOrderUseCase = new TradingCreateOrderFacade(createHandler,
                validators, userBalanceHandler, feePolicy);
        TradingTrackUseCase trackUseCase = new TradingTrackFacade(trackHandler, orderBookManager, marketDataTrackHandler);
        TradingUpdateUseCase updateUseCase = new TradingUpdateFacade(updateHandler, trackHandler);

        tradingUpdateUseCase = updateUseCase;
        userBalanceDomainService = new UserBalanceDomainServiceImpl();
        List<OrderMatchingStrategy<? extends Order>> strategies = List.of(
                new LimitOrderMatchingStrategy(userBalanceDomainService, tradeDomainService, domainService,
                        orderRepo, tradeRecordRepo, orderRedis, couponInfoHandler, feePolicy, tradingUserBalanceRepository),
                new MarketOrderMatchingStrategy(userBalanceDomainService, tradeDomainService, domainService,
                        orderRepo, tradeRecordRepo, couponInfoHandler, feePolicy, tradingUserBalanceRepository),
                new ReservationOrderMatchingStrategy(userBalanceDomainService, tradeDomainService, domainService,
                        orderRepo, couponInfoHandler, orderRedis, feePolicy, tradeRecordRepo, tradingUserBalanceRepository)
        );

        ExecuteOrderMatchingUseCase executeUseCase =
                new ExecuteOrderMatchingFacade(orderBookManager, kafkaPublisher, strategies);

        return new TradingApplicationServiceImpl(
                createOrderUseCase, trackUseCase, dataMapper, updateUseCase, executeUseCase
        );
    }
}
