package shop.shportfolio.marketdata.insight.container;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@Slf4j
@EnableAsync
@EntityScan(basePackages = "shop.shportfolio.marketdata.insight.infrastructure.database.jpa.entity")
@EnableDiscoveryClient
@ComponentScan(basePackages = "shop.shportfolio.marketdata.insight")
@SpringBootApplication
public class MarketDataInsightApplication {

    public static void main(String[] args) {
        SpringApplication.run(MarketDataInsightApplication.class, args);
    }
}
