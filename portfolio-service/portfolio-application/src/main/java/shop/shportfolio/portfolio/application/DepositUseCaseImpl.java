package shop.shportfolio.portfolio.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import shop.shportfolio.common.domain.dto.payment.PaymentPayRequest;
import shop.shportfolio.common.domain.dto.payment.PaymentResponse;
import shop.shportfolio.common.domain.valueobject.PaymentMethod;
import shop.shportfolio.common.domain.valueobject.PaymentStatus;
import shop.shportfolio.portfolio.application.command.create.DepositCreateCommand;
import shop.shportfolio.portfolio.application.command.create.DepositCreatedResponse;
import shop.shportfolio.portfolio.application.dto.DepositResultContext;
import shop.shportfolio.portfolio.application.exception.DepositFailedException;
import shop.shportfolio.portfolio.application.exception.TossAPIException;
import shop.shportfolio.portfolio.application.handler.AssetChangeLogHandler;
import shop.shportfolio.portfolio.application.handler.PortfolioCreateHandler;
import shop.shportfolio.portfolio.application.handler.PortfolioPaymentHandler;
import shop.shportfolio.portfolio.application.mapper.PortfolioDataMapper;
import shop.shportfolio.portfolio.application.port.input.DepositUseCase;
import shop.shportfolio.portfolio.application.port.output.kafka.OutBoxRecorder;
import shop.shportfolio.portfolio.domain.entity.AssetChangeLog;

import java.time.LocalDateTime;

@Slf4j
@Component
public class DepositUseCaseImpl implements DepositUseCase {

    private final PortfolioCreateHandler portfolioCreateHandler;
    private final AssetChangeLogHandler assetChangeLogHandler;
    private final PortfolioDataMapper portfolioDataMapper;
    private final PortfolioPaymentHandler portfolioPaymentHandler;
    private final OutBoxRecorder outBoxRecorder;
    public DepositUseCaseImpl(PortfolioCreateHandler portfolioCreateHandler,
                              AssetChangeLogHandler assetChangeLogHandler,
                              PortfolioDataMapper portfolioDataMapper,
                              PortfolioPaymentHandler portfolioPaymentHandler,
                              OutBoxRecorder outBoxRecorder) {
        this.portfolioCreateHandler = portfolioCreateHandler;
        this.assetChangeLogHandler = assetChangeLogHandler;
        this.portfolioDataMapper = portfolioDataMapper;
        this.portfolioPaymentHandler = portfolioPaymentHandler;
        this.outBoxRecorder = outBoxRecorder;
    }

    @Override
    @Transactional
    public DepositResultContext deposit(DepositCreateCommand depositCreateCommand) {
        try {
            PaymentPayRequest request = portfolioDataMapper.depositCreateCommandToPaymentPayRequest(depositCreateCommand);
            PaymentResponse paymentResponse = portfolioPaymentHandler.pay(request);
            if (paymentResponse.getStatus().equals(PaymentStatus.DONE)) {
                DepositResultContext context = portfolioCreateHandler
                        .deposit(depositCreateCommand, paymentResponse);
                AssetChangeLog assetChangeLog = assetChangeLogHandler.saveDeposit(
                        context.getDepositCreatedEvent().getDomainType(), context.getBalance().getPortfolioId());
                log.info("saved Asset log: {}", assetChangeLog);
                outBoxRecorder.saveDepositEvent(context.getDepositCreatedEvent());
                return context;
            }
        } catch (Exception e) {
            throw new TossAPIException(e.getMessage());
        }
        throw new DepositFailedException(String.format("userId: %s is deposit failed. ",
                depositCreateCommand.getUserId()));
    }

    @Override
    @Transactional
    public DepositResultContext depositMock(DepositCreateCommand depositCreateCommand) {
        PaymentResponse paymentResponse = new PaymentResponse(depositCreateCommand.getPaymentKey(),
                depositCreateCommand.getOrderId(),
                Long.parseLong(depositCreateCommand.getAmount()), PaymentMethod.EASY_PAY
                , PaymentStatus.DONE, LocalDateTime.now(),LocalDateTime.now(),"Mock Deposit",
                "Mock Response");
        DepositResultContext context = portfolioCreateHandler
                .deposit(depositCreateCommand, paymentResponse);
        AssetChangeLog assetChangeLog = assetChangeLogHandler.saveDeposit(
                context.getDepositCreatedEvent().getDomainType(), context.getBalance().getPortfolioId());
        log.info("saved Asset log: {}", assetChangeLog);
        outBoxRecorder.saveDepositEvent(context.getDepositCreatedEvent());
        return context;
    }
}
