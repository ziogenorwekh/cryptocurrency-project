package shop.shportfolio.matching.container;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@EnableAsync
@EnableScheduling
@EntityScan(basePackages = "shop.shportfolio.trading.infrastructure.database.jpa.entity")
@EnableDiscoveryClient
@ComponentScan(basePackages = {"shop.shportfolio.matching","shop.shportfolio.common",
        "shop.shportfolio.trading.infrastructure.database"})
@SpringBootApplication
public class MatchingApplication {

    public static void main(String[] args) {
        SpringApplication.run(MatchingApplication.class, args);
    }
}
