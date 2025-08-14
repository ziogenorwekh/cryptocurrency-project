package shop.shportfolio.trading.infrastructure.bithumb.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableConfigurationProperties(WebClientConfigData.class)
public class WebClientConfig {

    private final WebClientConfigData webClientConfigData;

    @Autowired
    public WebClientConfig(WebClientConfigData webClientConfigData) {
        this.webClientConfigData = webClientConfigData;
    }

    @Bean
    public WebClient tossWebClient() {
        return WebClient.builder()
                .baseUrl(webClientConfigData.getBaseUrl())
                .clientConnector(new ReactorClientHttpConnector())
                .build();
    }
}
