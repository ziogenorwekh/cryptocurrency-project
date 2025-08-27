//package shop.shportfolio.trading.application.test.bean;
//
//import org.mockito.Mockito;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import shop.shportfolio.trading.application.*;
//import shop.shportfolio.trading.application.facade.*;
//import shop.shportfolio.trading.application.handler.OrderBookLimitMatchingEngine;
//import shop.shportfolio.trading.application.orderbook.manager.OrderBookManager;
//import shop.shportfolio.trading.application.handler.OrderBookMarketMatchingEngine;
//import shop.shportfolio.trading.application.handler.OrderBookReservationMatchingEngine;
//import shop.shportfolio.trading.application.handler.create.TradingCreateHandler;
//import shop.shportfolio.trading.application.handler.matching.strategy.LimitOrderMatchingStrategy;
//import shop.shportfolio.trading.application.handler.matching.strategy.MarketOrderMatchingStrategy;
//import shop.shportfolio.trading.application.handler.matching.strategy.OrderMatchingStrategy;
//import shop.shportfolio.trading.application.handler.matching.strategy.ReservationOrderMatchingStrategy;
//import shop.shportfolio.trading.application.handler.track.CouponInfoTrackHandler;
//import shop.shportfolio.trading.application.handler.track.TradingTrackHandler;
//import shop.shportfolio.trading.application.handler.update.TradingUpdateHandler;
//import shop.shportfolio.trading.application.mapper.TradingDataMapper;
//import shop.shportfolio.trading.application.mapper.TradingDtoMapper;
//import shop.shportfolio.trading.application.policy.DefaultFeePolicy;
//import shop.shportfolio.trading.application.policy.FeePolicy;
//import shop.shportfolio.trading.application.ports.input.*;
//import shop.shportfolio.trading.application.ports.output.kafka.TradeKafkaPublisher;
//import shop.shportfolio.trading.application.ports.output.redis.TradingMarketDataRedisPort;
//import shop.shportfolio.trading.application.ports.output.redis.TradingOrderRedisPort;
//import shop.shportfolio.trading.application.ports.output.repository.TradingCouponRepositoryPort;
//import shop.shportfolio.trading.application.ports.output.repository.TradingMarketDataRepositoryPort;
//import shop.shportfolio.trading.application.ports.output.repository.TradingOrderRepositoryPort;
//import shop.shportfolio.trading.application.ports.output.repository.TradingTradeRecordRepositoryPort;
//import shop.shportfolio.trading.domain.TradingDomainService;
//import shop.shportfolio.trading.domain.TradingDomainServiceImpl;
//import shop.shportfolio.trading.domain.entity.Order;
//
//import java.util.List;
//
//@Configuration
//public class TradingApplicationServiceMockBean {
//
//    @Bean
//    public TradingDtoMapper tradingDtoMapper() {
//        return new TradingDtoMapper();
//    }
//
//    @Bean
//    public TradingOrderRepositoryPort tradingRepositoryAdapter() {
//        return Mockito.mock(TradingOrderRepositoryPort.class);
//    }
//
//    @Bean
//    public TradingCreateHandler tradingCreateHandler() {
//        return new TradingCreateHandler(tradingRepositoryAdapter(),
//                tradingMarketDataRepositoryAdapter()
//                , tradingDomainService());
//    }
//
//    @Bean
//    public TradingCreateOrderUseCase tradingCreateOrderUseCase() {
//        return new TradingCreateOrderFacade(tradingCreateHandler());
//    }
//
//    @Bean
//    public TradingCouponRepositoryPort tradingCouponRepositoryAdapter() {
//        return Mockito.mock(TradingCouponRepositoryPort.class);
//    }
//
//    @Bean
//    public CouponInfoTrackHandler couponInfoTrackHandler() {
//        return new CouponInfoTrackHandler(tradingCouponRepositoryAdapter());
//    }
//
//    @Bean
//    public FeePolicy feePolicy() {
//        return new DefaultFeePolicy();
//    }
//
//    @Bean
//    public OrderBookMarketMatchingEngine orderBookMarketMatchingEngine() {
//        return new OrderBookMarketMatchingEngine(
//                tradingDomainService(),
//                tradingRepositoryAdapter(),
//                couponInfoTrackHandler(),
//                feePolicy(), tradingTradeRecordRepositoryPort());
//    }
//
//    @Bean
//    public TradingMarketDataRedisPort marketDataRedisPort() {
//        return Mockito.mock(TradingMarketDataRedisPort.class);
//    }
//
//    @Bean
//    public OrderBookManager orderBookManageHandler() {
//        return new OrderBookManager(tradingDomainService(), tradingRepositoryAdapter()
//                , tradingDtoMapper(), tradingDataRedisRepositoryAdapter(), marketDataRedisPort(),
//                tradingTradeRecordRepositoryPort(), tradingMarketDataRepositoryAdapter());
//    }
//
//    @Bean
//    public TradingMarketDataRepositoryPort tradingMarketDataRepositoryAdapter() {
//        return Mockito.mock(TradingMarketDataRepositoryPort.class);
//    }
//
//
//    @Bean
//    public TradingTradeRecordRepositoryPort tradingTradeRecordRepositoryPort() {
//        return Mockito.mock(TradingTradeRecordRepositoryPort.class);
//    }
//
//    @Bean
//    public TradingTrackHandler tradingTrackHandler() {
//        return new TradingTrackHandler(tradingRepositoryAdapter(), tradingTradeRecordRepositoryPort(),tradingMarketDataRepositoryAdapter());
//    }
//
//    @Bean
//    public TradeKafkaPublisher temporaryKafkaProducer() {
//        return Mockito.mock(TradeKafkaPublisher.class);
//    }
//
//    @Bean
//    public TradingOrderRedisPort tradingDataRedisRepositoryAdapter() {
//        return Mockito.mock(TradingOrderRedisPort.class);
//    }
//
//    @Bean
//    public TradingDomainService tradingDomainService() {
//        return new TradingDomainServiceImpl();
//    }
//
//    @Bean
//    public TradingDataMapper tradingDataMapper() {
//        return new TradingDataMapper();
//    }
//
//    @Bean
//    public TradingTrackUseCase tradingTrackQueryUseCase() {
//        return new TradingTrackFacade(tradingTrackHandler(), orderBookManageHandler());
//    }
//
//    @Bean
//    public TradingApplicationService tradingApplicationService() {
//        return new TradingApplicationServiceImpl(tradingCreateOrderUseCase(), tradingTrackQueryUseCase(),
//                tradingDataMapper(), tradingUpdateUseCase(), executeOrderUseCase());
//    }
//
//    @Bean
//    public TradingUpdateUseCase tradingUpdateUseCase() {
//        return new TradingUpdateFacade(tradingUpdateHandler(), tradingTrackHandler());
//    }
//
//    @Bean
//    public TradingUpdateHandler tradingUpdateHandler() {
//        return new TradingUpdateHandler(tradingRepositoryAdapter(), tradingDomainService(),
//                tradingDataRedisRepositoryAdapter());
//    }
//
//    @Bean
//    public ExecuteOrderMatchingUseCase executeOrderUseCase() {
//        List<OrderMatchingStrategy<? extends Order>> strategies = List.of(
//                limitOrderMatchingStrategy(), marketOrderMatchingStrategy(), reservationOrderMatchingStrategy()
//        );
//        return new ExecuteOrderMatchingFacade(orderBookManageHandler(), temporaryKafkaProducer(), strategies);
//    }
//
//    @Bean
//    public LimitOrderMatchingStrategy limitOrderMatchingStrategy() {
//        return new LimitOrderMatchingStrategy(tradingDomainService(), tradingRepositoryAdapter(),
//                tradingTradeRecordRepositoryPort(),
//                tradingDataRedisRepositoryAdapter(), couponInfoTrackHandler(), feePolicy());
//    }
//
//    @Bean
//    public MarketOrderMatchingStrategy marketOrderMatchingStrategy() {
//        return new MarketOrderMatchingStrategy(tradingDomainService(), tradingRepositoryAdapter(),
//                tradingTradeRecordRepositoryPort(),
//                couponInfoTrackHandler(), feePolicy());
//    }
//
//    @Bean
//    public ReservationOrderMatchingStrategy reservationOrderMatchingStrategy() {
//        return new ReservationOrderMatchingStrategy(tradingDomainService(), tradingRepositoryAdapter(),
//                couponInfoTrackHandler(),
//                tradingDataRedisRepositoryAdapter(), feePolicy(), tradingTradeRecordRepositoryPort());
//    }
//
//    @Bean
//    public OrderBookReservationMatchingEngine orderBookReservationMatchingEngine() {
//        return new OrderBookReservationMatchingEngine(tradingDomainService(), tradingRepositoryAdapter(),
//                couponInfoTrackHandler(), tradingDataRedisRepositoryAdapter(),
//                feePolicy(), tradingTradeRecordRepositoryPort());
//    }
//
//    @Bean
//    public OrderBookLimitMatchingEngine orderBookLimitMatchingEngine() {
//        return new OrderBookLimitMatchingEngine(tradingDomainService(), tradingRepositoryAdapter(),
//                tradingTradeRecordRepositoryPort(),
//                tradingDataRedisRepositoryAdapter(), couponInfoTrackHandler(), feePolicy());
//    }
//}
