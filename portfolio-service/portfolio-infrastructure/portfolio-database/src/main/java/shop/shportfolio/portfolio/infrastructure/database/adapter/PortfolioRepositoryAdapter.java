package shop.shportfolio.portfolio.infrastructure.database.adapter;

import org.springframework.stereotype.Repository;
import shop.shportfolio.portfolio.application.port.output.repository.PortfolioRepositoryPort;
import shop.shportfolio.portfolio.domain.entity.CryptoBalance;
import shop.shportfolio.portfolio.domain.entity.CurrencyBalance;
import shop.shportfolio.portfolio.domain.entity.DepositWithdrawal;
import shop.shportfolio.portfolio.domain.entity.Portfolio;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public class PortfolioRepositoryAdapter implements PortfolioRepositoryPort {

    @Override
    public Optional<CryptoBalance> findCryptoBalanceByPortfolioIdAndMarketId(UUID portfolioId, String marketId) {
        return Optional.empty();
    }

    @Override
    public Optional<Portfolio> findPortfolioByPortfolioIdAndUserId(UUID portfolioId, UUID userId) {
        return Optional.empty();
    }

    @Override
    public Optional<Portfolio> findPortfolioByUserId(UUID userId) {
        return Optional.empty();
    }

    @Override
    public Portfolio savePortfolio(Portfolio portfolio) {
        return null;
    }

    @Override
    public CurrencyBalance saveCurrencyBalance(CurrencyBalance currencyBalance) {
        return null;
    }

    @Override
    public CryptoBalance saveCryptoBalance(CryptoBalance cryptoBalance) {
        return null;
    }

    @Override
    public Optional<CurrencyBalance> findCurrencyBalanceByUserId(UUID userId) {
        return Optional.empty();
    }

    @Override
    public Optional<CurrencyBalance> findCurrencyBalanceByPortfolioId(UUID portfolioId) {
        return Optional.empty();
    }

    @Override
    public DepositWithdrawal saveDepositWithdrawal(DepositWithdrawal deposit) {
        return null;
    }

    @Override
    public List<CryptoBalance> findCryptoBalancesByPortfolioId(UUID portfolioId) {
        return List.of();
    }
}
