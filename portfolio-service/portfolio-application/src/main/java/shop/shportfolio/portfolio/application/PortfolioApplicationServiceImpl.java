package shop.shportfolio.portfolio.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import shop.shportfolio.common.domain.dto.payment.PaymentPayRequest;
import shop.shportfolio.common.domain.dto.payment.PaymentResponse;
import shop.shportfolio.common.domain.valueobject.PaymentStatus;
import shop.shportfolio.portfolio.application.command.create.*;
import shop.shportfolio.portfolio.application.command.track.*;
import shop.shportfolio.portfolio.application.dto.DepositResultContext;
import shop.shportfolio.portfolio.application.dto.TotalBalanceContext;
import shop.shportfolio.portfolio.application.dto.WithdrawalResultContext;
import shop.shportfolio.portfolio.application.exception.DepositFailedException;
import shop.shportfolio.portfolio.application.handler.AssetChangeLogHandler;
import shop.shportfolio.portfolio.application.handler.PortfolioPaymentHandler;
import shop.shportfolio.portfolio.application.handler.PortfolioCreateHandler;
import shop.shportfolio.portfolio.application.handler.PortfolioTrackHandler;
import shop.shportfolio.portfolio.application.mapper.PortfolioDataMapper;
import shop.shportfolio.portfolio.application.port.input.PortfolioApplicationService;
import shop.shportfolio.portfolio.application.port.output.kafka.DepositKafkaPublisher;
import shop.shportfolio.portfolio.application.port.output.kafka.WithdrawalKafkaPublisher;
import shop.shportfolio.portfolio.domain.entity.CryptoBalance;
import shop.shportfolio.portfolio.domain.entity.CurrencyBalance;
import shop.shportfolio.portfolio.domain.entity.Portfolio;

@Slf4j
@Service
@Validated
public class PortfolioApplicationServiceImpl implements PortfolioApplicationService {

    private final PortfolioTrackHandler portfolioTrackHandler;
    private final PortfolioDataMapper portfolioDataMapper;
    private final PortfolioCreateHandler portfolioCreateHandler;
    private final PortfolioPaymentHandler portfolioPaymentHandler;
    private final DepositKafkaPublisher depositKafkaPublisher;
    private final WithdrawalKafkaPublisher withdrawalKafkaPublisher;
    private final AssetChangeLogHandler assetChangeLogHandler;
    @Autowired
    public PortfolioApplicationServiceImpl(PortfolioTrackHandler portfolioTrackHandler,
                                           PortfolioDataMapper portfolioDataMapper,
                                           PortfolioCreateHandler portfolioCreateHandler,
                                           PortfolioPaymentHandler portfolioPaymentHandler,
                                           DepositKafkaPublisher depositKafkaPublisher,
                                           WithdrawalKafkaPublisher withdrawalKafkaPublisher, AssetChangeLogHandler assetChangeLogHandler) {
        this.portfolioTrackHandler = portfolioTrackHandler;
        this.portfolioDataMapper = portfolioDataMapper;
        this.portfolioCreateHandler = portfolioCreateHandler;
        this.portfolioPaymentHandler = portfolioPaymentHandler;
        this.depositKafkaPublisher = depositKafkaPublisher;
        this.withdrawalKafkaPublisher = withdrawalKafkaPublisher;
        this.assetChangeLogHandler = assetChangeLogHandler;
    }


    @Override
    @Transactional(readOnly = true)
    public CryptoBalanceTrackQueryResponse trackCryptoBalance(CryptoBalanceTrackQuery cryptoBalanceTrackQuery) {
        CryptoBalance balance = portfolioTrackHandler.findCryptoBalanceByPortfolioIdAndMarketId(cryptoBalanceTrackQuery);
        return portfolioDataMapper.cryptoBalanceToCryptoBalanceTrackQueryResponse(balance);
    }

    @Override
    @Transactional(readOnly = true)
    public CurrencyBalanceTrackQueryResponse trackCurrencyBalance(CurrencyBalanceTrackQuery currencyBalanceTrackQuery) {
        CurrencyBalance currencyBalance = portfolioTrackHandler.findCurrencyBalanceByPortfolioId(currencyBalanceTrackQuery);
        return portfolioDataMapper.currencyBalanceToCurrencyBalanceTrackQueryResponse(currencyBalance);
    }

    @Override
    @Transactional(readOnly = true)
    public PortfolioTrackQueryResponse trackPortfolio(PortfolioTrackQuery portfolioTrackQuery) {
        Portfolio portfolio = portfolioTrackHandler.findPortfolioByPortfolioIdAndUserId(portfolioTrackQuery);
        return portfolioDataMapper.PortfolioToTotalAssetValueTrackQueryResponse(portfolio);
    }

    @Override
    @Transactional(readOnly = true)
    public TotalBalanceTrackQueryResponse trackTotalBalances(TotalBalanceTrackQuery totalBalanceTrackQuery) {
        TotalBalanceContext balanceContext = portfolioTrackHandler.findBalances(totalBalanceTrackQuery);
        return portfolioDataMapper.totalBalanceContextToTotalBalanceTrackQueryResponse(balanceContext);
    }

    @Override
    public DepositCreatedResponse deposit(DepositCreateCommand depositCreateCommand) {
        PaymentPayRequest request = portfolioDataMapper.depositCreateCommandToPaymentPayRequest(depositCreateCommand);
        PaymentResponse paymentResponse = portfolioPaymentHandler.pay(request);
        if (paymentResponse.getStatus().equals(PaymentStatus.DONE)) {
            DepositResultContext context = portfolioCreateHandler
                    .deposit(depositCreateCommand, paymentResponse);
            depositKafkaPublisher.publish(context.getDepositCreatedEvent());
            assetChangeLogHandler.saveDeposit(context.getDepositCreatedEvent().getDomainType(),
                    context.getBalance().getPortfolioId());
            return portfolioDataMapper.currencyBalanceToDepositCreatedResponse(context.getBalance(),
                    depositCreateCommand.getUserId(), paymentResponse.getTotalAmount());
        }
        throw new DepositFailedException(String.format("userId: %s is deposit failed. ",
                depositCreateCommand.getUserId()));
    }

    @Override
    public PortfolioCreatedResponse createPortfolio(PortfolioCreateCommand portfolioCreateCommand) {
        Portfolio portfolio = portfolioCreateHandler.createPortfolio(portfolioCreateCommand);
        return portfolioDataMapper.portfolioToPortfolioCreatedResponse(portfolio);
    }

    @Override
    public WithdrawalCreatedResponse withdrawal(WithdrawalCreateCommand withdrawalCreateCommand) {
        WithdrawalResultContext context = portfolioCreateHandler.withdrawal(withdrawalCreateCommand);
        assetChangeLogHandler.saveWithdrawal(context.getWithdrawalCreatedEvent().getDomainType(),
                context.getBalance().getPortfolioId());
        withdrawalKafkaPublisher.publish(context.getWithdrawalCreatedEvent());
        return portfolioDataMapper.currencyBalanceToWithdrawalCreatedResponse(
                context.getWithdrawalCreatedEvent().getDomainType()
                , "출금 완료");
    }
}
