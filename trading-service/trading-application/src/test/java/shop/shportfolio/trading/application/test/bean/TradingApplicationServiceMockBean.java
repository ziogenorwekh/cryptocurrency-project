package shop.shportfolio.trading.application.test.bean;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import shop.shportfolio.trading.application.*;
import shop.shportfolio.trading.application.facade.LimitOrderExecutionFacade;
import shop.shportfolio.trading.application.facade.MarketOrderExecutionFacade;
import shop.shportfolio.trading.application.facade.TradingCreateOrderFacade;
import shop.shportfolio.trading.application.facade.TradingTrackQueryFacade;
import shop.shportfolio.trading.application.handler.OrderBookLimitMatchingEngine;
import shop.shportfolio.trading.application.handler.OrderBookManager;
import shop.shportfolio.trading.application.handler.OrderBookMarketMatchingEngine;
import shop.shportfolio.trading.application.handler.create.TradingCreateHandler;
import shop.shportfolio.trading.application.handler.track.CouponInfoTrackHandler;
import shop.shportfolio.trading.application.handler.track.TradingTrackHandler;
import shop.shportfolio.trading.application.mapper.TradingDataMapper;
import shop.shportfolio.trading.application.mapper.TradingDtoMapper;
import shop.shportfolio.trading.application.policy.DefaultFeePolicy;
import shop.shportfolio.trading.application.policy.FeePolicy;
import shop.shportfolio.trading.application.ports.input.*;
import shop.shportfolio.trading.application.ports.output.kafka.TradeKafkaPublisher;
import shop.shportfolio.trading.application.ports.output.redis.MarketDataRedisPort;
import shop.shportfolio.trading.application.ports.output.repository.TradingCouponRepositoryPort;
import shop.shportfolio.trading.application.ports.output.repository.TradingRepositoryPort;
import shop.shportfolio.trading.domain.TradingDomainService;
import shop.shportfolio.trading.domain.TradingDomainServiceImpl;

@Configuration
public class TradingApplicationServiceMockBean {

    @Bean
    public TradingDtoMapper tradingDtoMapper() {
        return new TradingDtoMapper();
    }

    @Bean
    public TradingRepositoryPort tradingRepositoryAdapter() {
        return Mockito.mock(TradingRepositoryPort.class);
    };

    @Bean
    public TradingCreateHandler tradingCreateHandler(){
        return new TradingCreateHandler(tradingRepositoryAdapter(),tradingDomainService());
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
                feePolicy());
    }

    @Bean
    public OrderBookManager orderBookManageHandler() {
        return new OrderBookManager(tradingDomainService(),tradingRepositoryAdapter()
        ,tradingDtoMapper(),tradingDataRedisRepositoryAdapter());
    }
    @Bean
    public TradingTrackHandler  tradingTrackHandler() {
        return new TradingTrackHandler(tradingRepositoryAdapter());
    }

    @Bean
    public TradeKafkaPublisher temporaryKafkaProducer(){
        return Mockito.mock(TradeKafkaPublisher.class);
    }

    @Bean
    public MarketDataRedisPort tradingDataRedisRepositoryAdapter() {
        return Mockito.mock(MarketDataRedisPort.class);
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
    public LimitOrderExecutionUseCase limitOrderExecutionUseCase() {
        return new LimitOrderExecutionFacade(orderBookManageHandler(),temporaryKafkaProducer()
                ,orderBookLimitMatchingEngine());
    }

    @Bean
    public OrderBookLimitMatchingEngine orderBookLimitMatchingEngine() {
        return new OrderBookLimitMatchingEngine(tradingDomainService(),tradingRepositoryAdapter(),
                tradingDataRedisRepositoryAdapter(),couponInfoTrackHandler(),feePolicy());
    }
}
