package shop.shportfolio.portfolio.application.test;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import shop.shportfolio.common.domain.valueobject.AssetCode;
import shop.shportfolio.portfolio.application.command.*;
import shop.shportfolio.portfolio.application.port.input.PortfolioApplicationService;
import shop.shportfolio.portfolio.application.port.output.payment.PaymentTossAPIPort;
import shop.shportfolio.portfolio.application.port.output.repository.PortfolioPaymentRepositoryPort;
import shop.shportfolio.portfolio.application.port.output.repository.PortfolioRepositoryPort;
import shop.shportfolio.portfolio.application.port.output.repository.PortfolioUserBalanceViewRepositoryPort;
import shop.shportfolio.portfolio.application.test.helper.PortfolioTestConstraints;
import shop.shportfolio.portfolio.application.test.helper.PortfolioTestHelper;
import shop.shportfolio.portfolio.domain.entity.Balance;
import shop.shportfolio.portfolio.domain.entity.Portfolio;
import shop.shportfolio.portfolio.domain.view.UserBalanceView;

import java.math.BigDecimal;
import java.util.Optional;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class PortfolioApplicationTest {

    @Mock
    private PortfolioRepositoryPort portfolioRepositoryPort;

    @Mock
    private PortfolioUserBalanceViewRepositoryPort portfolioUserBalanceViewRepositoryPort;

    @Mock
    private PaymentTossAPIPort paymentTossAPIPort;

    @Mock
    private PortfolioPaymentRepositoryPort portfolioPaymentRepositoryPort;

    private PortfolioApplicationService portfolioApplicationService;

    private PortfolioTestHelper helper;

    @BeforeEach
    public void setUp() {
        helper = new PortfolioTestHelper();
        portfolioApplicationService = helper.createPortfolioApplicationService(
                portfolioRepositoryPort,
                portfolioUserBalanceViewRepositoryPort,
                paymentTossAPIPort,
                portfolioPaymentRepositoryPort);
    }

    @Test
    @DisplayName("마켓 구매내역 조회 테스트")
    public void trackMarketBalanceTest() {
        // given
        MarketBalanceTrackQuery marketBalanceTrackQuery =
                new MarketBalanceTrackQuery(PortfolioTestConstraints.userId, PortfolioTestConstraints.marketId);
        Balance balance = PortfolioTestConstraints.balance;
        Mockito.when(portfolioRepositoryPort.findBalanceByPortfolioIdAndMarketId(Mockito.any(), Mockito.any()))
                .thenReturn(Optional.of(balance));
        // when
        MarketBalanceTrackQueryResponse response = portfolioApplicationService
                .trackMarketBalance(marketBalanceTrackQuery);

        // then 하이
        Assertions.assertNotNull(response);
        Assertions.assertEquals(PortfolioTestConstraints.marketId, response.getMarketId());
        Assertions.assertEquals(PortfolioTestConstraints.portfolioId, response.getPortfolioId());
        Assertions.assertEquals(PortfolioTestConstraints.quantity, response.getQuantity());
        Assertions.assertEquals(PortfolioTestConstraints.purchasePrice, response.getPurchasePrice());
        Mockito.verify(portfolioRepositoryPort, Mockito.times(1))
                .findBalanceByPortfolioIdAndMarketId(Mockito.any(), Mockito.any());
    }

    @Test
    @DisplayName("유저 밸런스 뷰 조회 테스트")
    public void trackUserBalanceViewTest() {
        // given
        UserBalanceView userBalanceView = PortfolioTestConstraints.userBalanceView;
        UserBalanceTrackQuery userBalanceTrackQuery = new UserBalanceTrackQuery(PortfolioTestConstraints.userId);
        Mockito.when(portfolioUserBalanceViewRepositoryPort.
                        findUserBalanceByUserId(PortfolioTestConstraints.userId))
                .thenReturn(Optional.of(userBalanceView));
        // when
        UserBalanceTrackQueryResponse response = portfolioApplicationService.trackUserBalance(userBalanceTrackQuery);
        // then
        Assertions.assertNotNull(response);
        Assertions.assertEquals(PortfolioTestConstraints.userId, response.getUserId());
        Assertions.assertEquals(PortfolioTestConstraints.money, response.getMoney());
        Assertions.assertEquals(AssetCode.KRW.name(), response.getAssetCode().name());
    }

    @Test
    @DisplayName("유저 자산 생성 테스트")
    public void trackPortfolioViewTest() {
        // given
//        TotalAssetValueTrackQuery query = new TotalAssetValueTrackQuery(PortfolioTestConstraints.portfolioId,
//                PortfolioTestConstraints.userId);
//        Mockito.when(portfolioRepositoryPort.findPortfolioByPortfolioIdAndUserId(Mockito.any(), Mockito.any()))
//                .thenReturn(Optional.of(PortfolioTestConstraints.portfolio));
//        // when
//        TotalAssetValueTrackQueryResponse response = portfolioApplicationService.trackTotalAssetValue(query);
//        // then
//        Mockito.verify(portfolioRepositoryPort, Mockito.times(1))
//                .findPortfolioByPortfolioIdAndUserId(Mockito.any(), Mockito.any());
//        Assertions.assertNotNull(response);
//        Assertions.assertEquals(PortfolioTestConstraints.portfolioId, response.getPortfolioId());
//        Assertions.assertEquals(PortfolioTestConstraints.userId, response.getUserId());
//        Assertions.assertEquals(BigDecimal.valueOf(1_000_000),response.getTotalAssetValue());
    }

    @Test
    @DisplayName("유저 포트폴리오 생성 테스트")
    public void createPortfolioTest() {
        // given
        PortfolioCreateCommand command = new PortfolioCreateCommand(PortfolioTestConstraints.userId);
        Mockito.when(portfolioRepositoryPort.savePortfolio(Mockito.any()))
                .thenReturn(PortfolioTestConstraints.newPortfolio);
        // when
        PortfolioCreatedResponse response = portfolioApplicationService.createPortfolio(command);
        // then
        Assertions.assertNotNull(response);
        Assertions.assertEquals(PortfolioTestConstraints.userId, response.getUserId());
        Assertions.assertEquals(PortfolioTestConstraints.portfolioId, response.getPortfolioId());
        Assertions.assertEquals(BigDecimal.ZERO, response.getAmount());
    }

    @Test
    @DisplayName("입금 테스트")
    public void depositTest() {
        // given

        // when

        // then
    }

}
