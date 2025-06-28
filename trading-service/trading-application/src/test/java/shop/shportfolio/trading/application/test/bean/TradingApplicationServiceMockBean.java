package shop.shportfolio.trading.application.test.bean;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import shop.shportfolio.trading.application.MarketOrderExecutionFacade;
import shop.shportfolio.trading.application.TradingApplicationServiceImpl;
import shop.shportfolio.trading.application.TradingCreateOrderFacade;
import shop.shportfolio.trading.application.TradingTrackQueryFacade;
import shop.shportfolio.trading.application.handler.OrderBookManager;
import shop.shportfolio.trading.application.handler.OrderBookMarketMatchingEngine;
import shop.shportfolio.trading.application.handler.create.TradingCreateHandler;
import shop.shportfolio.trading.application.handler.track.TradingTrackHandler;
import shop.shportfolio.trading.application.mapper.TradingDataMapper;
import shop.shportfolio.trading.application.mapper.TradingDtoMapper;
import shop.shportfolio.trading.application.ports.input.MarketOrderExecutionUseCase;
import shop.shportfolio.trading.application.ports.input.TradingApplicationService;
import shop.shportfolio.trading.application.ports.input.TradingCreateOrderUseCase;
import shop.shportfolio.trading.application.ports.input.TradingTrackQueryUseCase;
import shop.shportfolio.trading.application.ports.output.kafka.TemporaryKafkaPublisher;
import shop.shportfolio.trading.application.ports.output.redis.MarketDataRedisAdapter;
import shop.shportfolio.trading.application.ports.output.repository.TradingRepositoryAdapter;
import shop.shportfolio.trading.domain.TradingDomainService;
import shop.shportfolio.trading.domain.TradingDomainServiceImpl;

@Configuration
public class TradingApplicationServiceMockBean {

    @Bean
    public TradingDtoMapper tradingDtoMapper() {
        return new TradingDtoMapper();
    }

    @Bean
    public TradingRepositoryAdapter tradingRepositoryAdapter() {
        return Mockito.mock(TradingRepositoryAdapter.class);
    };

    @Bean
    public TradingCreateHandler tradingCreateHandler(){
        return new TradingCreateHandler(tradingRepositoryAdapter(),tradingDomainService());
    }

    @Bean
    public TradingCreateOrderUseCase tradingCreateOrderUseCase(){
        return new TradingCreateOrderFacade(tradingCreateHandler(),
                tradingDataRedisRepositoryAdapter());
    }

    @Bean
    public MarketOrderExecutionUseCase marketOrderExecutionUseCase() {
        return new MarketOrderExecutionFacade(orderBookManageHandler(),
                temporaryKafkaProducer(),orderBookMarketMatchingEngine());
    }

    @Bean
    public OrderBookMarketMatchingEngine orderBookMarketMatchingEngine() {
        return new OrderBookMarketMatchingEngine(tradingDomainService(),tradingRepositoryAdapter());
    }


    @Bean
    public OrderBookManager orderBookManageHandler() {
        return new OrderBookManager(tradingDomainService(),tradingRepositoryAdapter()
        ,tradingDtoMapper(),tradingDataRedisRepositoryAdapter());
    }
    @Bean
    public TradingTrackHandler  tradingTrackHandler() {
        return new TradingTrackHandler(tradingRepositoryAdapter(),
                tradingDomainService());
    }

    @Bean
    public TemporaryKafkaPublisher temporaryKafkaProducer(){
        return Mockito.mock(TemporaryKafkaPublisher.class);
    }

    @Bean
    public MarketDataRedisAdapter tradingDataRedisRepositoryAdapter() {
        return Mockito.mock(MarketDataRedisAdapter.class);
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
                ,tradingDataMapper());
    }
}
