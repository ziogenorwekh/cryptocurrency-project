package shop.shportfolio.trading.infrastructure.redis.configuration;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "spring.redis")
public class RedisConfigData {

    private String host;
    private int port;
}
