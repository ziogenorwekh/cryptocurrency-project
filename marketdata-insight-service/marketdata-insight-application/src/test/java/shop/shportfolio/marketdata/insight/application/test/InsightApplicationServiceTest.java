package shop.shportfolio.marketdata.insight.application.test;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import shop.shportfolio.common.domain.valueobject.MarketId;
import shop.shportfolio.marketdata.insight.application.InsightApplicationServiceImpl;
import shop.shportfolio.marketdata.insight.application.command.request.AiAnalysisTrackQuery;
import shop.shportfolio.marketdata.insight.application.command.response.AiAnalysisTrackResponse;
import shop.shportfolio.marketdata.insight.application.dto.ai.AiAnalysisResponseDto;
import shop.shportfolio.marketdata.insight.application.dto.candle.response.CandleMinuteResponseDto;
import shop.shportfolio.marketdata.insight.application.handler.AIAnalysisHandler;
import shop.shportfolio.marketdata.insight.application.mapper.MarketDataDtoMapper;
import shop.shportfolio.marketdata.insight.application.ports.input.AIAnalysisUseCase;
import shop.shportfolio.marketdata.insight.application.ports.input.InsightApplicationService;
import shop.shportfolio.marketdata.insight.application.ports.output.ai.OpenAiPort;
import shop.shportfolio.marketdata.insight.application.ports.output.bithumb.BithumbApiPort;
import shop.shportfolio.marketdata.insight.application.ports.output.repository.AIAnalysisResultRepositoryPort;
import shop.shportfolio.marketdata.insight.application.test.factory.CandleMinuteTestFactory;
import shop.shportfolio.marketdata.insight.application.usecase.AIAnalysisUseCaseImpl;
import shop.shportfolio.marketdata.insight.domain.MarketDataInsightDomainService;
import shop.shportfolio.marketdata.insight.domain.MarketDataInsightDomainServiceImpl;
import shop.shportfolio.marketdata.insight.domain.entity.AIAnalysisResult;
import shop.shportfolio.marketdata.insight.domain.valueobject.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class InsightApplicationServiceTest {


    private InsightApplicationService insightApplicationService;
    private MarketDataDtoMapper marketDataDtoMapper;
    private AIAnalysisUseCase aiAnalysisUseCase;
    @Mock
    private BithumbApiPort bithumbApiPort;
    @Mock
    private OpenAiPort openAiPort;

    private AIAnalysisHandler aiAnalysisHandler;
    @Mock
    private AIAnalysisResultRepositoryPort repositoryPort;
    private MarketDataInsightDomainService  marketDataInsightDomainService;

    private final String marketId = "KRW-BTC";
    private final PeriodType periodType = PeriodType.ONE_HOUR;
    private AIAnalysisResultId analysisResultId = new AIAnalysisResultId(UUID.randomUUID());
    private MarketId marketIdVO = new MarketId(marketId);
    private AnalysisTime analysisTimeVO = new AnalysisTime(LocalDateTime.now().atOffset(ZoneOffset.UTC));
    private PeriodEnd periodEndVO = new PeriodEnd(LocalDateTime.now().atOffset(ZoneOffset.UTC));
    private PeriodStart periodStartVO = new PeriodStart(LocalDateTime.now().minusMinutes(30).atOffset(ZoneOffset.UTC));
    private MomentumScore momentumScore = new MomentumScore(BigDecimal.TEN);
    private PriceTrend priceTrend = PriceTrend.UP;
    private Signal signal = Signal.BUY;
    private SummaryComment summaryComment = new SummaryComment("");
    @BeforeEach
    public void setUp() {
        marketDataInsightDomainService = new MarketDataInsightDomainServiceImpl();
        aiAnalysisHandler = new AIAnalysisHandler(repositoryPort,marketDataInsightDomainService);
        aiAnalysisUseCase = new AIAnalysisUseCaseImpl(aiAnalysisHandler);
        marketDataDtoMapper = new MarketDataDtoMapper();
        insightApplicationService = new InsightApplicationServiceImpl(marketDataDtoMapper, aiAnalysisUseCase);
    }

    @Disabled("AI 분석 스케쥴러가 담당")
    @Test
    @DisplayName("특정 기간 내에 AI 분석이 없다면 openAi, bithumb 을 호출해서 분석 결과를 도출하는 테스트")
    public void createAiAnalysisTest() {
        // given
        AiAnalysisTrackQuery query = new  AiAnalysisTrackQuery(marketId,periodType);
        Mockito.when(repositoryPort.findAIAnalysisResult(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(Optional.empty());
        AIAnalysisResult aiAnalysisResult = AIAnalysisResult.createAIAnalysisResult(analysisResultId,
                marketIdVO, analysisTimeVO, periodEndVO, periodStartVO, momentumScore, periodType,
                priceTrend, signal, summaryComment,summaryComment);
        Mockito.when(repositoryPort.saveAIAnalysisResult(Mockito.any())).thenReturn(aiAnalysisResult);
        List<CandleMinuteResponseDto> responseDtos = CandleMinuteTestFactory.candleMinuteResponseDtoOneHours();
        Mockito.when(bithumbApiPort.findCandleMinutes(Mockito.any()))
                .thenReturn(responseDtos);
        Mockito.when(openAiPort.analyzeOneHours(responseDtos.get(0).getMarketId(),responseDtos)).thenReturn(
                new AiAnalysisResponseDto(marketId, analysisTimeVO.getValue(), momentumScore.getValue(),
                        periodEndVO.getValue(),
                        periodStartVO.getValue(), periodType, priceTrend,
                        signal, summaryComment.getValue(),summaryComment.getValue())
        );
        // when
        AiAnalysisTrackResponse response = insightApplicationService.trackAiAnalysis(query);
        // then
        Assertions.assertNotNull(response);
        Assertions.assertEquals(marketId, response.getMarketId());
        Assertions.assertEquals(periodType, response.getPeriodType());
        Assertions.assertEquals(priceTrend, response.getPriceTrend());
        Assertions.assertEquals(signal, response.getSignal());
        Assertions.assertEquals(summaryComment.getValue(), response.getSummaryCommentEng());
        Mockito.verify(bithumbApiPort, Mockito.times(1)).findCandleMinutes(Mockito.any());
        Mockito.verify(openAiPort, Mockito.times(1)).analyzeOneHours(Mockito.any(),Mockito.any());
        Mockito.verify(repositoryPort, Mockito.times(1)).saveAIAnalysisResult(Mockito.any());
    }

    @Disabled("ai 분석 테스트 코드 다시 작성해야 함")
    @Test
    @DisplayName("특정 시간대에 ai 분석이 있다면 그걸 리턴하는 테스트")
    public void trackAiAnalysisTest() {
        // given
        AiAnalysisTrackQuery query = new  AiAnalysisTrackQuery(marketId,periodType);
        AIAnalysisResult aiAnalysisResult = AIAnalysisResult.createAIAnalysisResult(analysisResultId,
                marketIdVO, analysisTimeVO, periodEndVO, periodStartVO, momentumScore, periodType,
                priceTrend, signal, summaryComment,summaryComment);
        Mockito.when(repositoryPort.findAIAnalysisResult(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(Optional.of(aiAnalysisResult));
        // when
        AiAnalysisTrackResponse response = insightApplicationService.trackAiAnalysis(query);
        // then
        Assertions.assertNotNull(response);
        Assertions.assertEquals(marketId, response.getMarketId());
        Assertions.assertEquals(periodType, response.getPeriodType());
        Assertions.assertEquals(priceTrend, response.getPriceTrend());
        Mockito.verify(repositoryPort, Mockito.times(1)).findAIAnalysisResult(
                Mockito.any(),Mockito.any(),Mockito.any(),Mockito.any());
    }

}
