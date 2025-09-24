package shop.shportfolio.portfolio.application.test;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import shop.shportfolio.common.domain.valueobject.TransactionStatus;
import shop.shportfolio.portfolio.application.command.create.WithdrawalCreateCommand;
import shop.shportfolio.portfolio.application.handler.PortfolioCreateHandler;
import shop.shportfolio.portfolio.application.handler.PortfolioUpdateHandler;
import shop.shportfolio.portfolio.application.port.output.kafka.WithdrawalPublisher;
import shop.shportfolio.portfolio.application.port.output.repository.PortfolioRepositoryPort;
import shop.shportfolio.portfolio.application.saga.WithdrawalSaga;
import shop.shportfolio.portfolio.application.test.helper.PortfolioTestHelper;
import shop.shportfolio.portfolio.application.test.helper.TestConstraints;
import shop.shportfolio.portfolio.domain.DepositWithdrawalDomainService;
import shop.shportfolio.portfolio.domain.DepositWithdrawalDomainServiceImpl;
import shop.shportfolio.portfolio.domain.PortfolioDomainService;
import shop.shportfolio.portfolio.domain.PortfolioDomainServiceImpl;
import shop.shportfolio.portfolio.domain.entity.DepositWithdrawal;
import shop.shportfolio.portfolio.domain.entity.Portfolio;
import shop.shportfolio.portfolio.domain.event.WithdrawalCreatedEvent;

import java.util.Optional;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class WithdrawalSageTest {


    private WithdrawalSaga withdrawalSaga;
    @Mock
    private PortfolioRepositoryPort repositoryPort;
    @Mock
    private WithdrawalPublisher publisher;

    private PortfolioUpdateHandler portfolioUpdateHandler;
    private PortfolioCreateHandler portfolioCreateHandler;

    @BeforeEach
    public void setUp() {
        DepositWithdrawalDomainService depositWithdrawalDomainService = new DepositWithdrawalDomainServiceImpl();
        PortfolioDomainService portfolioDomainService = new PortfolioDomainServiceImpl();
        portfolioUpdateHandler = new PortfolioUpdateHandler(repositoryPort, portfolioDomainService);
        portfolioCreateHandler = new PortfolioCreateHandler(portfolioDomainService,
                repositoryPort, depositWithdrawalDomainService);
        withdrawalSaga = new WithdrawalSaga(portfolioCreateHandler, portfolioUpdateHandler, publisher);
    }

    @Test
    @DisplayName("출금 생성 테스트")
    public void createWithdrawalTest() {
        // given
        WithdrawalCreateCommand command = new WithdrawalCreateCommand(TestConstraints.userId,
                "Test Bank", "Test Message", 50000L);
        Portfolio portfolio = TestConstraints.portfolio;

        Mockito.when(repositoryPort.findPortfolioByUserId(TestConstraints.userId))
                .thenReturn(Optional.ofNullable(portfolio));
        Mockito.when(repositoryPort.findCurrencyBalanceByPortfolioId(portfolio.getId().getValue(), TestConstraints.userId))
                .thenReturn(Optional.ofNullable(TestConstraints.currencyBalance));

        // when
        DepositWithdrawal depositWithdrawal = withdrawalSaga.createWithdrawalSaga(command);
        // then
        Assertions.assertNotNull(depositWithdrawal);
        Assertions.assertEquals(depositWithdrawal.getAmount().getValue().longValue(), command.getAmount());
        Mockito.verify(repositoryPort, Mockito.times(1))
                .findPortfolioByUserId(TestConstraints.userId);
        Assertions.assertEquals(TransactionStatus.PENDING, depositWithdrawal.getTransactionStatus());
        Mockito.verify(repositoryPort, Mockito.times(1))
                .findCurrencyBalanceByPortfolioId(portfolio.getId().getValue(), TestConstraints.userId);
        Mockito.verify(repositoryPort, Mockito.times(1))
                .saveCurrencyBalance(TestConstraints.currencyBalance);
        Mockito.verify(publisher, Mockito.times(1))
                .publish(Mockito.any(WithdrawalCreatedEvent.class));
    }
}
