package shop.shportfolio.coupon.infrastructure.database.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "shop.shportfolio.coupon.infrastructure.database.repository")
public class JpaConfig {
}
