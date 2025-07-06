package shop.shportfolio.coupon.infrastructure.toss.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "toss-api-uri")
public class URIConfigData {

    private String confirm;
    private String refund;
    private long timeout;
}
