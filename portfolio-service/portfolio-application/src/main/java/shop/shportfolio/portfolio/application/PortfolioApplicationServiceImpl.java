package shop.shportfolio.portfolio.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

    @Autowired
    public PortfolioApplicationServiceImpl(PortfolioTrackHandler portfolioTrackHandler,
                                           PortfolioDataMapper portfolioDataMapper,
                                           PortfolioCreateHandler portfolioCreateHandler,
                                           PortfolioPaymentHandler portfolioPaymentHandler,
                                           DepositKafkaPublisher depositKafkaPublisher,
                                           WithdrawalKafkaPublisher withdrawalKafkaPublisher) {
        this.portfolioTrackHandler = portfolioTrackHandler;
        this.portfolioDataMapper = portfolioDataMapper;
        this.portfolioCreateHandler = portfolioCreateHandler;
        this.portfolioPaymentHandler = portfolioPaymentHandler;
        this.depositKafkaPublisher = depositKafkaPublisher;
        this.withdrawalKafkaPublisher = withdrawalKafkaPublisher;
    }


    @Override
    public CryptoBalanceTrackQueryResponse trackCryptoBalance(CryptoBalanceTrackQuery cryptoBalanceTrackQuery) {
        CryptoBalance balance = portfolioTrackHandler.findCryptoBalanceByPortfolioIdAndMarketId(cryptoBalanceTrackQuery);
        return portfolioDataMapper.cryptoBalanceToCryptoBalanceTrackQueryResponse(balance);
    }

    @Override
    public CurrencyBalanceTrackQueryResponse trackCurrencyBalance(CurrencyBalanceTrackQuery currencyBalanceTrackQuery) {
        CurrencyBalance currencyBalance = portfolioTrackHandler.findCurrencyBalanceByPortfolioId(currencyBalanceTrackQuery);
        return portfolioDataMapper.currencyBalanceToCurrencyBalanceTrackQueryResponse(currencyBalance);
    }

    @Override
    public PortfolioTrackQueryResponse trackPortfolio(PortfolioTrackQuery portfolioTrackQuery) {
        Portfolio portfolio = portfolioTrackHandler.findPortfolioByPortfolioIdAndUserId(portfolioTrackQuery);
        return portfolioDataMapper.PortfolioToTotalAssetValueTrackQueryResponse(portfolio);
    }

    @Override
    public TotalBalanceTrackQueryResponse trackTotalBalances(TotalBalanceTrackQuery totalBalanceTrackQuery) {
        TotalBalanceContext balanceContext = portfolioTrackHandler.findBalances(totalBalanceTrackQuery);
        return portfolioDataMapper.totalBalanceContextToTotalBalanceTrackQueryResponse(balanceContext);
    }

    @Override
    public DepositCreatedResponse deposit(DepositCreateCommand depositCreateCommand) {
        PaymentPayRequest request = portfolioDataMapper.depositCreateCommandToPaymentPayRequest(depositCreateCommand);
        PaymentResponse paymentResponse = portfolioPaymentHandler.pay(request);
        if (paymentResponse.getStatus().equals(PaymentStatus.DONE)) {
            DepositResultContext depositResultContext = portfolioCreateHandler
                    .deposit(depositCreateCommand, paymentResponse);
            depositKafkaPublisher.publish(depositResultContext.getDepositCreatedEvent());
            return portfolioDataMapper.currencyBalanceToDepositCreatedResponse(depositResultContext.getBalance(),
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
        WithdrawalResultContext withdrawalResultContext = portfolioCreateHandler.withdrawal(withdrawalCreateCommand);
        withdrawalKafkaPublisher.publish(withdrawalResultContext.getWithdrawalCreatedEvent());
        return portfolioDataMapper.currencyBalanceToWithdrawalCreatedResponse(withdrawalResultContext.getBalance(),
                withdrawalCreateCommand.getAmount(), withdrawalResultContext.getWithdrawalCreatedEvent().getDomainType()
                , "출금 완료");
    }
}
