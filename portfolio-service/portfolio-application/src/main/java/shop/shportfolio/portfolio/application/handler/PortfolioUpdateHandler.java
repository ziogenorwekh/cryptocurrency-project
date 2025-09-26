package shop.shportfolio.portfolio.application.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.portfolio.application.dto.*;
import shop.shportfolio.portfolio.application.exception.BalanceNotFoundException;
import shop.shportfolio.portfolio.application.exception.DepositFailedException;
import shop.shportfolio.portfolio.application.exception.PortfolioNotFoundException;
import shop.shportfolio.portfolio.application.port.output.repository.AssetChangeLogRepositoryPort;
import shop.shportfolio.portfolio.application.port.output.repository.PortfolioRepositoryPort;
import shop.shportfolio.portfolio.domain.PortfolioDomainService;
import shop.shportfolio.portfolio.domain.entity.CryptoBalance;
import shop.shportfolio.portfolio.domain.entity.CurrencyBalance;
import shop.shportfolio.portfolio.domain.entity.DepositWithdrawal;
import shop.shportfolio.portfolio.domain.entity.Portfolio;
import shop.shportfolio.common.domain.valueobject.BalanceId;
import shop.shportfolio.portfolio.domain.valueobject.PurchasePrice;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class PortfolioUpdateHandler {

    private final PortfolioRepositoryPort portfolioRepositoryPort;
    private final PortfolioDomainService portfolioDomainService;

    @Autowired
    public PortfolioUpdateHandler(PortfolioRepositoryPort portfolioRepositoryPort,
                                  PortfolioDomainService portfolioDomainService) {
        this.portfolioRepositoryPort = portfolioRepositoryPort;
        this.portfolioDomainService = portfolioDomainService;
    }

    public CryptoBalance updateCryptoBalance(TradeKafkaResponse response) {
        Portfolio portfolio = portfolioRepositoryPort.findPortfolioByUserId(UUID.fromString(response.getUserId()))
                .orElseThrow(() -> new PortfolioNotFoundException("No portfolio found for user id: " + response.getUserId()));

        CryptoBalance cryptoBalance = getOrCreateCryptoBalance(portfolio, response.getMarketId());

        Quantity quantity = new Quantity(BigDecimal.valueOf(response.getQuantity()));
        if (response.getTransactionType() == TransactionType.TRADE_BUY) {
            PurchasePrice price = new PurchasePrice(BigDecimal.valueOf(response.getOrderPrice()));
            portfolioDomainService.addPurchase(cryptoBalance, price, quantity);
        } else if (response.getTransactionType() == TransactionType.TRADE_SELL) {
            portfolioDomainService.subtractQuantity(cryptoBalance, quantity);
        }
        return portfolioRepositoryPort.saveCryptoBalance(cryptoBalance);
    }

    public void updateCurrencyBalance(BalanceKafkaResponse response) {
        Optional<CurrencyBalance> optional = portfolioRepositoryPort
                .findCurrencyBalanceByUserId(response.getUserId());
        optional.ifPresent(currencyBalance -> {
            portfolioDomainService.updateMoney(currencyBalance,
                    Money.of(BigDecimal.valueOf(response.getBalance())));
            portfolioRepositoryPort.saveCurrencyBalance(currencyBalance);
        });
    }

    public WithdrawalSagaContext completeWithdrawal(DepositWithdrawalKafkaResponse kafkaResponse) {
        CurrencyBalance currencyBalance = portfolioRepositoryPort
                .findCurrencyBalanceByUserId(kafkaResponse.getUserId()).orElseThrow(
                        () -> new BalanceNotFoundException("No portfolio found for user id: " + kafkaResponse.getUserId())
                );
        DepositWithdrawal depositWithdrawal = portfolioRepositoryPort.findDepositWithdrawalByUserId(UUID.fromString(kafkaResponse.getTransactionId())
                , kafkaResponse.getUserId()).orElseThrow(() -> new DepositFailedException("No deposits found for user id: " +
                kafkaResponse.getUserId()));
        portfolioDomainService.subtractMoney(currencyBalance,
                Money.of(BigDecimal.valueOf(kafkaResponse.getAmount())));
        portfolioDomainService.completeWithdrawal(depositWithdrawal);
        portfolioRepositoryPort.saveDepositWithdrawal(depositWithdrawal);
        portfolioRepositoryPort.saveCurrencyBalance(currencyBalance);
        return new WithdrawalSagaContext(depositWithdrawal, currencyBalance.getPortfolioId());
    }

    public WithdrawalSagaContext failWithdrawal(DepositWithdrawalKafkaResponse kafkaResponse) {
        Portfolio portfolio = portfolioRepositoryPort.findPortfolioByUserId(kafkaResponse.getUserId()).orElseThrow(
                () -> new PortfolioNotFoundException("No portfolio found for user id: " + kafkaResponse.getUserId())
        );
        DepositWithdrawal depositWithdrawal = portfolioRepositoryPort.findDepositWithdrawalByUserId(UUID.fromString(kafkaResponse.getTransactionId())
                , kafkaResponse.getUserId()).orElseThrow(() -> new BalanceNotFoundException("No portfolio found for user id: " + kafkaResponse.getUserId())
        );
        portfolioDomainService.failWithdrawal(depositWithdrawal);
        portfolioRepositoryPort.saveDepositWithdrawal(depositWithdrawal);
        return new WithdrawalSagaContext(depositWithdrawal, portfolio.getId());
    }

    private CryptoBalance getOrCreateCryptoBalance(Portfolio portfolio, String marketId) {
        return portfolioRepositoryPort.findCryptoBalanceByPortfolioIdAndMarketId(portfolio.getId().getValue(), marketId)
                .orElseGet(() -> portfolioDomainService.createCryptoBalance(new BalanceId(UUID.randomUUID()),
                        portfolio.getId(),
                        new MarketId(marketId),
                        Quantity.of(BigDecimal.ZERO),
                        new PurchasePrice(BigDecimal.ZERO),
                        UpdatedAt.now()
                ));
    }
}
