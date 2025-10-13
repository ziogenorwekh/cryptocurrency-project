package shop.shportfolio.marketdata.insight.api.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import shop.shportfolio.marketdata.insight.api.InsightResources;
import shop.shportfolio.marketdata.insight.application.command.request.AiAnalysisTrackQuery;
import shop.shportfolio.marketdata.insight.application.command.response.AiAnalysisTrackResponse;
import shop.shportfolio.marketdata.insight.application.ports.input.usecase.InsightApplicationService;
import shop.shportfolio.marketdata.insight.domain.valueobject.PeriodType;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class InsightResourcesTest {

    private MockMvc mockMvc;
    private InsightApplicationService insightApplicationService;

    @BeforeEach
    void setUp() {
        insightApplicationService = Mockito.mock(InsightApplicationService.class);
        InsightResources insightResources = new InsightResources(insightApplicationService);
        mockMvc = MockMvcBuilders.standaloneSetup(insightResources).build();
    }

    @Test
    @DisplayName("AI 분석 추적 API 정상 응답 테스트")
    void trackAiAnalysis_success() throws Exception {
        AiAnalysisTrackResponse mockResponse = AiAnalysisTrackResponse.builder()
                .marketId("test-market")
                .analysisTime(null)
                .momentumScore(null)
                .periodEnd(null)
                .periodStart(null)
                .periodType(null)
                .priceTrend(null)
                .signal(null)
                .summaryCommentEng("Test summary")
                .summaryCommentKor("Test summary")
                .build();
        Mockito.when(insightApplicationService.trackAiAnalysis(any(AiAnalysisTrackQuery.class)))
                .thenReturn(mockResponse);

        mockMvc.perform(get("/api/insights/ai-analysis/track/test-market")
                        .param("periodType", PeriodType.ONE_HOUR.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.marketId").value("test-market"));
    }
}