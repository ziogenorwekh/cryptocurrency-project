package shop.shportfolio.portfolio.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.portfolio.application.dto.BalanceKafkaResponse;
import shop.shportfolio.portfolio.application.handler.PortfolioUpdateHandler;
import shop.shportfolio.portfolio.application.port.input.kafka.PortfolioBalanceListener;

@Component
public class PortfolioBalanceListenerImpl implements PortfolioBalanceListener {

    private final PortfolioUpdateHandler portfolioUpdateHandler;

    @Autowired
    public PortfolioBalanceListenerImpl(PortfolioUpdateHandler portfolioUpdateHandler) {
        this.portfolioUpdateHandler = portfolioUpdateHandler;
    }

    @Override
    public void handleCurrencyBalanceChange(BalanceKafkaResponse balanceKafkaResponse) {
        portfolioUpdateHandler.updateCurrencyBalance(balanceKafkaResponse);
    }
}
