package shop.shportfolio.portfolio.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Portfolio API")
                        .description("사용자 포트폴리오 관리 서비스 API 문서")
                        .version("v1.0.0"));
    }
}
