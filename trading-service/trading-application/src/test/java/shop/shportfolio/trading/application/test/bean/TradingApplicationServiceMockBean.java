package shop.shportfolio.trading.application.test.bean;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import shop.shportfolio.trading.application.TradingApplicationServiceImpl;
import shop.shportfolio.trading.application.TradingCreateOrderFacade;
import shop.shportfolio.trading.application.handler.TradingCreateHandler;
import shop.shportfolio.trading.application.mapper.TradingDataMapper;
import shop.shportfolio.trading.application.ports.input.TradingApplicationService;
import shop.shportfolio.trading.application.ports.input.TradingCreateOrderUseCase;
import shop.shportfolio.trading.application.ports.output.repository.TradingRepositoryAdapter;
import shop.shportfolio.trading.domain.TradingDomainService;
import shop.shportfolio.trading.domain.TradingDomainServiceImpl;

@Configuration
public class TradingApplicationServiceMockBean {


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
        return new TradingCreateOrderFacade(tradingCreateHandler());
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
    public TradingApplicationService  tradingApplicationService(){
        return new TradingApplicationServiceImpl(tradingCreateOrderUseCase(),tradingDataMapper());
    }
}
