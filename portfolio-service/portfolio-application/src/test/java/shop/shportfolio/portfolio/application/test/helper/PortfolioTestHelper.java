package shop.shportfolio.portfolio.application.test.helper;

import shop.shportfolio.portfolio.application.DepositUseCaseImpl;
import shop.shportfolio.portfolio.application.PortfolioApplicationServiceImpl;
import shop.shportfolio.portfolio.application.handler.*;
import shop.shportfolio.portfolio.application.mapper.PortfolioDataMapper;
import shop.shportfolio.portfolio.application.port.input.DepositUseCase;
import shop.shportfolio.portfolio.application.port.input.PortfolioApplicationService;
import shop.shportfolio.portfolio.application.port.output.kafka.DepositPublisher;
import shop.shportfolio.portfolio.application.port.output.kafka.WithdrawalPublisher;
import shop.shportfolio.portfolio.application.port.output.payment.PaymentTossAPIPort;
import shop.shportfolio.portfolio.application.port.output.repository.AssetChangeLogRepositoryPort;
import shop.shportfolio.portfolio.application.port.output.repository.PortfolioPaymentRepositoryPort;
import shop.shportfolio.portfolio.application.port.output.repository.PortfolioRepositoryPort;
import shop.shportfolio.portfolio.application.saga.WithdrawalSaga;
import shop.shportfolio.portfolio.domain.*;

public class PortfolioTestHelper {

    public PortfolioApplicationService portfolioApplicationService;

    public PortfolioApplicationService createPortfolioApplicationService(
            PortfolioRepositoryPort portfolioRepositoryPort,
            PaymentTossAPIPort paymentTossAPIPort,
            PortfolioPaymentRepositoryPort portfolioPaymentRepositoryPort,
            DepositPublisher depositPublisher,
            WithdrawalPublisher withdrawalPublisher,
            AssetChangeLogRepositoryPort assetChangeLogRepositoryPort) {
        AssetChangeLogDomainService assetChangeLogDomainService = new AssetChangeLogDomainServiceImpl();
        PortfolioDataMapper portfolioDataMapper = new PortfolioDataMapper();
        DepositWithdrawalDomainService depositWithdrawalDomainService = new DepositWithdrawalDomainServiceImpl();
        PaymentDomainService paymentDomainService = new PaymentDomainServiceImpl();
        PortfolioDomainService portfolioDomainService = new PortfolioDomainServiceImpl();
        PortfolioCreateHandler portfolioCreateHandler = new PortfolioCreateHandler(portfolioDomainService,
                portfolioRepositoryPort, depositWithdrawalDomainService);
        PortfolioPaymentHandler portfolioPaymentHandler = new PortfolioPaymentHandler(paymentTossAPIPort,
                portfolioPaymentRepositoryPort, paymentDomainService);
        PortfolioTrackHandler portfolioTrackHandler = new PortfolioTrackHandler(portfolioRepositoryPort);
        AssetChangeLogHandler assetChangeLogHandler = new AssetChangeLogHandler(assetChangeLogRepositoryPort, assetChangeLogDomainService);
        PortfolioUpdateHandler portfolioUpdateHandler = new PortfolioUpdateHandler(portfolioRepositoryPort,
                portfolioDomainService);
        WithdrawalSaga withdrawalSaga = new WithdrawalSaga(portfolioCreateHandler,
                portfolioUpdateHandler,
                withdrawalPublisher,assetChangeLogHandler);
        DepositUseCase depositUseCase = new DepositUseCaseImpl(portfolioCreateHandler,assetChangeLogHandler
        ,portfolioDataMapper, portfolioPaymentHandler);
        portfolioApplicationService = new PortfolioApplicationServiceImpl(portfolioTrackHandler,
                portfolioDataMapper, depositPublisher,
                assetChangeLogHandler, withdrawalSaga, depositUseCase);
        return portfolioApplicationService;
    }

}
