package shop.shportfolio.trading.container.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import shop.shportfolio.trading.domain.*;

@Configuration
public class TradeApplicationConfiguration {

    @Bean
    public OrderDomainService orderDomainService() {
        return new OrderDomainServiceImpl();
    }

    @Bean
    public TradeDomainService tradeDomainService() {
        return new TradeDomainServiceImpl();
    }
    @Bean
    public UserBalanceDomainService userBalanceDomainService() {
        return new UserBalanceDomainServiceImpl();
    }
}
