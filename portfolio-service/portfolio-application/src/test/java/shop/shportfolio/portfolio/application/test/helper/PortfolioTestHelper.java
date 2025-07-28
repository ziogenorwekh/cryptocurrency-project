package shop.shportfolio.portfolio.application.test.helper;

import shop.shportfolio.portfolio.application.PortfolioApplicationServiceImpl;
import shop.shportfolio.portfolio.application.handler.PortfolioPaymentHandler;
import shop.shportfolio.portfolio.application.handler.PortfolioCreateHandler;
import shop.shportfolio.portfolio.application.handler.PortfolioTrackHandler;
import shop.shportfolio.portfolio.application.mapper.PortfolioDataMapper;
import shop.shportfolio.portfolio.application.port.input.PortfolioApplicationService;
import shop.shportfolio.portfolio.application.port.output.kafka.DepositKafkaPublisher;
import shop.shportfolio.portfolio.application.port.output.kafka.WithdrawalKafkaPublisher;
import shop.shportfolio.portfolio.application.port.output.payment.PaymentTossAPIPort;
import shop.shportfolio.portfolio.application.port.output.repository.PortfolioPaymentRepositoryPort;
import shop.shportfolio.portfolio.application.port.output.repository.PortfolioRepositoryPort;
import shop.shportfolio.portfolio.domain.*;

public class PortfolioTestHelper {

    public PortfolioApplicationService portfolioApplicationService;

    public PortfolioApplicationService createPortfolioApplicationService(
            PortfolioRepositoryPort portfolioRepositoryPort,
            PaymentTossAPIPort paymentTossAPIPort,
            PortfolioPaymentRepositoryPort portfolioPaymentRepositoryPort,
            DepositKafkaPublisher depositKafkaPublisher,
            WithdrawalKafkaPublisher withdrawalKafkaPublisher) {
        PortfolioDataMapper portfolioDataMapper = new PortfolioDataMapper();
        DepositWithdrawalDomainService depositWithdrawalDomainService = new DepositWithdrawalDomainServiceImpl();
        PaymentDomainService paymentDomainService = new PaymentDomainServiceImpl();
        PortfolioDomainService portfolioDomainService = new PortfolioDomainServiceImpl();
        PortfolioCreateHandler portfolioCreateHandler = new PortfolioCreateHandler(portfolioDomainService,
                portfolioRepositoryPort, depositWithdrawalDomainService);
        PortfolioPaymentHandler portfolioPaymentHandler = new PortfolioPaymentHandler(paymentTossAPIPort,
                portfolioPaymentRepositoryPort, paymentDomainService);
        PortfolioTrackHandler portfolioTrackHandler = new PortfolioTrackHandler(portfolioRepositoryPort);
        portfolioApplicationService = new PortfolioApplicationServiceImpl(portfolioTrackHandler,
                portfolioDataMapper, portfolioCreateHandler, portfolioPaymentHandler, depositKafkaPublisher,
                withdrawalKafkaPublisher);
        return portfolioApplicationService;
    }

}
