package shop.shportfolio.portfolio.application.dto;

import lombok.Getter;
import shop.shportfolio.portfolio.domain.entity.DepositWithdrawal;
import shop.shportfolio.portfolio.domain.valueobject.PortfolioId;

@Getter
public class WithdrawalSagaContext {
    private final DepositWithdrawal withdrawal;
    private final PortfolioId portfolioId;

    public WithdrawalSagaContext(DepositWithdrawal withdrawal, PortfolioId portfolioId) {
        this.withdrawal = withdrawal;
        this.portfolioId = portfolioId;
    }
}
