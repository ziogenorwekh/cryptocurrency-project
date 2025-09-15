package shop.shportfolio.portfolio.application.test.helper;

import shop.shportfolio.portfolio.application.PortfolioApplicationServiceImpl;
import shop.shportfolio.portfolio.application.handler.AssetChangeLogHandler;
import shop.shportfolio.portfolio.application.handler.PortfolioPaymentHandler;
import shop.shportfolio.portfolio.application.handler.PortfolioCreateHandler;
import shop.shportfolio.portfolio.application.handler.PortfolioTrackHandler;
import shop.shportfolio.portfolio.application.mapper.PortfolioDataMapper;
import shop.shportfolio.portfolio.application.port.input.PortfolioApplicationService;
import shop.shportfolio.portfolio.application.port.output.kafka.DepositPublisher;
import shop.shportfolio.portfolio.application.port.output.kafka.WithdrawalPublisher;
import shop.shportfolio.portfolio.application.port.output.payment.PaymentTossAPIPort;
import shop.shportfolio.portfolio.application.port.output.repository.AssetChangeLogRepositoryPort;
import shop.shportfolio.portfolio.application.port.output.repository.PortfolioPaymentRepositoryPort;
import shop.shportfolio.portfolio.application.port.output.repository.PortfolioRepositoryPort;
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
        AssetChangeLogHandler assetChangeLogHandler = new AssetChangeLogHandler(assetChangeLogRepositoryPort,assetChangeLogDomainService);
        portfolioApplicationService = new PortfolioApplicationServiceImpl(portfolioTrackHandler,
                portfolioDataMapper, portfolioCreateHandler, portfolioPaymentHandler, depositPublisher,
                withdrawalPublisher,assetChangeLogHandler);
        return portfolioApplicationService;
    }

}
