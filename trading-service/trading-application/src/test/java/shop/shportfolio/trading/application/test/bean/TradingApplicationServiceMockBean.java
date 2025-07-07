package shop.shportfolio.trading.application.test.bean;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import shop.shportfolio.trading.application.*;
import shop.shportfolio.trading.application.facade.*;
import shop.shportfolio.trading.application.handler.OrderBookLimitMatchingEngine;
import shop.shportfolio.trading.application.handler.OrderBookManager;
import shop.shportfolio.trading.application.handler.OrderBookMarketMatchingEngine;
import shop.shportfolio.trading.application.handler.OrderBookReservationMatchingEngine;
import shop.shportfolio.trading.application.handler.create.TradingCreateHandler;
import shop.shportfolio.trading.application.handler.track.CouponInfoTrackHandler;
import shop.shportfolio.trading.application.handler.track.TradingTrackHandler;
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
import shop.shportfolio.trading.domain.TradingDomainService;
import shop.shportfolio.trading.domain.TradingDomainServiceImpl;

@Configuration
public class TradingApplicationServiceMockBean {

    @Bean
    public TradingDtoMapper tradingDtoMapper() {
        return new TradingDtoMapper();
    }

    @Bean
    public TradingOrderRepositoryPort tradingRepositoryAdapter() {
        return Mockito.mock(TradingOrderRepositoryPort.class);
    };

    @Bean
    public TradingCreateHandler tradingCreateHandler(){
        return new TradingCreateHandler(tradingRepositoryAdapter(),
                tradingMarketDataRepositoryAdapter()
                ,tradingDomainService());
    }

    @Bean
    public TradingCreateOrderUseCase tradingCreateOrderUseCase(){
        return new TradingCreateOrderFacade(tradingCreateHandler());
    }

    @Bean
    public MarketOrderExecutionUseCase marketOrderExecutionUseCase() {
        return new MarketOrderExecutionFacade(orderBookManageHandler(),
                temporaryKafkaProducer(),orderBookMarketMatchingEngine());
    }
    @Bean
    public TradingCouponRepositoryPort tradingCouponRepositoryAdapter() {
        return Mockito.mock(TradingCouponRepositoryPort.class);
    }

    @Bean
    public CouponInfoTrackHandler couponInfoTrackHandler() {
        return new CouponInfoTrackHandler(tradingCouponRepositoryAdapter());
    }

    @Bean
    public FeePolicy feePolicy() {
        return new DefaultFeePolicy();
    }

    @Bean
    public OrderBookMarketMatchingEngine orderBookMarketMatchingEngine() {
        return new OrderBookMarketMatchingEngine(
                tradingDomainService(),
                tradingRepositoryAdapter(),
                couponInfoTrackHandler(),
                feePolicy(),tradingTradeRecordRepositoryPort());
    }

    @Bean
    public TradingMarketDataRedisPort marketDataRedisPort() {
        return Mockito.mock(TradingMarketDataRedisPort.class);
    }
    @Bean
    public OrderBookManager orderBookManageHandler() {
        return new OrderBookManager(tradingDomainService(),tradingRepositoryAdapter()
        ,tradingDtoMapper(),tradingDataRedisRepositoryAdapter(),marketDataRedisPort(),
                tradingTradeRecordRepositoryPort(),tradingMarketDataRepositoryAdapter());
    }
    @Bean
    public TradingMarketDataRepositoryPort tradingMarketDataRepositoryAdapter() {
        return Mockito.mock(TradingMarketDataRepositoryPort.class);
    }


    @Bean
    public TradingTradeRecordRepositoryPort tradingTradeRecordRepositoryPort() {
        return Mockito.mock(TradingTradeRecordRepositoryPort.class);
    }

    @Bean
    public TradingTrackHandler  tradingTrackHandler() {
        return new TradingTrackHandler(tradingRepositoryAdapter(),tradingTradeRecordRepositoryPort());
    }

    @Bean
    public TradeKafkaPublisher temporaryKafkaProducer(){
        return Mockito.mock(TradeKafkaPublisher.class);
    }

    @Bean
    public TradingOrderRedisPort tradingDataRedisRepositoryAdapter() {
        return Mockito.mock(TradingOrderRedisPort.class);
    }

    @Bean
    public TradingDomainService tradingDomainService(){
        return new TradingDomainServiceImpl();
    }

    @Bean
    public TradingDataMapper tradingDataMapper(){
        return new TradingDataMapper();
    }

    @Bean
    public TradingTrackQueryUseCase tradingTrackQueryUseCase() {
        return new TradingTrackQueryFacade(tradingTrackHandler(), orderBookManageHandler(), tradingDtoMapper());
    }

    @Bean
    public TradingApplicationService  tradingApplicationService(){
        return new TradingApplicationServiceImpl(tradingCreateOrderUseCase(),marketOrderExecutionUseCase(),
                tradingTrackQueryUseCase()
                ,tradingDataMapper(),limitOrderExecutionUseCase());
    }
    @Bean
    public ReservationOrderExecutionUseCase reservationOrderExecutionUseCase() {
        return new ReservationOrderExecutionFacade(orderBookManageHandler(), temporaryKafkaProducer(),
                orderBookReservationMatchingEngine());
    }
    @Bean
    public OrderBookReservationMatchingEngine orderBookReservationMatchingEngine() {
        return new OrderBookReservationMatchingEngine(tradingDomainService(), tradingRepositoryAdapter(),
                couponInfoTrackHandler(), tradingDataRedisRepositoryAdapter(),
                feePolicy(), tradingTradeRecordRepositoryPort());
    }

    @Bean
    public LimitOrderExecutionUseCase limitOrderExecutionUseCase() {
        return new LimitOrderExecutionFacade(orderBookManageHandler(),temporaryKafkaProducer()
                ,orderBookLimitMatchingEngine());
    }

    @Bean
    public OrderBookLimitMatchingEngine orderBookLimitMatchingEngine() {
        return new OrderBookLimitMatchingEngine(tradingDomainService(),tradingRepositoryAdapter(),
                tradingTradeRecordRepositoryPort(),
                tradingDataRedisRepositoryAdapter(),couponInfoTrackHandler(),feePolicy());
    }
}
