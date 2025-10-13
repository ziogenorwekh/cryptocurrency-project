package shop.shportfolio.portfolio.application.test;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import shop.shportfolio.portfolio.application.command.create.*;
import shop.shportfolio.portfolio.application.command.track.*;
import shop.shportfolio.portfolio.application.exception.DepositFailedException;
import shop.shportfolio.portfolio.application.exception.InvalidRequestException;
import shop.shportfolio.portfolio.application.port.input.PortfolioApplicationService;
import shop.shportfolio.portfolio.application.port.output.kafka.DepositPublisher;
import shop.shportfolio.portfolio.application.port.output.kafka.OutBoxRecorder;
import shop.shportfolio.portfolio.application.port.output.kafka.WithdrawalPublisher;
import shop.shportfolio.portfolio.application.port.output.payment.PaymentTossAPIPort;
import shop.shportfolio.portfolio.application.port.output.repository.AssetChangeLogRepositoryPort;
import shop.shportfolio.portfolio.application.port.output.repository.PortfolioPaymentRepositoryPort;
import shop.shportfolio.portfolio.application.port.output.repository.PortfolioRepositoryPort;
import shop.shportfolio.portfolio.application.test.helper.TestConstraints;
import shop.shportfolio.portfolio.application.test.helper.PortfolioTestHelper;
import shop.shportfolio.portfolio.domain.entity.AssetChangeLog;
import shop.shportfolio.portfolio.domain.entity.CryptoBalance;
import shop.shportfolio.portfolio.domain.entity.CurrencyBalance;
import shop.shportfolio.portfolio.domain.entity.DepositWithdrawal;
import java.util.List;
import java.util.Optional;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class PortfolioApplicationTest {

    @Mock
    private PortfolioRepositoryPort portfolioRepositoryPort;

    @Mock
    private PaymentTossAPIPort paymentTossAPIPort;

    @Mock
    private PortfolioPaymentRepositoryPort portfolioPaymentRepositoryPort;

    @Mock
    private DepositPublisher depositPublisher;

    @Mock
    private WithdrawalPublisher withdrawalPublisher;

    @Mock
    private AssetChangeLogRepositoryPort assetChangeLogRepositoryPort;

    private PortfolioApplicationService portfolioApplicationService;

    private PortfolioTestHelper helper;

    @Mock
    private OutBoxRecorder outBoxRecorder;
    @Captor
    ArgumentCaptor<CurrencyBalance> currencyBalanceArgumentCaptor;
    @Captor
    ArgumentCaptor<DepositWithdrawal> depositWithdrawalArgumentCaptor;

    @Captor
    ArgumentCaptor<AssetChangeLog> assetChangeLogArgumentCaptor;

    @BeforeEach
    public void setUp() {
        helper = new PortfolioTestHelper();
        portfolioApplicationService = helper.createPortfolioApplicationService(
                portfolioRepositoryPort,
                paymentTossAPIPort,
                portfolioPaymentRepositoryPort,
                depositPublisher,
                withdrawalPublisher,
                assetChangeLogRepositoryPort,
                outBoxRecorder);
    }

    @Test
    @DisplayName("암호화폐 마켓 구매내역 조회 테스트")
    public void trackMarketBalanceTest() {
        // given
        CryptoBalanceTrackQuery cryptoBalanceTrackQuery =
                new CryptoBalanceTrackQuery(TestConstraints.userId, TestConstraints.marketId);
        CryptoBalance balance = TestConstraints.cryptoBalance;
        Mockito.when(portfolioRepositoryPort.findCryptoBalanceByPortfolioIdAndMarketId(Mockito.any(), Mockito.any()))
                .thenReturn(Optional.of(balance));
        // when
        CryptoBalanceTrackQueryResponse response = portfolioApplicationService
                .trackCryptoBalance(cryptoBalanceTrackQuery);

        // then
        Assertions.assertNotNull(response);
        Assertions.assertEquals(TestConstraints.marketId, response.getMarketId());
        Assertions.assertEquals(TestConstraints.quantity, response.getQuantity());
        Assertions.assertEquals(TestConstraints.purchasePrice, response.getPurchasePrice());
        Mockito.verify(portfolioRepositoryPort, Mockito.times(1))
                .findCryptoBalanceByPortfolioIdAndMarketId(Mockito.any(), Mockito.any());
    }

    @Test
    @DisplayName("유저 자산 생성 테스트")
    public void trackPortfolioViewTest() {
        // given
        PortfolioTrackQuery query = new PortfolioTrackQuery(TestConstraints.userId);
        Mockito.when(portfolioRepositoryPort.findPortfolioByUserId(Mockito.any()))
                .thenReturn(Optional.of(TestConstraints.portfolio));
        // when
        PortfolioTrackQueryResponse response = portfolioApplicationService.trackPortfolio(query);
        // then
        Mockito.verify(portfolioRepositoryPort, Mockito.times(1))
                .findPortfolioByUserId(Mockito.any());
        Assertions.assertNotNull(response);
        Assertions.assertEquals(TestConstraints.portfolioId, response.getPortfolioId());
        Assertions.assertEquals(TestConstraints.userId, response.getUserId());
    }

//    @Test
//    @DisplayName("유저 포트폴리오 생성 테스트")
//    public void createPortfolioTest() {
//        // given
//        PortfolioCreateCommand command = new PortfolioCreateCommand(TestConstraints.userId);
//        Mockito.when(portfolioRepositoryPort.savePortfolio(Mockito.any()))
//                .thenReturn(TestConstraints.newPortfolio);
//        // when
//        PortfolioCreatedResponse response = portfolioApplicationService.createPortfolio(command);
//        // then
//        Assertions.assertNotNull(response);
//        Assertions.assertEquals(TestConstraints.userId, response.getUserId());
//        Assertions.assertEquals(TestConstraints.portfolioId, response.getPortfolioId());
//    }

    @Test
    @DisplayName("입금 테스트")
    public void depositTest() {
        // given
        DepositCreateCommand command = new DepositCreateCommand(TestConstraints.userId,
                String.valueOf(TestConstraints.money.longValue()),
                TestConstraints.orderId, TestConstraints.paymentKey);
        Mockito.when(paymentTossAPIPort.pay(Mockito.any())).thenReturn(TestConstraints.paymentResponseDone);
        Mockito.when(portfolioRepositoryPort.findPortfolioByUserId(TestConstraints.userId))
                .thenReturn(Optional.of(TestConstraints.portfolio));
        Mockito.when(portfolioRepositoryPort.findCurrencyBalanceByPortfolioId(TestConstraints.portfolioId
                ,TestConstraints.userId))
                .thenReturn(Optional.of(TestConstraints.currencyBalance));
        // when
        DepositCreatedResponse depositCreatedResponse = portfolioApplicationService.deposit(command);
        Mockito.verify(portfolioRepositoryPort, Mockito.times(1)).saveDepositWithdrawal(
                depositWithdrawalArgumentCaptor.capture());
        Mockito.verify(portfolioRepositoryPort, Mockito.times(1)).saveCurrencyBalance(
                currencyBalanceArgumentCaptor.capture());
        Mockito.verify(assetChangeLogRepositoryPort, Mockito.times(1)).save(
                assetChangeLogArgumentCaptor.capture());
        CurrencyBalance currencyBalance = currencyBalanceArgumentCaptor.getValue();
        DepositWithdrawal depositWithdrawal = depositWithdrawalArgumentCaptor.getValue();
        AssetChangeLog assetChangeLog = assetChangeLogArgumentCaptor.getValue();
        // then
        Assertions.assertNotNull(currencyBalance);
        Assertions.assertNotNull(depositWithdrawal);
        Assertions.assertEquals(TestConstraints.money, depositWithdrawal.getAmount().getValue());
        Assertions.assertEquals(TestConstraints.money.longValue()*2, currencyBalance.getAmount().getValue().longValue());
        Assertions.assertNotNull(depositCreatedResponse);
        Assertions.assertEquals(TestConstraints.userId, depositCreatedResponse.getUserId());
        Assertions.assertEquals(depositWithdrawal.getAmount(),assetChangeLog.getChangeMoney());
        Assertions.assertEquals(TestConstraints.portfolioId,assetChangeLog.getPortfolioId().getValue());
        Mockito.verify(assetChangeLogRepositoryPort, Mockito.times(1)).save(Mockito.any(AssetChangeLog.class));
    }

    @Test
    @DisplayName("입금 테스트인데, 토스페이 결제가 실패한 경우")
    public void failedTossAPITest() {
        // given
        DepositCreateCommand command = new DepositCreateCommand(TestConstraints.userId,
                String.valueOf(TestConstraints.money.longValue()),
                TestConstraints.orderId, TestConstraints.paymentKey);
        Mockito.when(paymentTossAPIPort.pay(Mockito.any())).thenReturn(TestConstraints.paymentResponseFAILED);
        // when
        DepositFailedException depositFailedException = Assertions.assertThrows(
                DepositFailedException.class, () -> portfolioApplicationService.deposit(command));
        // then
        Assertions.assertNotNull(depositFailedException);
        Assertions.assertEquals(String.format("userId: %s is deposit failed. ",
                TestConstraints.userId), depositFailedException.getMessage());
        Mockito.verify(depositPublisher, Mockito.times(0))
                .publish(Mockito.any());

    }

    @Test
    @DisplayName("출금 테스트")
    public void withdrawalTest() {
        // given
        WithdrawalCreateCommand command = new WithdrawalCreateCommand(TestConstraints.userId,
                "123-123-123", "국민은행", 1_000_000L);
        Mockito.when(portfolioRepositoryPort.findPortfolioByUserId(TestConstraints.userId))
                .thenReturn(Optional.of(TestConstraints.portfolio));
        Mockito.when(portfolioRepositoryPort.findCurrencyBalanceByPortfolioId(TestConstraints.portfolioId
                ,TestConstraints.userId))
                .thenReturn(Optional.of(TestConstraints.currencyBalance_1_200_000));
        // when
        WithdrawalCreatedResponse response = portfolioApplicationService.withdrawal(command);
        // then
        Assertions.assertNotNull(response);
        Assertions.assertEquals(TestConstraints.userId, response.getUserId());
        Assertions.assertEquals(1_000_000L, response.getWithdrawalAmount());
        Mockito.verify(withdrawalPublisher, Mockito.times(1)).publish(Mockito.any());
        Mockito.verify(portfolioRepositoryPort, Mockito.times(1)).saveDepositWithdrawal(Mockito.any());
    }

    @Test
    @DisplayName("잔액보다 많은 금액을 출금하려는 경우 에러나는 테스트")
    public void withdrawalFailedTest() {
        // given
        WithdrawalCreateCommand command = new WithdrawalCreateCommand(TestConstraints.userId, "123-123-123",
                "국민은행", 1_000_000L);
        Mockito.when(portfolioRepositoryPort.findPortfolioByUserId(TestConstraints.userId))
                .thenReturn(Optional.of(TestConstraints.portfolio));
        Mockito.when(portfolioRepositoryPort.findCurrencyBalanceByPortfolioId(TestConstraints.portfolioId,
                        TestConstraints.userId))
                .thenReturn(Optional.of(TestConstraints.currencyBalance_900_000));
        // when
        InvalidRequestException aThrows = Assertions.assertThrows(InvalidRequestException.class, () ->
                portfolioApplicationService.withdrawal(command));
        // then
        Assertions.assertNotNull(aThrows);
        Assertions.assertEquals("Withdrawal amount 1000000 exceeds available currency balance 900000",
                aThrows.getMessage());
    }

    @Test
    @DisplayName("현금 자산 조회 테스트")
    public void currencyBalanceTest() {
        // given
        CurrencyBalanceTrackQuery query = new CurrencyBalanceTrackQuery(TestConstraints.portfolioId,
                TestConstraints.userId);
        Mockito.when(portfolioRepositoryPort.findCurrencyBalanceByPortfolioId(TestConstraints.portfolioId,
                        TestConstraints.userId))
                .thenReturn(Optional.of(TestConstraints.currencyBalance));
        // when
        CurrencyBalanceTrackQueryResponse response = portfolioApplicationService.trackCurrencyBalance(query);
        // then
        Assertions.assertNotNull(response);
        Assertions.assertEquals(TestConstraints.currencyBalance.getAmount().getValue().longValue(),
                response.getAmount());
        Mockito.verify(portfolioRepositoryPort, Mockito.times(1))
                .findCurrencyBalanceByPortfolioId(Mockito.any(), Mockito.any());
    }

    @Test
    @DisplayName("암호화폐 자산 조회 테스트")
    public void cryptocurrencyBalanceTest() {
        // given
        CryptoBalanceTrackQuery query = new CryptoBalanceTrackQuery(TestConstraints.portfolioId,
                TestConstraints.marketId);
        Mockito.when(portfolioRepositoryPort.findCryptoBalanceByPortfolioIdAndMarketId(TestConstraints.portfolioId
        , TestConstraints.marketId)).thenReturn(Optional.of(TestConstraints.cryptoBalance));
        // when
        CryptoBalanceTrackQueryResponse response = portfolioApplicationService.trackCryptoBalance(query);
        // then
        Assertions.assertNotNull(response);
        Assertions.assertEquals(TestConstraints.marketId, response.getMarketId());
        Assertions.assertEquals(TestConstraints.purchasePrice, response.getPurchasePrice());
        Assertions.assertEquals(TestConstraints.quantity, response.getQuantity());
        Mockito.verify(portfolioRepositoryPort, Mockito.times(1))
                .findCryptoBalanceByPortfolioIdAndMarketId(Mockito.any(), Mockito.any());
    }

    @Test
    @DisplayName("전체 자산 조회 테스트")
    public void totalValueRetrieveTest() {
        // given
        TotalBalanceTrackQuery query = new TotalBalanceTrackQuery(TestConstraints.portfolioId, TestConstraints.userId);
        Mockito.when(portfolioRepositoryPort.findCryptoBalancesByPortfolioId(TestConstraints.portfolioId))
                .thenReturn(List.of(
                        TestConstraints.cryptoBalance1,
                        TestConstraints.cryptoBalance2,
                        TestConstraints.cryptoBalance3,
                        TestConstraints.cryptoBalance4,
                        TestConstraints.cryptoBalance5,
                        TestConstraints.cryptoBalance6,
                        TestConstraints.cryptoBalance7
                ));
        Mockito.when(portfolioRepositoryPort.findCurrencyBalanceByPortfolioId(TestConstraints.portfolioId,
                        TestConstraints.userId))
                .thenReturn(Optional.of(TestConstraints.currencyBalance_900_000));
        // when
        TotalBalanceTrackQueryResponse response = portfolioApplicationService.trackTotalBalances(query);
        // then
        Assertions.assertNotNull(response);
        Assertions.assertEquals(7,response.getCryptoBalanceTrackQueryResponses().size());
        Mockito.verify(portfolioRepositoryPort, Mockito.times(1))
                .findCryptoBalancesByPortfolioId(TestConstraints.portfolioId);
        Mockito.verify(portfolioRepositoryPort, Mockito.times(1))
                .findCurrencyBalanceByPortfolioId(TestConstraints.portfolioId,
                        TestConstraints.userId);
    }
}
