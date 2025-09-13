package shop.shportfolio.matching.container.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import shop.shportfolio.matching.domain.MatchingDomainService;
import shop.shportfolio.matching.domain.MatchingDomainServiceImpl;

@Configuration
public class MatchingApplicationConfig {

    @Bean
    public MatchingDomainService matchingDomainService() {
        return new MatchingDomainServiceImpl();
    }
}
