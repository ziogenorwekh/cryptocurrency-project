package shop.shportfolio.portfolio.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.valueobject.CreatedAt;
import shop.shportfolio.common.domain.valueobject.UpdatedAt;
import shop.shportfolio.common.domain.valueobject.UserId;
import shop.shportfolio.portfolio.application.exception.PortfolioExistException;
import shop.shportfolio.portfolio.application.port.input.kafka.PortfolioUserListener;
import shop.shportfolio.portfolio.application.port.output.repository.PortfolioRepositoryPort;
import shop.shportfolio.portfolio.domain.PortfolioDomainService;
import shop.shportfolio.portfolio.domain.entity.Portfolio;
import shop.shportfolio.portfolio.domain.valueobject.PortfolioId;

import java.util.UUID;

@Component
public class PortfolioUserListenerImpl implements PortfolioUserListener {

    private final PortfolioRepositoryPort portfolioRepositoryPort;
    private final PortfolioDomainService portfolioDomainService;
    @Autowired
    public PortfolioUserListenerImpl(PortfolioRepositoryPort portfolioRepositoryPort,
                                     PortfolioDomainService portfolioDomainService) {
        this.portfolioRepositoryPort = portfolioRepositoryPort;
        this.portfolioDomainService = portfolioDomainService;
    }

    @Override
    public void createPortfolio(UserId userId) {
        portfolioRepositoryPort.findPortfolioByUserId(userId.getValue()).ifPresent(portfolio -> {
            throw new PortfolioExistException(String.format("userId : %s is exist Portfolio.",
                    userId.getValue()));
        });
        Portfolio portfolio = portfolioDomainService.createPortfolio(new PortfolioId(UUID.randomUUID()),
                userId, CreatedAt.now(), UpdatedAt.now());
        portfolioRepositoryPort.savePortfolio(portfolio);
    }

    @Override
    public void deletePortfolio(UserId userId) {
        portfolioRepositoryPort.deletePortfolio(userId.getValue());
    }
}
