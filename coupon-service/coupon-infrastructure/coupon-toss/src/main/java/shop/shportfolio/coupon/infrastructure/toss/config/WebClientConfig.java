package shop.shportfolio.coupon.infrastructure.toss.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
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
                .defaultHeader(webClientConfigData.getAuthorization(),
                        webClientConfigData.getToken())
                .clientConnector(new ReactorClientHttpConnector())
                .build();
    }
}
