package shop.shportfolio.portfolio.api.resources.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import shop.shportfolio.portfolio.api.resources.PortfolioResources;
import shop.shportfolio.portfolio.application.command.create.PortfolioCreateCommand;
import shop.shportfolio.portfolio.application.command.create.PortfolioCreatedResponse;
import shop.shportfolio.portfolio.application.command.track.*;
import shop.shportfolio.portfolio.application.port.input.PortfolioApplicationService;
import shop.shportfolio.portfolio.domain.valueobject.ChangeType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class PortfolioResourcesTest {

    private final PortfolioApplicationService portfolioApplicationService = Mockito.mock(PortfolioApplicationService.class);
    private final PortfolioResources portfolioResources = new PortfolioResources(portfolioApplicationService);

    private static UUID tokenUserId = UUID.randomUUID();
    private static String marketId = "KRW-BTC";
    private static UUID portfolioId = UUID.randomUUID();
    private static BigDecimal purchasePrice = BigDecimal.TEN;
    private static BigDecimal quantity = BigDecimal.TEN;
    private static LocalDateTime updateTime = LocalDateTime.now(ZoneOffset.UTC);
    private static Long amount = 100L;
    @Test
    @DisplayName("암호화폐 보유 수량 조회 테스트")
    void trackCryptoBalanceShouldReturnCryptoBalance() {
        String marketId = "BTC-KRW";
        CryptoBalanceTrackQuery query = new CryptoBalanceTrackQuery();
        query.setMarketId(marketId);
        CryptoBalanceTrackQueryResponse expectedResponse = new
                CryptoBalanceTrackQueryResponse(tokenUserId, marketId, purchasePrice, quantity, updateTime);

        when(portfolioApplicationService.trackCryptoBalance(query)).thenReturn(expectedResponse);

        ResponseEntity<CryptoBalanceTrackQueryResponse> response = portfolioResources.trackCryptoBalance(marketId, query);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
    }

    @Test
    @DisplayName("원화 보유 수량 조회 테스트")
    void trackCurrencyBalanceShouldReturnCurrencyBalance() {
        CurrencyBalanceTrackQuery query = new CurrencyBalanceTrackQuery();
        CurrencyBalanceTrackQueryResponse expectedResponse =
                new CurrencyBalanceTrackQueryResponse(tokenUserId,amount,updateTime);

        when(portfolioApplicationService.trackCurrencyBalance(query)).thenReturn(expectedResponse);

        ResponseEntity<CurrencyBalanceTrackQueryResponse> response = portfolioResources.trackCurrencyBalance(query);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
    }

    @Test
    @DisplayName("포트폴리오 조회 테스트")
    void trackPortfolioShouldReturnPortfolio() {
        UUID userId = UUID.randomUUID();
        PortfolioTrackQueryResponse expectedResponse =
                new PortfolioTrackQueryResponse(portfolioId,userId,updateTime);

        when(portfolioApplicationService.trackPortfolio(any())).thenReturn(expectedResponse);

        ResponseEntity<PortfolioTrackQueryResponse> response = portfolioResources.trackPortfolio(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
    }

    @Test
    @DisplayName("총 자산 평가 조회 테스트")
    void trackTotalBalancesShouldReturnTotalBalances() {
        UUID userId = UUID.randomUUID();
        UUID portfolioId = UUID.randomUUID();
        TotalBalanceTrackQueryResponse expectedResponse = new
                TotalBalanceTrackQueryResponse(null,
                new ArrayList<>());

        when(portfolioApplicationService.trackTotalBalances(any())).thenReturn(expectedResponse);

        ResponseEntity<TotalBalanceTrackQueryResponse> response = portfolioResources
                .trackTotalBalances(userId, portfolioId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
    }

    @Test
    @DisplayName("포트폴리오 생성 테스트")
    void createPortfolioShouldReturnCreatedPortfolio() {
        UUID userId = UUID.randomUUID();
        PortfolioCreatedResponse expectedResponse = new PortfolioCreatedResponse(portfolioId,
                tokenUserId,LocalDateTime.now(ZoneOffset.UTC));

        when(portfolioApplicationService.createPortfolio(any())).thenReturn(expectedResponse);

        ResponseEntity<PortfolioCreatedResponse> response = portfolioResources.createPortfolio(userId);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
    }

    @Test
    @DisplayName("자산 변화 로그 조회 테스트")
    void trackAssetLogShouldReturnAssetLogs() {
        UUID userId = UUID.randomUUID();
        List<AssetChangLogTrackQueryResponse> expectedResponses = List.of(
                new AssetChangLogTrackQueryResponse(tokenUserId, ChangeType.DEPOSIT,marketId,BigDecimal.ONE
                ,"출금",LocalDateTime.now(ZoneOffset.UTC)));

        when(portfolioApplicationService.trackAssetChangLog(any())).thenReturn(expectedResponses);

        ResponseEntity<List<AssetChangLogTrackQueryResponse>> response = portfolioResources.trackAssetLog(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponses, response.getBody());
    }
}
