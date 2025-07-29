package shop.shportfolio.portfolio.application.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.dto.payment.PaymentResponse;
import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.portfolio.application.command.create.DepositCreateCommand;
import shop.shportfolio.portfolio.application.command.create.PortfolioCreateCommand;
import shop.shportfolio.portfolio.application.command.create.WithdrawalCreateCommand;
import shop.shportfolio.portfolio.application.dto.DepositResultContext;
import shop.shportfolio.portfolio.application.dto.WithdrawalResultContext;
import shop.shportfolio.portfolio.application.exception.BalanceNotFoundException;
import shop.shportfolio.portfolio.application.exception.PortfolioExistException;
import shop.shportfolio.portfolio.application.exception.PortfolioNotFoundException;
import shop.shportfolio.portfolio.application.port.output.repository.PortfolioRepositoryPort;
import shop.shportfolio.portfolio.domain.DepositWithdrawalDomainService;
import shop.shportfolio.portfolio.domain.PortfolioDomainService;
import shop.shportfolio.portfolio.domain.entity.CurrencyBalance;
import shop.shportfolio.portfolio.domain.entity.DepositWithdrawal;
import shop.shportfolio.portfolio.domain.entity.Portfolio;
import shop.shportfolio.portfolio.domain.event.DepositCreatedEvent;
import shop.shportfolio.portfolio.domain.event.WithdrawalCreatedEvent;
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
                new UserId(command.getUserId()), CreatedAt.now(), UpdatedAt.now());
        return portfolioRepositoryPort.savePortfolio(portfolio);
    }

    public DepositResultContext deposit(DepositCreateCommand command, PaymentResponse response) {
        Portfolio portfolio = getPortfolioOrThrow(command.getUserId());
        log.info("Will deposit portfolioId -> {}", portfolio.getId().getValue());
        DepositCreatedEvent depositCreatedEvent = createDepositEvent(portfolio, response);

        CurrencyBalance currencyBalance = processDepositCurrencyBalance(portfolio,
                depositCreatedEvent.getDomainType().getAmount());

        persistDepositAndBalance(depositCreatedEvent.getDomainType(), currencyBalance);

        return new DepositResultContext(depositCreatedEvent, currencyBalance);
    }


    public WithdrawalResultContext withdrawal(WithdrawalCreateCommand command) {
        Portfolio portfolio = getPortfolioOrThrow(command.getUserId());
        log.info("Will withdrawal portfolioId -> {}", portfolio.getId().getValue());
        DepositWithdrawal withdrawal = createWithdrawal(portfolio, command);
        CurrencyBalance currencyBalance = processWithdrawalCurrencyBalance(portfolio, withdrawal.getAmount());

        WithdrawalCreatedEvent withdrawalEvent = createWithdrawalEvent(withdrawal);

        persistDepositAndBalance(withdrawalEvent.getDomainType(), currencyBalance);

        return new WithdrawalResultContext(withdrawalEvent, currencyBalance);
    }

    private Portfolio getPortfolioOrThrow(UUID userId) {
        return portfolioRepositoryPort.findPortfolioByUserId(userId)
                .orElseThrow(() -> new PortfolioNotFoundException(String.format("userId : %s is not found.", userId)));
    }

    private DepositCreatedEvent createDepositEvent(Portfolio portfolio, PaymentResponse response) {
        return depositWithdrawalDomainService.createDeposit(
                new TransactionId(UUID.randomUUID()), portfolio.getUserId(),
                Money.of(BigDecimal.valueOf(response.getTotalAmount())),
                TransactionType.DEPOSIT, TransactionTime.now(),
                TransactionStatus.COMPLETED, RelatedWalletAddress.empty(),
                CreatedAt.now(), UpdatedAt.now()
        );
    }

    private DepositWithdrawal createWithdrawal(Portfolio portfolio, WithdrawalCreateCommand command) {
        RelatedWalletAddress relatedWalletAddress = new RelatedWalletAddress(command.getAccount(),
                command.getBankName(), WalletType.BANK_ACCOUNT);
        return depositWithdrawalDomainService
                .createWithdrawal(new TransactionId(UUID.randomUUID()), portfolio.getUserId()
                , Money.of(BigDecimal.valueOf(command.getAmount())),
                TransactionType.WITHDRAWAL, TransactionTime.now(),
                TransactionStatus.PENDING, relatedWalletAddress,
                        CreatedAt.now(), UpdatedAt.now());
    }

    private WithdrawalCreatedEvent createWithdrawalEvent(DepositWithdrawal depositWithdrawal) {
        return depositWithdrawalDomainService.updateWithdrawal(depositWithdrawal);
    }

    private CurrencyBalance processWithdrawalCurrencyBalance(Portfolio portfolio, Money withdrawalAmount) {
        CurrencyBalance currencyBalance = portfolioRepositoryPort.findCurrencyBalanceByPortfolioId(portfolio.getId().getValue())
                .orElseThrow(() -> new BalanceNotFoundException(String.format("CurrencyBalance's portfolioId : %s is not found.",
                        portfolio.getId().getValue())));

        portfolioDomainService.subtractMoney(currencyBalance, withdrawalAmount);
        log.info("Withdrawal amount -> {}", withdrawalAmount);
        log.info("successful withdrawal amount and rest balance is -> {}", currencyBalance.getAmount().getValue());
        return currencyBalance;
    }

    private CurrencyBalance processDepositCurrencyBalance(Portfolio portfolio, Money depositAmount) {
        Optional<CurrencyBalance> optional = portfolioRepositoryPort
                .findCurrencyBalanceByPortfolioId(portfolio.getId().getValue());
        if (optional.isEmpty()) {
            return portfolioDomainService.createCurrencyBalance(
                    new BalanceId(UUID.randomUUID()), portfolio.getId(),
                    new MarketId("KRW"), depositAmount, UpdatedAt.now()
            );
        }

        CurrencyBalance currencyBalance = optional.get();
        log.info("Current balance for portfolio {} money is -> {} before deposit.", portfolio.getId().getValue(),
                currencyBalance.getAmount().getValue());
        portfolioDomainService.addMoney(currencyBalance, depositAmount);
        return currencyBalance;
    }

    private void persistDepositAndBalance(DepositWithdrawal depositWithdrawal, CurrencyBalance currencyBalance) {
        portfolioRepositoryPort.saveDepositWithdrawal(depositWithdrawal);
        portfolioRepositoryPort.saveCurrencyBalance(currencyBalance);
        log.info("successful deposit Amount: {} userId: {}",
                depositWithdrawal.getAmount().getValue(), depositWithdrawal.getUserId().getValue());
    }

}
