package shop.shportfolio.portfolio.application.dto;

import lombok.Getter;
import shop.shportfolio.portfolio.domain.entity.CurrencyBalance;
import shop.shportfolio.portfolio.domain.entity.Portfolio;
import shop.shportfolio.portfolio.domain.event.WithdrawalCreatedEvent;
import shop.shportfolio.portfolio.domain.valueobject.PortfolioId;

@Getter
public class WithdrawalResultContext {

    private final WithdrawalCreatedEvent withdrawalCreatedEvent;
    private final PortfolioId portfolioId;
    public WithdrawalResultContext(WithdrawalCreatedEvent withdrawalCreatedEvent,PortfolioId portfolioId) {
        this.withdrawalCreatedEvent = withdrawalCreatedEvent;
        this.portfolioId = portfolioId;
    }
}
