package shop.shportfolio.portfolio.application.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.dto.payment.PaymentResponse;
import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.portfolio.application.command.DepositCreateCommand;
import shop.shportfolio.portfolio.application.command.PortfolioCreateCommand;
import shop.shportfolio.portfolio.application.dto.DepositResultContext;
import shop.shportfolio.portfolio.application.exception.PortfolioExistException;
import shop.shportfolio.portfolio.application.exception.PortfolioNotFoundException;
import shop.shportfolio.portfolio.application.port.output.repository.PortfolioRepositoryPort;
import shop.shportfolio.portfolio.domain.DepositWithdrawalDomainService;
import shop.shportfolio.portfolio.domain.PortfolioDomainService;
import shop.shportfolio.portfolio.domain.entity.Balance;
import shop.shportfolio.portfolio.domain.entity.CurrencyBalance;
import shop.shportfolio.portfolio.domain.entity.DepositWithdrawal;
import shop.shportfolio.portfolio.domain.entity.Portfolio;
import shop.shportfolio.portfolio.domain.event.DepositCreatedEvent;
import shop.shportfolio.portfolio.domain.valueobject.*;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class PortfolioCreateHandler {

    private final PortfolioDomainService portfolioDomainService;
    private final PortfolioRepositoryPort portfolioRepositoryPort;
    private final DepositWithdrawalDomainService depositWithdrawalDomainService;

    @Autowired
    public PortfolioCreateHandler(PortfolioDomainService portfolioDomainService,
                                  PortfolioRepositoryPort portfolioRepositoryPort,
                                  DepositWithdrawalDomainService depositWithdrawalDomainService) {
        this.portfolioDomainService = portfolioDomainService;
        this.portfolioRepositoryPort = portfolioRepositoryPort;
        this.depositWithdrawalDomainService = depositWithdrawalDomainService;
    }


    public Portfolio createPortfolio(PortfolioCreateCommand command) {
        portfolioRepositoryPort.findPortfolioByUserId(command.getUserId()).ifPresent(portfolio -> {
            throw new PortfolioExistException(String.format("userId : %s is exist Portfolio.",
                    command.getUserId()));
        });
        Portfolio portfolio = portfolioDomainService.createPortfolio(new PortfolioId(UUID.randomUUID()),
                new UserId(command.getUserId()),
                TotalAssetValue.of(BigDecimal.ZERO), CreatedAt.now(), UpdatedAt.now());
        return portfolioRepositoryPort.savePortfolio(portfolio);
    }

    public DepositResultContext deposit(DepositCreateCommand command, PaymentResponse response) {
        Portfolio portfolio = portfolioRepositoryPort.findPortfolioByUserId(command.getUserId())
                .orElseThrow(() -> new PortfolioNotFoundException(String.format("userId : %s is not found.",
                        command.getUserId())));
        DepositCreatedEvent depositCreatedEvent = depositWithdrawalDomainService
                .createDepositWithdrawal(
                        new TransactionId(UUID.randomUUID()), portfolio.getUserId(),
                        Money.of(BigDecimal.valueOf(response.getTotalAmount())),
                        TransactionType.DEPOSIT, TransactionTime.now(),
                        TransactionStatus.COMPLETED, RelatedWalletAddress.empty(),
                        CreatedAt.now(), UpdatedAt.now());
        DepositWithdrawal depositWithdrawal = portfolioRepositoryPort.saveDepositWithdrawal(depositCreatedEvent.getDomainType());
        log.info("successful deposit Amount: {} userId: {}", depositWithdrawal.getAmount().getValue(),
                depositWithdrawal.getUserId().getValue());
        Optional<CurrencyBalance> optional = portfolioRepositoryPort
                .findCurrencyBalanceByPortfolioIdAndUserId(portfolio.getId().getValue());
        CurrencyBalance currencyBalance;
        if (optional.isEmpty()) {
            currencyBalance = portfolioDomainService
                    .createCurrencyBalance(new BalanceId(UUID.randomUUID()), portfolio.getId()
                            , new MarketId("KRW"), depositWithdrawal.getAmount(), UpdatedAt.now());
        } else {
            currencyBalance = optional.get();
            portfolioDomainService.addMoney(currencyBalance, depositWithdrawal.getAmount());
        }
        return new DepositResultContext(depositCreatedEvent, currencyBalance);
    }

    public Portfolio withdraw(DepositCreateCommand depositCreateCommand) {
        return null;
    }
}
