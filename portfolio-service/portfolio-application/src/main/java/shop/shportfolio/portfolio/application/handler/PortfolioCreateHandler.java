package shop.shportfolio.portfolio.application.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.dto.payment.PaymentResponse;
import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.portfolio.application.command.create.DepositCreateCommand;
import shop.shportfolio.portfolio.application.command.create.PortfolioCreateCommand;
import shop.shportfolio.portfolio.application.command.create.WithdrawalCreateCommand;
import shop.shportfolio.portfolio.application.dto.DepositResultContext;
import shop.shportfolio.portfolio.application.dto.WithdrawalResultContext;
import shop.shportfolio.portfolio.application.exception.BalanceNotFoundException;
import shop.shportfolio.portfolio.application.exception.InvalidRequestException;
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
        UUID portfolioId = portfolio.getId().getValue();

        log.info("Will deposit portfolioId -> {}", portfolioId);
        DepositCreatedEvent depositCreatedEvent = createDepositEvent(portfolio, response);

        CurrencyBalance currencyBalance = getOrCreateCurrencyBalance(portfolioId,portfolio.getUserId());

        persistDepositAndBalance(depositCreatedEvent.getDomainType(), currencyBalance);

        return new DepositResultContext(depositCreatedEvent, currencyBalance);
    }


    public WithdrawalResultContext withdrawal(WithdrawalCreateCommand command) {
        Portfolio portfolio = getPortfolioOrThrow(command.getUserId());
        UUID portfolioId = portfolio.getId().getValue();

        log.info("Will withdrawal portfolioId -> {}", portfolioId);
        DepositWithdrawal withdrawal = createWithdrawal(portfolio, command);

        CurrencyBalance currencyBalance = getCurrencyBalanceOrThrow(portfolioId);
        isOverCurrencyBalanceAmount(currencyBalance, command.getAmount());
        WithdrawalCreatedEvent withdrawalEvent = createWithdrawalEvent(withdrawal);

        persistDepositAndBalance(withdrawalEvent.getDomainType(), currencyBalance);

        return new WithdrawalResultContext(withdrawalEvent, currencyBalance);
    }

    private void isOverCurrencyBalanceAmount(CurrencyBalance currencyBalance, Long withdrawalAmount) {
        if (currencyBalance.isOverCurrencyBalanceAmount(withdrawalAmount)) {
            throw new InvalidRequestException(String.format(
                    "Withdrawal amount %d exceeds available currency balance %s",
                    withdrawalAmount, currencyBalance.getAmount().getValue()));
        }
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

    private CurrencyBalance getOrCreateCurrencyBalance(UUID portfolioId,UserId userId) {
        Optional<CurrencyBalance> optional = portfolioRepositoryPort.findCurrencyBalanceByPortfolioId(portfolioId);
        return optional.orElseGet(() -> portfolioDomainService.createCurrencyBalance(
                new BalanceId(UUID.randomUUID()),
                new PortfolioId(portfolioId),
                new MarketId("KRW"),
                Money.of(BigDecimal.ZERO),
                UpdatedAt.now(),
                userId
        ));
    }

    private CurrencyBalance getCurrencyBalanceOrThrow(UUID portfolioId) {
        return portfolioRepositoryPort.findCurrencyBalanceByPortfolioId(portfolioId)
                .orElseThrow(() -> new BalanceNotFoundException(
                        String.format("CurrencyBalance's portfolioId : %s is not found.", portfolioId)));
    }

    private void persistDepositAndBalance(DepositWithdrawal depositWithdrawal, CurrencyBalance currencyBalance) {
        portfolioRepositoryPort.saveDepositWithdrawal(depositWithdrawal);
        portfolioRepositoryPort.saveCurrencyBalance(currencyBalance);
        log.info("successful deposit Amount: {} userId: {}",
                depositWithdrawal.getAmount().getValue(), depositWithdrawal.getUserId().getValue());
    }
}
