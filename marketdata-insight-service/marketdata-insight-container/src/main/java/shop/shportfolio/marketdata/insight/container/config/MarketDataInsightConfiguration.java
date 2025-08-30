package shop.shportfolio.marketdata.insight.container.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import shop.shportfolio.marketdata.insight.domain.MarketDataInsightDomainService;
import shop.shportfolio.marketdata.insight.domain.MarketDataInsightDomainServiceImpl;

@Configuration
public class MarketDataInsightConfiguration {

    @Bean
    public MarketDataInsightDomainService marketDataInsightDomainService() {
        return new MarketDataInsightDomainServiceImpl();
    }
}
