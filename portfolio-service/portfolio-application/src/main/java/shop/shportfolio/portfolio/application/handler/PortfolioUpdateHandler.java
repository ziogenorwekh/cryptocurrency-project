package shop.shportfolio.portfolio.application.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.portfolio.application.port.output.repository.PortfolioRepositoryPort;
import shop.shportfolio.portfolio.domain.PortfolioDomainService;
import shop.shportfolio.portfolio.domain.entity.Portfolio;

@Slf4j
@Component
public class PortfolioUpdateHandler {

    private final PortfolioRepositoryPort portfolioRepositoryPort;
    private final PortfolioDomainService portfolioDomainService;

    @Autowired
    public PortfolioUpdateHandler(PortfolioRepositoryPort portfolioRepositoryPort, PortfolioDomainService portfolioDomainService) {
        this.portfolioRepositoryPort = portfolioRepositoryPort;
        this.portfolioDomainService = portfolioDomainService;
    }


}
