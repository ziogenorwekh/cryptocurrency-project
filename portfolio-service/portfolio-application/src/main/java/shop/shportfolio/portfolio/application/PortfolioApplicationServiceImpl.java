package shop.shportfolio.portfolio.application;

import jakarta.validation.Valid;
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
import shop.shportfolio.portfolio.application.exception.TossAPIException;
import shop.shportfolio.portfolio.application.handler.AssetChangeLogHandler;
import shop.shportfolio.portfolio.application.handler.PortfolioPaymentHandler;
import shop.shportfolio.portfolio.application.handler.PortfolioCreateHandler;
import shop.shportfolio.portfolio.application.handler.PortfolioTrackHandler;
import shop.shportfolio.portfolio.application.mapper.PortfolioDataMapper;
import shop.shportfolio.portfolio.application.port.input.PortfolioApplicationService;
import shop.shportfolio.portfolio.application.port.output.kafka.DepositPublisher;
import shop.shportfolio.portfolio.application.port.output.kafka.WithdrawalPublisher;
import shop.shportfolio.portfolio.domain.entity.AssetChangeLog;
import shop.shportfolio.portfolio.domain.entity.CryptoBalance;
import shop.shportfolio.portfolio.domain.entity.CurrencyBalance;
import shop.shportfolio.portfolio.domain.entity.Portfolio;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Validated
public class PortfolioApplicationServiceImpl implements PortfolioApplicationService {

    private final PortfolioTrackHandler portfolioTrackHandler;
    private final PortfolioDataMapper portfolioDataMapper;
    private final PortfolioCreateHandler portfolioCreateHandler;
    private final PortfolioPaymentHandler portfolioPaymentHandler;
    private final DepositPublisher depositPublisher;
    private final WithdrawalPublisher withdrawalPublisher;
    private final AssetChangeLogHandler assetChangeLogHandler;

    @Autowired
    public PortfolioApplicationServiceImpl(PortfolioTrackHandler portfolioTrackHandler,
                                           PortfolioDataMapper portfolioDataMapper,
                                           PortfolioCreateHandler portfolioCreateHandler,
                                           PortfolioPaymentHandler portfolioPaymentHandler,
                                           DepositPublisher depositPublisher,
                                           WithdrawalPublisher withdrawalPublisher, AssetChangeLogHandler assetChangeLogHandler) {
        this.portfolioTrackHandler = portfolioTrackHandler;
        this.portfolioDataMapper = portfolioDataMapper;
        this.portfolioCreateHandler = portfolioCreateHandler;
        this.portfolioPaymentHandler = portfolioPaymentHandler;
        this.depositPublisher = depositPublisher;
        this.withdrawalPublisher = withdrawalPublisher;
        this.assetChangeLogHandler = assetChangeLogHandler;
    }


    @Override
    @Transactional(readOnly = true)
    public CryptoBalanceTrackQueryResponse trackCryptoBalance(@Valid CryptoBalanceTrackQuery cryptoBalanceTrackQuery) {

        CryptoBalance balance = portfolioTrackHandler.findCryptoBalanceByPortfolioIdAndMarketId(cryptoBalanceTrackQuery);
        log.info("crypto balance track query received {}", balance);
        return portfolioDataMapper.cryptoBalanceToCryptoBalanceTrackQueryResponse(balance);
    }

    @Override
    @Transactional(readOnly = true)
    public CurrencyBalanceTrackQueryResponse trackCurrencyBalance(@Valid CurrencyBalanceTrackQuery currencyBalanceTrackQuery) {
        CurrencyBalance currencyBalance = portfolioTrackHandler.findCurrencyBalanceByPortfolioId(currencyBalanceTrackQuery);
        return portfolioDataMapper.currencyBalanceToCurrencyBalanceTrackQueryResponse(currencyBalance);
    }

    @Override
    @Transactional(readOnly = true)
    public PortfolioTrackQueryResponse trackPortfolio(@Valid PortfolioTrackQuery portfolioTrackQuery) {
        Portfolio portfolio = portfolioTrackHandler.findPortfolioByUserId(portfolioTrackQuery);
        return portfolioDataMapper.PortfolioToTotalAssetValueTrackQueryResponse(portfolio);
    }

    @Override
    @Transactional(readOnly = true)
    public TotalBalanceTrackQueryResponse trackTotalBalances(@Valid TotalBalanceTrackQuery totalBalanceTrackQuery) {
        TotalBalanceContext balanceContext = portfolioTrackHandler.findBalances(totalBalanceTrackQuery);
        return portfolioDataMapper.totalBalanceContextToTotalBalanceTrackQueryResponse(balanceContext);
    }

    @Override
    @Transactional
    public DepositCreatedResponse deposit(@Valid DepositCreateCommand depositCreateCommand) {
        try {
            PaymentPayRequest request = portfolioDataMapper.depositCreateCommandToPaymentPayRequest(depositCreateCommand);
            PaymentResponse paymentResponse = portfolioPaymentHandler.pay(request);
            if (paymentResponse.getStatus().equals(PaymentStatus.DONE)) {
                DepositResultContext context = portfolioCreateHandler
                        .deposit(depositCreateCommand, paymentResponse);
                depositPublisher.publish(context.getDepositCreatedEvent());
                AssetChangeLog assetChangeLog = assetChangeLogHandler.saveDeposit(
                        context.getDepositCreatedEvent().getDomainType(), context.getBalance().getPortfolioId());
                log.info("saved Asset log: {}", assetChangeLog);
                return portfolioDataMapper.currencyBalanceToDepositCreatedResponse(context.getBalance(),
                        depositCreateCommand.getUserId(), paymentResponse.getTotalAmount());
            }
        } catch (Exception e) {
            throw new TossAPIException(e.getMessage());
        }
        throw new DepositFailedException(String.format("userId: %s is deposit failed. ",
                depositCreateCommand.getUserId()));
    }

//    @Override
//    @Transactional
//    public PortfolioCreatedResponse createPortfolio(@Valid PortfolioCreateCommand portfolioCreateCommand) {
//        Portfolio portfolio = portfolioCreateHandler.createPortfolio(portfolioCreateCommand);
//        return portfolioDataMapper.portfolioToPortfolioCreatedResponse(portfolio);
//    }

    @Override
    @Transactional
    public WithdrawalCreatedResponse withdrawal(@Valid WithdrawalCreateCommand withdrawalCreateCommand) {
        WithdrawalResultContext context = portfolioCreateHandler.withdrawal(withdrawalCreateCommand);
        AssetChangeLog assetChangeLog = assetChangeLogHandler.saveWithdrawal(context.getWithdrawalCreatedEvent().getDomainType(),
                context.getBalance().getPortfolioId());
        log.info("saved Asset log: {}", assetChangeLog);
        withdrawalPublisher.publish(context.getWithdrawalCreatedEvent());
        return portfolioDataMapper.currencyBalanceToWithdrawalCreatedResponse(
                context.getWithdrawalCreatedEvent().getDomainType()
                , "출금 완료");
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssetChangLogTrackQueryResponse> trackAllAssetChangLog(@Valid AssetChangLogTrackQuery assetChangLogTrackQuery) {
        List<AssetChangeLog> logList = assetChangeLogHandler.trackAssetChangLog(assetChangLogTrackQuery);
        return logList.stream().map(portfolioDataMapper::assetChangLogToAssetChangLogTrackQueryResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssetChangLogTrackQueryResponse> trackCryptoAssetChangLog(@Valid AssetChangLogTrackQuery query) {
        List<AssetChangeLog> logList = assetChangeLogHandler.trackCryptoAssetChangLogs(query);
        return logList.stream().map(portfolioDataMapper::assetChangLogToAssetChangLogTrackQueryResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssetChangLogTrackQueryResponse> trackDepositWithdrawalAssetChangLog(@Valid AssetChangLogTrackQuery query) {
        List<AssetChangeLog> logList = assetChangeLogHandler.trackDepositWithdrawalAssetChangLogs(query);
        return logList.stream().map(portfolioDataMapper::assetChangLogToAssetChangLogTrackQueryResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<AssetChangLogTrackQueryResponse> trackCryptoAssetChangLog(@Valid CryptoAssetTrackQuery query) {
        List<AssetChangeLog> logList = assetChangeLogHandler.trackCryptoAssetChangLogs(query);
        return logList.stream().map(portfolioDataMapper::assetChangLogToAssetChangLogTrackQueryResponse).toList();
    }
}
