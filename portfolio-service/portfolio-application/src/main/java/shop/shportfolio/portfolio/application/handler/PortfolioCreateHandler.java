package shop.shportfolio.portfolio.application.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.valueobject.CreatedAt;
import shop.shportfolio.common.domain.valueobject.UpdatedAt;
import shop.shportfolio.common.domain.valueobject.UserId;
import shop.shportfolio.portfolio.application.command.DepositCreateCommand;
import shop.shportfolio.portfolio.application.command.PortfolioCreateCommand;
import shop.shportfolio.portfolio.application.exception.PortfolioExistException;
import shop.shportfolio.portfolio.application.port.output.repository.PortfolioRepositoryPort;
import shop.shportfolio.portfolio.domain.PortfolioDomainService;
import shop.shportfolio.portfolio.domain.entity.Portfolio;
import shop.shportfolio.portfolio.domain.valueobject.PortfolioId;
import shop.shportfolio.portfolio.domain.valueobject.TotalAssetValue;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Component
public class PortfolioCreateHandler {

    private final PortfolioDomainService portfolioDomainService;
    private final PortfolioRepositoryPort portfolioRepositoryPort;

    @Autowired
    public PortfolioCreateHandler(PortfolioDomainService portfolioDomainService,
                                  PortfolioRepositoryPort portfolioRepositoryPort) {
        this.portfolioDomainService = portfolioDomainService;
        this.portfolioRepositoryPort = portfolioRepositoryPort;
    }


    public Portfolio createPortfolio(PortfolioCreateCommand command) {
        portfolioRepositoryPort.findPortfolioByUserId(command.getUserId()).ifPresent(portfolio -> {
            throw new PortfolioExistException(String.format("userId : %s is exist Portfolio.",
                    command.getUserId()));
        });
        Portfolio portfolio = portfolioDomainService.createPortfolio(new PortfolioId(UUID.randomUUID()),
                new UserId(command.getUserId()),
                TotalAssetValue.of(BigDecimal.ZERO), CreatedAt.now(), UpdatedAt.now());
        return portfolioRepositoryPort.savePortfolio(portfolio);
    }

    public Portfolio withdraw(DepositCreateCommand depositCreateCommand) {
        return null;
    }
}
