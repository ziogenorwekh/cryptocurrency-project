package shop.shportfolio.portfolio.container.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import shop.shportfolio.portfolio.domain.*;

@Configuration
public class PortfolioConfiguration {

    @Bean
    public PortfolioDomainService portfolioDomainService() {
        return new PortfolioDomainServiceImpl();
    }

    @Bean
    public PaymentDomainService paymentDomainService() {
        return new PaymentDomainServiceImpl();
    }
    @Bean
    public AssetChangeLogDomainService assetChangeLogDomainService() {
        return new AssetChangeLogDomainServiceImpl();
    }
    @Bean
    public DepositWithdrawalDomainService depositWithdrawalDomainService() {
        return new DepositWithdrawalDomainServiceImpl();
    }
}
