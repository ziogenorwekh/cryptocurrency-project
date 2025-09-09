package shop.shportfolio.user.infrastructure.database.jpa.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "shop.shportfolio.user.infrastructure.database.jpa.repository")
public class JpaConfig {
}
