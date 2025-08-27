package shop.shportfolio.marketdata.insight.container.config;

@Configuration
public class MarketDataInsightConfiguration {

    @Bean
    public MarketDataInsightDomainService marketDataInsightDomainService() {
        return new MarketDataInsightDomainServiceImpl();
    }
}
