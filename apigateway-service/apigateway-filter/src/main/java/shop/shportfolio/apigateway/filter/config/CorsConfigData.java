package shop.shportfolio.apigateway.filter.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@NoArgsConstructor
@AllArgsConstructor
public class CorsConfigData {
    private String accessControlAllowOrigin;
    private String accessControlAllowMethods;
    private String accessControlAllowHeaders;
    private String accessControlAllowCredentials;
}