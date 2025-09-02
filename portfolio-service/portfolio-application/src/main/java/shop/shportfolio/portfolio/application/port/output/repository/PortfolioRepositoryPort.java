package shop.shportfolio.portfolio.application.port.output.repository;

import shop.shportfolio.portfolio.domain.entity.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PortfolioRepositoryPort {

    Optional<CryptoBalance> findCryptoBalanceByPortfolioIdAndMarketId(UUID portfolioId, String marketId);

    Optional<Portfolio> findPortfolioByUserId(UUID userId);

    Portfolio savePortfolio(Portfolio portfolio);

    CurrencyBalance saveCurrencyBalance(CurrencyBalance currencyBalance);

    CryptoBalance saveCryptoBalance(CryptoBalance cryptoBalance);

    Optional<CurrencyBalance> findCurrencyBalanceByUserId(UUID userId);
    Optional<CurrencyBalance> findCurrencyBalanceByPortfolioId(UUID portfolioId);

    DepositWithdrawal saveDepositWithdrawal(DepositWithdrawal deposit);

    List<CryptoBalance> findCryptoBalancesByPortfolioId(UUID portfolioId);

    void deletePortfolio(UUID userId);
}
