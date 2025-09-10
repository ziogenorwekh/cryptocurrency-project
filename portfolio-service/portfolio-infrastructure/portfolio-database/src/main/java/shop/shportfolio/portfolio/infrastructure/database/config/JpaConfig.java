package shop.shportfolio.portfolio.infrastructure.database.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(basePackages = "shop.shportfolio.portfolio.infrastructure.database.repository")
@Configuration
public class JpaConfig {
}
