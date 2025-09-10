package shop.shportfolio.coupon.infrastructure.toss.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@ConfigurationPropertiesScan(basePackageClasses = WebClientConfigData.class)
@Configuration
public class WebClientConfig {

    private final WebClientConfigData webClientConfigData;

    @Autowired
    public WebClientConfig(WebClientConfigData webClientConfigData) {
        this.webClientConfigData = webClientConfigData;
    }

    @Bean
    public WebClient tossWebClient() {
        String authHeader = "Basic " + Base64.getEncoder()
                .encodeToString((webClientConfigData.getSecretKey() + ":").getBytes(StandardCharsets.UTF_8));
        return WebClient.builder()
                .baseUrl(webClientConfigData.getBaseUrl())
                .defaultHeader("Authorization", authHeader)
                .clientConnector(new ReactorClientHttpConnector())
                .build();
    }
}
