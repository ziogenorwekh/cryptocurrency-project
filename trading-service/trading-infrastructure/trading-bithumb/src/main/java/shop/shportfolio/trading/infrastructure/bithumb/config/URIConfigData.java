package shop.shportfolio.trading.infrastructure.bithumb.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "bithumb-api-uri")
public class URIConfigData {

    private String bithumbCommonURI;
    private long timeout;
}
