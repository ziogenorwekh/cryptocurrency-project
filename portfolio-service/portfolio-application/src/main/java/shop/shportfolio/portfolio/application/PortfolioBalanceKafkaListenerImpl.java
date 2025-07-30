package shop.shportfolio.portfolio.application;

import org.springframework.stereotype.Component;
import shop.shportfolio.portfolio.application.dto.BalanceKafkaResponse;
import shop.shportfolio.portfolio.application.port.input.kafka.PortfolioBalanceKafkaListener;
import shop.shportfolio.portfolio.application.port.output.repository.PortfolioRepositoryPort;
import shop.shportfolio.portfolio.domain.PortfolioDomainService;

@Component
public class PortfolioBalanceKafkaListenerImpl implements PortfolioBalanceKafkaListener {

    private final PortfolioDomainService portfolioDomainService;
    private final PortfolioRepositoryPort  portfolioRepositoryPort;

    public PortfolioBalanceKafkaListenerImpl(PortfolioDomainService portfolioDomainService, PortfolioRepositoryPort portfolioRepositoryPort) {
        this.portfolioDomainService = portfolioDomainService;
        this.portfolioRepositoryPort = portfolioRepositoryPort;
    }

    @Override
    public void handleCurrencyBalanceChange(BalanceKafkaResponse balanceKafkaResponse) {

    }
}
