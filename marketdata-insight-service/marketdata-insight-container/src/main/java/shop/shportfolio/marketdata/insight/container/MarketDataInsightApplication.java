package shop.shportfolio.marketdata.insight.container;


import org.springframework.boot.SpringApplication;

@Slf4j
@EntityScan(basePackages = "shop.shportfolio.marketdata.insight.infrastructure.database")
@EnableDiscoveryClient
@ComponentScan(basePackages = "shop.shportfolio.marketdata.insight")
@SpringBootApplication
public class MarketDataInsightApplication {

    public static void main(String[] args) {
        SpringApplication.run(MarketDataInsightApplication.class, args);
    }
}
