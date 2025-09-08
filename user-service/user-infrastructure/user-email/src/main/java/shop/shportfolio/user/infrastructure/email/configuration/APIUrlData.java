package shop.shportfolio.user.infrastructure.email.configuration;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
public class APIUrlData {

    @Value("${reset.password.url}")
    private String resetPasswordUrl;
}
