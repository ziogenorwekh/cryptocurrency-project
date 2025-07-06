package shop.shportfolio.coupon.container;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@Slf4j
@EntityScan(basePackages = "store.shportfolio.coupon.infrastructure.database")
@EnableDiscoveryClient
@ComponentScan(basePackages = {"shop.shportfolio.coupon","shop.shportfolio.common"})
@SpringBootApplication
public class CouponApplication {

    public static void main(String[] args) {
        SpringApplication.run(CouponApplication.class, args);
    }
}
