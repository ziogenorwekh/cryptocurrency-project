package shop.shportfolio.portfolio.application.test.helper;

import shop.shportfolio.portfolio.application.PortfolioApplicationServiceImpl;
import shop.shportfolio.portfolio.application.handler.PortfolioPaymentHandler;
import shop.shportfolio.portfolio.application.handler.PortfolioCreateHandler;
import shop.shportfolio.portfolio.application.handler.PortfolioTrackHandler;
import shop.shportfolio.portfolio.application.mapper.PortfolioDataMapper;
import shop.shportfolio.portfolio.application.port.input.PortfolioApplicationService;
import shop.shportfolio.portfolio.application.port.output.payment.PaymentTossAPIPort;
import shop.shportfolio.portfolio.application.port.output.repository.PortfolioPaymentRepositoryPort;
import shop.shportfolio.portfolio.application.port.output.repository.PortfolioRepositoryPort;
import shop.shportfolio.portfolio.application.port.output.repository.PortfolioUserBalanceViewRepositoryPort;
import shop.shportfolio.portfolio.domain.PaymentDomainService;
import shop.shportfolio.portfolio.domain.PaymentDomainServiceImpl;
import shop.shportfolio.portfolio.domain.PortfolioDomainService;
import shop.shportfolio.portfolio.domain.PortfolioDomainServiceImpl;

public class PortfolioTestHelper {

    public PortfolioApplicationService portfolioApplicationService;

    public PortfolioApplicationService createPortfolioApplicationService(
            PortfolioRepositoryPort portfolioRepositoryPort,
            PortfolioUserBalanceViewRepositoryPort portfolioUserBalanceViewRepositoryPort,
            PaymentTossAPIPort paymentTossAPIPort, PortfolioPaymentRepositoryPort portfolioPaymentRepositoryPort) {
        PortfolioDataMapper portfolioDataMapper = new PortfolioDataMapper();
        PaymentDomainService paymentDomainService = new PaymentDomainServiceImpl();
        PortfolioDomainService portfolioDomainService = new PortfolioDomainServiceImpl();
        PortfolioCreateHandler portfolioCreateHandler = new PortfolioCreateHandler(portfolioDomainService,
                portfolioRepositoryPort);
        PortfolioPaymentHandler portfolioPaymentHandler = new PortfolioPaymentHandler(paymentTossAPIPort,
                portfolioPaymentRepositoryPort,paymentDomainService);
        PortfolioTrackHandler portfolioTrackHandler = new PortfolioTrackHandler(portfolioRepositoryPort,
                portfolioUserBalanceViewRepositoryPort);
        portfolioApplicationService = new PortfolioApplicationServiceImpl(portfolioTrackHandler,
                portfolioDataMapper,portfolioCreateHandler, portfolioPaymentHandler);
        return portfolioApplicationService;
    }

}
