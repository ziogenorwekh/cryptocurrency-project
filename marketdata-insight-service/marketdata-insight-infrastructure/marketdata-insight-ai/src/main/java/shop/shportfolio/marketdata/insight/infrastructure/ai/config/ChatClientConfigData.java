package shop.shportfolio.marketdata.insight.infrastructure.ai.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "open-api-client")
public class ChatClientConfigData {

    @Value("${open.ai.apikey}")
    private String apiKey;
    @Value("${open.ai.baseurl}")
    private String baseUrl;
    @Value("${open.ai.model}")
    private String model;
    @Value("${open.ai.timeout}")
    private Long timeout;
    @Value("${open.ai.max-tries}")
    private Long maxTries;
    @Value("${open.ai.temperature}")
    private String temperature;
    @Value("${open.ai.max-tokens}")
    private int maxTokens;
}
