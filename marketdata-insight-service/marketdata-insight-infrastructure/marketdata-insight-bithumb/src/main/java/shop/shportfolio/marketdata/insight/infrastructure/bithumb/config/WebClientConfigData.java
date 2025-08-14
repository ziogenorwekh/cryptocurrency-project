package shop.shportfolio.marketdata.insight.infrastructure.bithumb.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "bithumb-api-webclient")
public class WebClientConfigData {

    private String baseUrl;
    private long timeout;
}
