package shop.shportfolio.trading.container;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@Slf4j
@EntityScan(basePackages = "shop.shportfolio.trading.infrastructure.database.jpa.entity")
@EnableDiscoveryClient
@ComponentScan(basePackages = {"shop.shportfolio.trading","shop.shportfolio.common"})
@SpringBootApplication
public class TradeApplication {
    public static void main(String[] args) {
        SpringApplication.run(TradeApplication.class, args);
    }
}
