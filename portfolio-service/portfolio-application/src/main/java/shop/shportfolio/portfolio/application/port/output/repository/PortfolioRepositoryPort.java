package shop.shportfolio.portfolio.application.port.output.repository;

import shop.shportfolio.portfolio.domain.entity.*;

import java.util.Optional;
import java.util.UUID;

public interface PortfolioRepositoryPort {

    Optional<CryptoBalance> findCryptoBalanceByPortfolioIdAndMarketId(UUID portfolioId, String marketId);

    Optional<Portfolio> findPortfolioByPortfolioIdAndUserId(UUID portfolioId, UUID userId);

    Optional<Portfolio> findPortfolioByUserId(UUID userId);

    Portfolio savePortfolio(Portfolio portfolio);

    CurrencyBalance saveCurrencyBalance(CurrencyBalance currencyBalance);
    Optional<CurrencyBalance> findCurrencyBalanceByPortfolioIdAndUserId(UUID portfolioId);

    DepositWithdrawal saveDepositWithdrawal(DepositWithdrawal deposit);
}
