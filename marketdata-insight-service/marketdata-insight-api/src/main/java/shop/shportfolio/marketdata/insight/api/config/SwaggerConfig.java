package shop.shportfolio.marketdata.insight.api.config;

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
                        .title("Market Data & Insight API")
                        .description("시장 데이터 및 AI 인사이트 관련 전체 API 문서")
                        .version("v1.0.0"));
    }
}
