package shop.shportfolio.portfolio.application.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.valueobject.Quantity;
import shop.shportfolio.common.domain.valueobject.TransactionType;
import shop.shportfolio.portfolio.application.dto.TradeKafkaResponse;
import shop.shportfolio.portfolio.application.exception.BalanceNotFoundException;
import shop.shportfolio.portfolio.application.exception.PortfolioNotFoundException;
import shop.shportfolio.portfolio.application.port.output.repository.PortfolioRepositoryPort;
import shop.shportfolio.portfolio.domain.PortfolioDomainService;
import shop.shportfolio.portfolio.domain.entity.CryptoBalance;
import shop.shportfolio.portfolio.domain.entity.Portfolio;
import shop.shportfolio.portfolio.domain.valueobject.PurchasePrice;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Component
public class PortfolioUpdateHandler {

    private final PortfolioRepositoryPort portfolioRepositoryPort;
    private final PortfolioDomainService portfolioDomainService;

    @Autowired
    public PortfolioUpdateHandler(PortfolioRepositoryPort portfolioRepositoryPort, PortfolioDomainService portfolioDomainService) {
        this.portfolioRepositoryPort = portfolioRepositoryPort;
        this.portfolioDomainService = portfolioDomainService;
    }

    public CryptoBalance updateCryptoBalance(TradeKafkaResponse response) {
        Portfolio portfolio = portfolioRepositoryPort.findPortfolioByUserId(UUID.fromString(response.getUserId()))
                .orElseThrow(() -> new PortfolioNotFoundException("No portfolio found for user id: " + response.getUserId()));

        UUID portfolioId = portfolio.getId().getValue();
        CryptoBalance cryptoBalance = portfolioRepositoryPort.findCryptoBalanceByPortfolioIdAndMarketId(portfolioId, response.getMarketId())
                .orElseThrow(() -> new BalanceNotFoundException("No crypto balance found for portfolio id: " + portfolioId));

        Quantity quantity = new Quantity(BigDecimal.valueOf(response.getQuantity()));
        if (response.getTransactionType() == TransactionType.TRADE_BUY) {
            PurchasePrice price = new PurchasePrice(BigDecimal.valueOf(response.getOrderPrice()));
            portfolioDomainService.addPurchase(cryptoBalance, price, quantity);
        } else if (response.getTransactionType() == TransactionType.TRADE_SELL) {
            portfolioDomainService.subtractQuantity(cryptoBalance, quantity);
        }

        return portfolioRepositoryPort.saveCryptoBalance(cryptoBalance);
    }

}
