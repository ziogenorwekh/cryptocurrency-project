package shop.shportfolio.coupon.infrastructure.toss.config;

import lombok.Data;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class URIConfigData {

    private String confirm;
    private String refund;
    private long timeout;
}
