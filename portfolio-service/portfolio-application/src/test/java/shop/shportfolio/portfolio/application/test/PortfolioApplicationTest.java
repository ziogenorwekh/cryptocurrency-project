package shop.shportfolio.portfolio.application.test;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import shop.shportfolio.common.domain.valueobject.AssetCode;
import shop.shportfolio.portfolio.application.command.MarketBalanceTrackQuery;
import shop.shportfolio.portfolio.application.command.MarketBalanceTrackQueryResponse;
import shop.shportfolio.portfolio.application.command.UserBalanceTrackQuery;
import shop.shportfolio.portfolio.application.command.UserBalanceTrackQueryResponse;
import shop.shportfolio.portfolio.application.port.input.PortfolioApplicationService;
import shop.shportfolio.portfolio.application.port.output.repository.PortfolioRepositoryPort;
import shop.shportfolio.portfolio.application.port.output.repository.PortfolioUserBalanceViewRepositoryPort;
import shop.shportfolio.portfolio.application.test.helper.PortfolioTestConstraints;
import shop.shportfolio.portfolio.application.test.helper.PortfolioTestHelper;
import shop.shportfolio.portfolio.domain.entity.Balance;
import shop.shportfolio.portfolio.domain.view.UserBalanceView;

import java.util.Optional;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class PortfolioApplicationTest {

    @Mock
    private PortfolioRepositoryPort portfolioRepositoryPort;

    @Mock
    private PortfolioUserBalanceViewRepositoryPort portfolioUserBalanceViewRepositoryPort;

    private PortfolioApplicationService portfolioApplicationService;

    private PortfolioTestHelper helper;

    @BeforeEach
    public void setUp() {
        helper = new PortfolioTestHelper();
        portfolioApplicationService = helper.createPortfolioApplicationService(
                portfolioRepositoryPort,
                portfolioUserBalanceViewRepositoryPort);
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

        // then
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
    @DisplayName("유저 자산 조회 테스트")
    public void trackPortfolioViewTest() {
        // given

        // when

        // then
    }
}
