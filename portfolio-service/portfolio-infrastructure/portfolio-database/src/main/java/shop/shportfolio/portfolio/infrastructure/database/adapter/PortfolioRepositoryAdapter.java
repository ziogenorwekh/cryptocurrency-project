package shop.shportfolio.portfolio.infrastructure.database.adapter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import shop.shportfolio.portfolio.application.port.output.repository.PortfolioRepositoryPort;
import shop.shportfolio.portfolio.domain.entity.CryptoBalance;
import shop.shportfolio.portfolio.domain.entity.CurrencyBalance;
import shop.shportfolio.portfolio.domain.entity.DepositWithdrawal;
import shop.shportfolio.portfolio.domain.entity.Portfolio;
import shop.shportfolio.portfolio.infrastructure.database.entity.CryptoBalanceEntity;
import shop.shportfolio.portfolio.infrastructure.database.entity.CurrencyBalanceEntity;
import shop.shportfolio.portfolio.infrastructure.database.entity.DepositWithdrawalEntity;
import shop.shportfolio.portfolio.infrastructure.database.entity.PortfolioEntity;
import shop.shportfolio.portfolio.infrastructure.database.mapper.PortfolioDataAccessMapper;
import shop.shportfolio.portfolio.infrastructure.database.repository.CryptoBalanceJpaRepository;
import shop.shportfolio.portfolio.infrastructure.database.repository.CurrencyBalanceJpaRepository;
import shop.shportfolio.portfolio.infrastructure.database.repository.DepositWithdrawalJpaRepository;
import shop.shportfolio.portfolio.infrastructure.database.repository.PortfolioJpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class PortfolioRepositoryAdapter implements PortfolioRepositoryPort {

    private final CryptoBalanceJpaRepository cryptoBalanceJpaRepository;
    private final CurrencyBalanceJpaRepository currencyBalanceJpaRepository;
    private final DepositWithdrawalJpaRepository depositWithdrawalJpaRepository;
    private final PortfolioJpaRepository portfolioJpaRepository;
    private final PortfolioDataAccessMapper portfolioDataAccessMapper;

    @Autowired
    public PortfolioRepositoryAdapter(CryptoBalanceJpaRepository cryptoBalanceJpaRepository,
                                      CurrencyBalanceJpaRepository currencyBalanceJpaRepository,
                                      DepositWithdrawalJpaRepository depositWithdrawalJpaRepository,
                                      PortfolioJpaRepository portfolioJpaRepository,
                                      PortfolioDataAccessMapper portfolioDataAccessMapper) {
        this.cryptoBalanceJpaRepository = cryptoBalanceJpaRepository;
        this.currencyBalanceJpaRepository = currencyBalanceJpaRepository;
        this.depositWithdrawalJpaRepository = depositWithdrawalJpaRepository;
        this.portfolioJpaRepository = portfolioJpaRepository;
        this.portfolioDataAccessMapper = portfolioDataAccessMapper;
    }

    @Override
    public Optional<CryptoBalance> findCryptoBalanceByPortfolioIdAndMarketId(UUID portfolioId, String marketId) {
        return cryptoBalanceJpaRepository.findCryptoBalanceEntityByPortfolioIdAndMarketId(portfolioId, marketId)
                .map(portfolioDataAccessMapper::cryptoBalanceEntityToCryptoBalance);
    }

    @Override
    public Optional<Portfolio> findPortfolioByUserId(UUID userId) {
        return portfolioJpaRepository.findPortfolioEntityByUserId(userId)
                .map(portfolioDataAccessMapper::portfolioEntityToPortfolio);
    }

    @Override
    public Portfolio savePortfolio(Portfolio portfolio) {
        PortfolioEntity portfolioEntity = portfolioDataAccessMapper.portfolioToPortfolioEntity(portfolio);
        return portfolioDataAccessMapper.portfolioEntityToPortfolio(portfolioJpaRepository.save(portfolioEntity));
    }

    @Override
    public CurrencyBalance saveCurrencyBalance(CurrencyBalance currencyBalance) {
        CurrencyBalanceEntity currencyBalanceEntity = portfolioDataAccessMapper
                .currencyBalanceToCurrencyBalanceEntity(currencyBalance);
        return portfolioDataAccessMapper
                .currencyBalanceEntityToCurrencyBalance(currencyBalanceJpaRepository.save(currencyBalanceEntity));
    }

    @Override
    public CryptoBalance saveCryptoBalance(CryptoBalance cryptoBalance) {
        CryptoBalanceEntity cryptoBalanceEntity = portfolioDataAccessMapper
                .cryptoBalanceToCryptoBalanceEntity(cryptoBalance);
        return portfolioDataAccessMapper
                .cryptoBalanceEntityToCryptoBalance(cryptoBalanceJpaRepository.save(cryptoBalanceEntity));
    }

    @Override
    public Optional<CurrencyBalance> findCurrencyBalanceByUserId(UUID userId) {
        return currencyBalanceJpaRepository.findCurrencyBalanceEntityByUserId(userId)
                .map(portfolioDataAccessMapper::currencyBalanceEntityToCurrencyBalance);
    }

    @Override
    public Optional<CurrencyBalance> findCurrencyBalanceByPortfolioId(UUID portfolioId, UUID userId) {
        Optional<CurrencyBalance> currencyBalance = currencyBalanceJpaRepository.findCurrencyBalanceEntityByPortfolioIdAndUserId(portfolioId, userId)
                .map(portfolioDataAccessMapper::currencyBalanceEntityToCurrencyBalance);
        return currencyBalance;
    }

    @Override
    public DepositWithdrawal saveDepositWithdrawal(DepositWithdrawal depositWithdrawal) {
        DepositWithdrawalEntity depositWithdrawalEntity = portfolioDataAccessMapper
                .depositWithdrawalToDepositWithdrawalEntity(depositWithdrawal);
        return portfolioDataAccessMapper.depositWithdrawalEntityToDepositWithdrawal(
                depositWithdrawalJpaRepository.save(depositWithdrawalEntity));
    }

    @Override
    public Optional<DepositWithdrawal> findDepositWithdrawalByUserId(UUID transactionId, UUID userId) {
        return depositWithdrawalJpaRepository
                .findDepositWithdrawalEntityByTransactionIdAndUserId(transactionId, userId)
                .map(portfolioDataAccessMapper::depositWithdrawalEntityToDepositWithdrawal);
    }

    @Override
    public List<CryptoBalance> findCryptoBalancesByPortfolioId(UUID portfolioId) {
        return cryptoBalanceJpaRepository.findCryptoBalanceEntitiesByPortfolioId(portfolioId)
                .stream().map(portfolioDataAccessMapper::cryptoBalanceEntityToCryptoBalance)
                .collect(Collectors.toList());
    }

    @Override
    public void deletePortfolio(UUID userId) {
        portfolioJpaRepository.deletePortfolioEntityByUserId(userId);
    }
}
