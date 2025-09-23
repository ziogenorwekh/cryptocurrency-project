package shop.shportfolio.marketdata.insight.api.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import shop.shportfolio.marketdata.insight.api.MarketDataResources;
import shop.shportfolio.marketdata.insight.application.command.request.CandleMinuteTrackQuery;
import shop.shportfolio.marketdata.insight.application.command.request.MarketTrackQuery;
import shop.shportfolio.marketdata.insight.application.command.response.*;
import shop.shportfolio.marketdata.insight.application.ports.input.MarketDataApplicationService;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class MarketDataResourcesTest {

    private MockMvc mockMvc;
    private MarketDataApplicationService marketDataApplicationService;

    @BeforeEach
    void setUp() {
        marketDataApplicationService = Mockito.mock(MarketDataApplicationService.class);
        MarketDataResources marketDataResources = new MarketDataResources(marketDataApplicationService);
        mockMvc = MockMvcBuilders.standaloneSetup(marketDataResources).build();
    }

    @Test
    @DisplayName("시장 코드 추적 API 정상 응답 테스트")
    void trackMarketCodes_success() throws Exception {
        // given
        MarketCodeTrackResponse mockResponse = new MarketCodeTrackResponse(
                "KRW-BTC", "test-market", "Test Market");
        Mockito.when(marketDataApplicationService.findMarketByMarketId(any(MarketTrackQuery.class)))
                .thenReturn(mockResponse);
        // when & then
        mockMvc.perform(get("/api/markets/test-market/track")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("모든 시장 조회 API 정상 응답 테스트")
    void findAllMarkets_success() throws Exception {
        // given
        List<MarketCodeTrackResponse> mockList = Collections.singletonList(new MarketCodeTrackResponse(
                "KRW-BTC", "test-market", "Test Market"));
        Mockito.when(marketDataApplicationService.findAllMarkets())
                .thenReturn(mockList);

        // when & then
        mockMvc.perform(get("/api/markets")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("분 단위 캔들 데이터 추적 API 정상 응답 테스트")
    void trackCandleMinutes_success() throws Exception {

        // given
        CandleMinuteTrackResponse mockResponse = new CandleMinuteTrackResponse("test-market", "2024-01-01T00:00:00KST",
                BigDecimal.valueOf(1000.0), BigDecimal.valueOf(1100.0), BigDecimal.valueOf(900.0),
                BigDecimal.valueOf(1050.0),
                1700000000000L, BigDecimal.valueOf(5000.0),
                BigDecimal.valueOf(5.0), 1);
        List<CandleMinuteTrackResponse> mockList = Collections.singletonList(mockResponse);
        Mockito.when(marketDataApplicationService.findCandleMinute(any(CandleMinuteTrackQuery.class)))
                .thenReturn(mockList);

        // when & then
        mockMvc.perform(get("/api/candles/test-market/minutes/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}