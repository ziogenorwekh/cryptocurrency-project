package shop.shportfolio.trading.application.test.helper;

import shop.shportfolio.trading.application.TradingApplicationServiceImpl;
import shop.shportfolio.trading.application.ports.input.usecase.TradingCreateOrderUseCase;
import shop.shportfolio.trading.application.ports.input.usecase.TradingTrackUseCase;
import shop.shportfolio.trading.application.saga.CancelOrderSaga;
import shop.shportfolio.trading.application.ports.output.kafka.*;
import shop.shportfolio.trading.application.ports.output.marketdata.BithumbApiPort;
import shop.shportfolio.trading.application.ports.input.usecase.impl.TradingCreateOrderUseCaseImpl;
import shop.shportfolio.trading.application.ports.input.usecase.impl.TradingTrackUseCaseImpl;
import shop.shportfolio.trading.application.saga.CancelOrderSagaImpl;
import shop.shportfolio.trading.application.handler.UserBalanceHandler;
import shop.shportfolio.trading.application.handler.create.TradingCreateHandler;
import shop.shportfolio.trading.application.handler.CouponInfoHandler;
import shop.shportfolio.trading.application.handler.track.MarketDataTrackHandler;
import shop.shportfolio.trading.application.handler.track.TradingTrackHandler;
import shop.shportfolio.trading.application.handler.update.TradingUpdateHandler;
import shop.shportfolio.trading.application.mapper.TradingDataMapper;
import shop.shportfolio.trading.application.mapper.TradingDtoMapper;
import shop.shportfolio.trading.application.policy.*;
import shop.shportfolio.trading.application.ports.input.*;
import shop.shportfolio.trading.application.ports.output.repository.*;
import shop.shportfolio.trading.application.orderbook.matching.FeeRateResolver;
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
    public CancelOrderSaga cancelOrderSaga;
    public OrderDomainService orderDomainService;
    public CouponInfoHandler couponInfo;
    public FeeRateResolver feeRateResolver;
//    public OrderExecutionChecker orderExecutionChecker;
//    public OrderMatchProcessor orderMatchProcessor;
    public UserBalanceHandler userBalanceHandler;
//    public OrderBookManager orderBookManager;
//    public OrderMatchingExecutor executeUseCase;
//    public List<OrderMatchingStrategy<? extends Order>> strategies;
    public TradingApplicationService createTradingApplicationService(
            TradingOrderRepositoryPort orderRepo,
            TradingTradeRecordRepositoryPort tradeRecordRepo,
            TradingMarketDataRepositoryPort marketRepo,
            TradingCouponRepositoryPort couponRepo,
            TradePublisher kafkaPublisher,
            TradingUserBalanceRepositoryPort tradingUserBalanceRepository,
            UserBalancePublisher userBalancePublisher,
            BithumbApiPort bithumbApiPort,
            MarketOrderCreatedPublisher marketOrderCreatedPublisher,
            ReservationOrderCreatedPublisher reservationOrderCreatedPublisher,
            LimitOrderCreatedPublisher limitOrderCreatedPublisher,
            LimitOrderCancelledPublisher limitOrderCancelledPublisher,
            ReservationOrderCancelledPublisher reservationOrderCancelledPublisher
    ) {
        userBalanceDomainService = new UserBalanceDomainServiceImpl();
        TradingDtoMapper dtoMapper = new TradingDtoMapper();
        TradingDataMapper dataMapper = new TradingDataMapper();
         orderDomainService = new OrderDomainServiceImpl();
        FeePolicy feePolicy = new DefaultFeePolicy();
        PriceLimitPolicy priceLimitPolicy = new DefaultPriceLimitPolicy();
        tradeDomainService = new TradeDomainServiceImpl();
//        orderBookManager = new OrderBookManager(orderDomainService,
//                orderRedis, tradeRecordRepo, marketRepo, tradeDomainService);

        TradingTrackHandler trackHandler = new TradingTrackHandler(orderRepo);
        TradingCreateHandler createHandler = new TradingCreateHandler(orderRepo, marketRepo, orderDomainService);
        TradingUpdateHandler updateHandler = new TradingUpdateHandler(orderRepo, orderDomainService);

        MarketDataTrackHandler marketDataTrackHandler = new MarketDataTrackHandler(
                marketRepo, tradeRecordRepo);
        CouponInfoHandler couponInfoHandler = new CouponInfoHandler(couponRepo,userBalanceDomainService);
        couponInfo = couponInfoHandler;
        List<OrderValidator<? extends Order>> validators = List.of(
                new LimitOrderValidator(bithumbApiPort, priceLimitPolicy),
                new MarketOrderValidator(bithumbApiPort),
                new ReservationOrderValidator(bithumbApiPort)
        );
        userBalanceHandler = new UserBalanceHandler(tradingUserBalanceRepository, userBalanceDomainService);
        TradingCreateOrderUseCase createOrderUseCase = new TradingCreateOrderUseCaseImpl(createHandler,
                validators, userBalanceHandler, couponInfoHandler, feePolicy
        );
        TradingTrackUseCase trackUseCase = new TradingTrackUseCaseImpl(trackHandler
                , marketDataTrackHandler,dtoMapper,bithumbApiPort);
        CancelOrderSaga updateUseCase = new CancelOrderSagaImpl(updateHandler,
                trackHandler, userBalanceHandler);

        cancelOrderSaga = updateUseCase;

//        orderMatchProcessor = new OrderMatchProcessor(orderDomainService, tradeDomainService,
//                tradeRecordRepo, userBalanceHandler);
//        feeRateResolver = new FeeRateResolver(feePolicy, couponInfoHandler);
//        orderExecutionChecker = new OrderExecutionChecker(orderDomainService);
//        strategies = List.of(
//                new LimitOrderMatchingStrategy(feeRateResolver, userBalanceHandler, orderExecutionChecker,
//                        orderMatchProcessor, orderRepo, orderRedis),
//                new MarketOrderMatchingStrategy(feeRateResolver, userBalanceHandler,
//                        orderMatchProcessor, orderRepo,orderRedis),
//                new ReservationOrderMatchingStrategy(feeRateResolver, orderExecutionChecker, userBalanceHandler,
//                        orderMatchProcessor, orderRepo, orderRedis)
//        );


//        executeUseCase =
//                new OrderMatchingExecutorImpl(orderBookManager, kafkaPublisher, strategies, userBalancePublisher);

        return new TradingApplicationServiceImpl(
                createOrderUseCase, trackUseCase, dataMapper, updateUseCase
                , marketOrderCreatedPublisher, reservationOrderCreatedPublisher, limitOrderCreatedPublisher
                , limitOrderCancelledPublisher, reservationOrderCancelledPublisher

        );
    }

//    public OrderMatchingExecutor getExecuteUseCase() {
//        return executeUseCase;
//    }

    public UserBalanceHandler getUserBalanceHandler() {
        return userBalanceHandler;
    }

    public FeeRateResolver getFeeRateResolver() {
        return feeRateResolver;
    }

    public TradeDomainService getTradeDomainService() {
        return tradeDomainService;
    }

    public OrderDomainService getOrderDomainService() {
        return orderDomainService;
    }
}
