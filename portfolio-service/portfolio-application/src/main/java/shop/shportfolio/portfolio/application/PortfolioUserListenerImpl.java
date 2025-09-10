package shop.shportfolio.portfolio.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
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

@Slf4j
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
    @Transactional
    public void createPortfolio(UserId userId) {
        log.info("Creating portfolio user {}", userId.getValue());
        portfolioRepositoryPort.findPortfolioByUserId(userId.getValue()).ifPresent(portfolio -> {
            throw new PortfolioExistException(String.format("userId : %s is exist Portfolio.",
                    userId.getValue()));
        });
        Portfolio portfolio = portfolioDomainService.createPortfolio(new PortfolioId(UUID.randomUUID()),
                userId, CreatedAt.now(), UpdatedAt.now());
        portfolioRepositoryPort.savePortfolio(portfolio);
    }

    @Override
    @Transactional
    public void deletePortfolio(UserId userId) {
        log.info("Delete portfolio {}", userId.getValue());
        portfolioRepositoryPort.deletePortfolio(userId.getValue());
    }
}
