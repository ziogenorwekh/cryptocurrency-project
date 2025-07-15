package shop.shportfolio.trading.application.test.helper;

import shop.shportfolio.trading.application.TradingApplicationServiceImpl;
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
import shop.shportfolio.trading.application.ports.output.repository.TradingCouponRepositoryPort;
import shop.shportfolio.trading.application.ports.output.repository.TradingMarketDataRepositoryPort;
import shop.shportfolio.trading.application.ports.output.repository.TradingOrderRepositoryPort;
import shop.shportfolio.trading.application.ports.output.repository.TradingTradeRecordRepositoryPort;
import shop.shportfolio.trading.application.validator.LimitOrderValidator;
import shop.shportfolio.trading.application.validator.MarketOrderValidator;
import shop.shportfolio.trading.application.validator.ReservationOrderValidator;
import shop.shportfolio.trading.domain.TradingDomainService;
import shop.shportfolio.trading.domain.TradingDomainServiceImpl;
import shop.shportfolio.trading.domain.entity.Order;

import java.util.List;

public class TradingOrderTestHelper {


    public static TradingUpdateUseCase tradingUpdateUseCase;
    public static TradingDomainService tradingDomainService;
    public static CouponInfoTrackHandler couponInfo;

    public static TradingApplicationService createTradingApplicationService(
            TradingOrderRepositoryPort orderRepo,
            TradingTradeRecordRepositoryPort tradeRecordRepo,
            TradingOrderRedisPort orderRedis,
            TradingMarketDataRepositoryPort marketRepo,
            TradingMarketDataRedisPort marketDataRedis,
            TradingCouponRepositoryPort couponRepo,
            TradeKafkaPublisher kafkaPublisher,
            BithumbApiPort bithumbApiPort
    ) {
        TradingDtoMapper dtoMapper = new TradingDtoMapper();
        TradingDataMapper dataMapper = new TradingDataMapper();
        TradingDomainService domainService = new TradingDomainServiceImpl();
        tradingDomainService = domainService;
        FeePolicy feePolicy = new DefaultFeePolicy();
        LiquidityPolicy liquidityPolicy = new DefaultLiquidityPolicy();
        PriceLimitPolicy priceLimitPolicy = new DefaultPriceLimitPolicy();

        OrderBookManager orderBookManager = new OrderBookManager(domainService,
                orderRepo, dtoMapper, orderRedis, marketDataRedis, tradeRecordRepo, marketRepo);

        TradingTrackHandler trackHandler = new TradingTrackHandler(orderRepo, tradeRecordRepo, marketRepo);

        TradingCreateHandler createHandler = new TradingCreateHandler(orderRepo, marketRepo, domainService);
        TradingUpdateHandler updateHandler = new TradingUpdateHandler(orderRepo, domainService, orderRedis);

        MarketDataTrackHandler marketDataTrackHandler = new MarketDataTrackHandler(bithumbApiPort, dtoMapper,
                marketRepo, tradeRecordRepo);
        CouponInfoTrackHandler couponInfoTrackHandler = new CouponInfoTrackHandler(couponRepo);
        couponInfo = couponInfoTrackHandler;
        List<OrderValidator<? extends Order>> validators = List.of(
                new LimitOrderValidator(orderBookManager, priceLimitPolicy, liquidityPolicy),
                new MarketOrderValidator(orderBookManager),
                new ReservationOrderValidator(orderBookManager, liquidityPolicy)
        );

        TradingCreateOrderUseCase createOrderUseCase = new TradingCreateOrderFacade(createHandler, validators);
        TradingTrackUseCase trackUseCase = new TradingTrackFacade(trackHandler, orderBookManager, marketDataTrackHandler);
        TradingUpdateUseCase updateUseCase = new TradingUpdateFacade(updateHandler, trackHandler);

        tradingUpdateUseCase = updateUseCase;

        List<OrderMatchingStrategy<? extends Order>> strategies = List.of(
                new LimitOrderMatchingStrategy(domainService, orderRepo, tradeRecordRepo, orderRedis, couponInfoTrackHandler, feePolicy),
                new MarketOrderMatchingStrategy(domainService, orderRepo, tradeRecordRepo, couponInfoTrackHandler, feePolicy),
                new ReservationOrderMatchingStrategy(domainService, orderRepo, couponInfoTrackHandler, orderRedis, feePolicy, tradeRecordRepo)
        );

        ExecuteOrderMatchingUseCase executeUseCase =
                new ExecuteOrderMatchingFacade(orderBookManager, kafkaPublisher, strategies);

        return new TradingApplicationServiceImpl(
                createOrderUseCase, trackUseCase, dataMapper, updateUseCase, executeUseCase
        );
    }
}
