package shop.shportfolio.portfolio.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.valueobject.UserId;
import shop.shportfolio.portfolio.application.exception.PortfolioNotFoundException;
import shop.shportfolio.portfolio.application.port.input.kafka.PortfolioUserKafkaListener;
import shop.shportfolio.portfolio.application.port.output.repository.PortfolioRepositoryPort;
import shop.shportfolio.portfolio.domain.entity.Portfolio;

@Component
public class PortfolioUserKafkaListenerImpl implements PortfolioUserKafkaListener {

    private final PortfolioRepositoryPort portfolioRepositoryPort;

    @Autowired
    public PortfolioUserKafkaListenerImpl(PortfolioRepositoryPort portfolioRepositoryPort) {
        this.portfolioRepositoryPort = portfolioRepositoryPort;
    }

    @Override
    public void deletePortfolio(UserId userId) {
        portfolioRepositoryPort.deletePortfolio(userId.getValue());
    }
}
