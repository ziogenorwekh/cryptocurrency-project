package shop.shportfolio.portfolio.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.valueobject.Money;
import shop.shportfolio.portfolio.application.dto.BalanceKafkaResponse;
import shop.shportfolio.portfolio.application.handler.PortfolioUpdateHandler;
import shop.shportfolio.portfolio.application.port.input.kafka.PortfolioBalanceKafkaListener;
import shop.shportfolio.portfolio.application.port.output.repository.PortfolioRepositoryPort;
import shop.shportfolio.portfolio.domain.PortfolioDomainService;
import shop.shportfolio.portfolio.domain.entity.CurrencyBalance;

import java.math.BigDecimal;
import java.util.Optional;

@Component
public class PortfolioBalanceKafkaListenerImpl implements PortfolioBalanceKafkaListener {

    private final PortfolioUpdateHandler portfolioUpdateHandler;

    @Autowired
    public PortfolioBalanceKafkaListenerImpl(PortfolioUpdateHandler portfolioUpdateHandler) {
        this.portfolioUpdateHandler = portfolioUpdateHandler;
    }

    @Override
    public void handleCurrencyBalanceChange(BalanceKafkaResponse balanceKafkaResponse) {
        portfolioUpdateHandler.updateCurrencyBalance(balanceKafkaResponse);
    }
}
