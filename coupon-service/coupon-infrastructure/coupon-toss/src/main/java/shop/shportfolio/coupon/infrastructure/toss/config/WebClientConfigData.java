package shop.shportfolio.coupon.infrastructure.toss.config;

import lombok.Data;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class WebClientConfigData {

    private String baseUrl;
    private String authorization;
    private String token;
}
