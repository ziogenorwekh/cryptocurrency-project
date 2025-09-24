package shop.shportfolio.marketdata.insight.application.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import shop.shportfolio.common.domain.valueobject.MarketId;
import shop.shportfolio.marketdata.insight.application.dto.ai.AiAnalysisResponseDto;
import shop.shportfolio.marketdata.insight.application.handler.AIAnalysisHandler;
import shop.shportfolio.marketdata.insight.application.ports.output.ai.OpenAiPort;
import shop.shportfolio.marketdata.insight.application.ports.output.bithumb.BithumbApiPort;
import shop.shportfolio.marketdata.insight.application.ports.output.repository.AIAnalysisResultRepositoryPort;
import shop.shportfolio.marketdata.insight.application.scheduler.AiCandleAnalysisScheduler;
import shop.shportfolio.marketdata.insight.application.test.factory.CandleDayTestFactory;
import shop.shportfolio.marketdata.insight.application.test.factory.CandleMinuteTestFactory;
import shop.shportfolio.marketdata.insight.domain.MarketDataInsightDomainService;
import shop.shportfolio.marketdata.insight.domain.MarketDataInsightDomainServiceImpl;
import shop.shportfolio.marketdata.insight.domain.entity.AIAnalysisResult;
import shop.shportfolio.marketdata.insight.domain.valueobject.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class SchedulerTest {

    private OpenAiPort openAiPort;
    private BithumbApiPort bithumbApiPort;
    private AIAnalysisResultRepositoryPort repositoryPort;
    private AIAnalysisHandler aiAnalysisHandler;
    private AiCandleAnalysisScheduler scheduler;
    private MarketDataInsightDomainService marketDataInsightService;
    private AIAnalysisResult aiAnalysisResult;

    @BeforeEach
    public void setUp() {
        // Mock 객체 생성
        openAiPort = org.mockito.Mockito.mock(OpenAiPort.class);
        bithumbApiPort = org.mockito.Mockito.mock(BithumbApiPort.class);
        repositoryPort = org.mockito.Mockito.mock(AIAnalysisResultRepositoryPort.class);
        marketDataInsightService = new MarketDataInsightDomainServiceImpl();
        // 스케줄러 인스턴스 생성
        aiAnalysisHandler = new AIAnalysisHandler(repositoryPort, marketDataInsightService);
        scheduler = new AiCandleAnalysisScheduler(openAiPort, bithumbApiPort, repositoryPort, aiAnalysisHandler);

        aiAnalysisResult = AIAnalysisResult.createAIAnalysisResult(new AIAnalysisResultId(UUID.randomUUID()),
                new MarketId("KRW-BTC"),
                new AnalysisTime(OffsetDateTime.now(ZoneOffset.UTC).minusDays(1)),
                new PeriodEnd(OffsetDateTime.now(ZoneOffset.UTC).minusDays(1)),
                new PeriodStart(OffsetDateTime.now(ZoneOffset.UTC).minusDays(2)),
                new MomentumScore(BigDecimal.ONE),
                PeriodType.ONE_DAY,
                PriceTrend.UP,
                Signal.BUY,
                new SummaryComment("Good"),
                new SummaryComment("좋음"));
    }

    @Test
    @DisplayName("분석 결과가 없는 경우 전체 분석")
    public void noResultAndFullAnalysisTest() {
        // given
        Mockito.when(repositoryPort.findLastAnalysis(Mockito.any(), Mockito.any()))
                .thenReturn(Optional.empty());
        Mockito.when(bithumbApiPort.findCandleDays(Mockito.any()))
                .thenReturn(CandleDayTestFactory.createMockDayCandles());
        Mockito.when(openAiPort.analyzeDays(Mockito.any(), Mockito.any()))
                .thenReturn(AiAnalysisResponseDto.builder()
                        .periodType(PeriodType.ONE_DAY)
                        .marketId("KRW-BTC")
                        .momentumScore(BigDecimal.ONE)
                        .priceTrend(PriceTrend.UP)
                        .signal(Signal.BUY)
                        .periodStart(OffsetDateTime.now())
                        .analysisTime(OffsetDateTime.now())
                        .summaryCommentKor("좋음")
                        .summaryCommentEng("Good")
                        .periodEnd(OffsetDateTime.now())
                        .build());
        // when
        scheduler.analysis(PeriodType.ONE_DAY);
        // then
        Mockito.verify(bithumbApiPort, Mockito.times(1)).findCandleDays(Mockito.any());
        Mockito.verify(repositoryPort, Mockito.times(1)).findLastAnalysis(Mockito.any(), Mockito.any());
        Mockito.verify(openAiPort, Mockito.times(1)).analyzeDays(Mockito.any(), Mockito.any());
        Mockito.verify(repositoryPort, Mockito.times(1)).saveAIAnalysisResult(Mockito.any());
    }

    @Test
    @DisplayName("분석 결과가 있지만, 최신 분석 결과가 있는 경우")
    public void existResultButLatestAndNoAnalysisTest() {
        // given
        aiAnalysisResult = AIAnalysisResult.createAIAnalysisResult(new AIAnalysisResultId(UUID.randomUUID()),
                new MarketId("KRW-BTC"),
                new AnalysisTime(OffsetDateTime.now(ZoneOffset.UTC).minusDays(0)),
                new PeriodEnd(OffsetDateTime.now(ZoneOffset.UTC).minusDays(0)),
                new PeriodStart(OffsetDateTime.now(ZoneOffset.UTC).minusDays(1)),
                new MomentumScore(BigDecimal.ONE),
                PeriodType.ONE_DAY,
                PriceTrend.UP,
                Signal.BUY,
                new SummaryComment("Good"),
                new SummaryComment("좋음"));
        Mockito.when(repositoryPort.findLastAnalysis(Mockito.any(), Mockito.any()))
                .thenReturn(Optional.of(aiAnalysisResult));
        // when
        scheduler.analysis(PeriodType.ONE_DAY);
        // then
        Mockito.verify(bithumbApiPort, Mockito.times(1)).findCandleDays(Mockito.any());
        Mockito.verify(repositoryPort, Mockito.times(1)).findLastAnalysis(
                Mockito.any(), Mockito.any());
        Mockito.verify(openAiPort, Mockito.times(0)).analyzeDays(Mockito.any(), Mockito.any());
        Mockito.verify(openAiPort, Mockito.times(0)).incrementAnalysisDaysWithLatestResult(
                Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.verify(repositoryPort, Mockito.times(0)).saveAIAnalysisResult(Mockito.any());
    }

    @Test
    @DisplayName("분석 결과가 있는 경우 증분 분석")
    public void existResultAndIncrementalAnalysisTest() {
        // given
        Mockito.when(repositoryPort.findLastAnalysis(Mockito.any(), Mockito.any()))
                .thenReturn(Optional.of(aiAnalysisResult));
        Mockito.when(bithumbApiPort.findCandleDays(Mockito.any()))
                .thenReturn(CandleDayTestFactory.createMockDayCandles());
        Mockito.when(openAiPort.incrementAnalysisDaysWithLatestResult(Mockito.any(),
                        Mockito.any(), Mockito.any()))
                .thenReturn(AiAnalysisResponseDto.builder()
                        .periodType(PeriodType.ONE_DAY)
                        .marketId("KRW-BTC")
                        .momentumScore(BigDecimal.ONE)
                        .priceTrend(PriceTrend.UP)
                        .signal(Signal.BUY)
                        .periodStart(OffsetDateTime.now())
                        .analysisTime(OffsetDateTime.now())
                        .summaryCommentKor("좋음")
                        .summaryCommentEng("Good")
                        .periodEnd(OffsetDateTime.now())
                        .build());
        // when
        scheduler.analysis(PeriodType.ONE_DAY);
        // then
        Mockito.verify(bithumbApiPort, Mockito.times(1)).findCandleDays(Mockito.any());
        Mockito.verify(repositoryPort, Mockito.times(1)).findLastAnalysis(
                Mockito.any(), Mockito.any());
        Mockito.verify(openAiPort, Mockito.times(1)).incrementAnalysisDaysWithLatestResult(
                Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.verify(repositoryPort, Mockito.times(1)).saveAIAnalysisResult(Mockito.any());
    }

    @Test
    @DisplayName("분석 결과가 있는 경우 증분 분석 30분봉")
    public void existResult30AndIncrementalAnalysisTest() {
        // given
        aiAnalysisResult = AIAnalysisResult.createAIAnalysisResult(new AIAnalysisResultId(UUID.randomUUID()),
                new MarketId("KRW-BTC"),
                new AnalysisTime(OffsetDateTime.now(ZoneOffset.UTC).minusHours(1)),
                new PeriodEnd(OffsetDateTime.now(ZoneOffset.UTC).minusMinutes(30)),
                new PeriodStart(OffsetDateTime.now(ZoneOffset.UTC).minusHours(6)),
                new MomentumScore(BigDecimal.ONE),
                PeriodType.THIRTY_MINUTES,
                PriceTrend.UP,
                Signal.BUY,
                new SummaryComment("Good"),
                new SummaryComment("좋음"));
        Mockito.when(repositoryPort.findLastAnalysis(Mockito.any(), Mockito.any()))
                .thenReturn(Optional.of(aiAnalysisResult));
        Mockito.when(bithumbApiPort.findCandleMinutes(Mockito.any()))
                .thenReturn(CandleMinuteTestFactory.createMockMinuteCandles());
        Mockito.when(openAiPort.incrementAnalysisThirtyMinutesWithLatestResult(Mockito.any(),
                        Mockito.any(), Mockito.any()))
                .thenReturn(AiAnalysisResponseDto.builder()
                        .periodType(PeriodType.THIRTY_MINUTES)
                        .marketId("KRW-BTC")
                        .momentumScore(BigDecimal.ONE)
                        .priceTrend(PriceTrend.UP)
                        .signal(Signal.BUY)
                        .periodStart(OffsetDateTime.now())
                        .analysisTime(OffsetDateTime.now())
                        .summaryCommentKor("좋음")
                        .summaryCommentEng("Good")
                        .periodEnd(OffsetDateTime.now())
                        .build());
        // when
        scheduler.analysis(PeriodType.THIRTY_MINUTES);
        // then
        Mockito.verify(bithumbApiPort, Mockito.times(1)).findCandleMinutes(Mockito.any());
        Mockito.verify(repositoryPort, Mockito.times(1)).findLastAnalysis(
                Mockito.any(), Mockito.any());
        Mockito.verify(openAiPort, Mockito.times(1)).incrementAnalysisThirtyMinutesWithLatestResult(
                Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.verify(repositoryPort, Mockito.times(1)).saveAIAnalysisResult(Mockito.any());
    }

    @Test
    @DisplayName("분석 결과가 있는 경우 증분 분석2")
    public void existResultAndIncrementalAnalysisTest2() {
        // given
        aiAnalysisResult = AIAnalysisResult.createAIAnalysisResult(new AIAnalysisResultId(UUID.randomUUID()),
                new MarketId("KRW-BTC"),
                new AnalysisTime(OffsetDateTime.now(ZoneOffset.UTC).minusDays(3)),
                new PeriodEnd(OffsetDateTime.now(ZoneOffset.UTC).minusDays(3)),
                new PeriodStart(OffsetDateTime.now(ZoneOffset.UTC).minusDays(4)),
                new MomentumScore(BigDecimal.ONE),
                PeriodType.ONE_DAY,
                PriceTrend.UP,
                Signal.BUY,
                new SummaryComment("Good"),
                new SummaryComment("좋음"));
        Mockito.when(repositoryPort.findLastAnalysis(Mockito.any(), Mockito.any()))
                .thenReturn(Optional.of(aiAnalysisResult));
        Mockito.when(bithumbApiPort.findCandleDays(Mockito.any()))
                .thenReturn(CandleDayTestFactory.createMockDayCandles());
        Mockito.when(openAiPort.incrementAnalysisDaysWithLatestResult(Mockito.any(),
                        Mockito.any(), Mockito.any()))
                .thenReturn(AiAnalysisResponseDto.builder()
                        .periodType(PeriodType.ONE_DAY)
                        .marketId("KRW-BTC")
                        .momentumScore(BigDecimal.ONE)
                        .priceTrend(PriceTrend.UP)
                        .signal(Signal.BUY)
                        .periodStart(OffsetDateTime.now())
                        .analysisTime(OffsetDateTime.now())
                        .summaryCommentKor("좋음")
                        .summaryCommentEng("Good")
                        .periodEnd(OffsetDateTime.now())
                        .build());
        // when
        scheduler.analysis(PeriodType.ONE_DAY);
        // then
        Mockito.verify(bithumbApiPort, Mockito.times(1)).findCandleDays(Mockito.any());
        Mockito.verify(repositoryPort, Mockito.times(1)).findLastAnalysis(
                Mockito.any(), Mockito.any());
        Mockito.verify(openAiPort, Mockito.times(1)).incrementAnalysisDaysWithLatestResult(
                Mockito.any(), Mockito.any(), Mockito.any());
        Mockito.verify(repositoryPort, Mockito.times(1)).saveAIAnalysisResult(Mockito.any());
    }

    @Test
    @DisplayName("분석 결과가 있는 경우 증분 경계")
    public void existResultButExactly10daysAgoTest() {
        aiAnalysisResult = AIAnalysisResult.createAIAnalysisResult(new AIAnalysisResultId(UUID.randomUUID()),
                new MarketId("KRW-BTC"),
                new AnalysisTime(OffsetDateTime.now(ZoneOffset.UTC).minusDays(10)),
                new PeriodEnd(OffsetDateTime.now(ZoneOffset.UTC).minusDays(10)),
                new PeriodStart(OffsetDateTime.now(ZoneOffset.UTC).minusDays(11)),
                new MomentumScore(BigDecimal.ONE),
                PeriodType.ONE_DAY,
                PriceTrend.UP,
                Signal.BUY,
                new SummaryComment("Good"),
                new SummaryComment("좋음"));
        Mockito.when(repositoryPort.findLastAnalysis(Mockito.any(), Mockito.any()))
                .thenReturn(Optional.of(aiAnalysisResult));
        Mockito.when(bithumbApiPort.findCandleDays(Mockito.any()))
                .thenReturn(CandleDayTestFactory.createMockDayCandles());
        Mockito.when(openAiPort.analyzeDays(Mockito.any(),
                        Mockito.any()))
                .thenReturn(AiAnalysisResponseDto.builder()
                        .periodType(PeriodType.ONE_DAY)
                        .marketId("KRW-BTC")
                        .momentumScore(BigDecimal.ONE)
                        .priceTrend(PriceTrend.UP)
                        .signal(Signal.BUY)
                        .periodStart(OffsetDateTime.now())
                        .analysisTime(OffsetDateTime.now())
                        .summaryCommentKor("좋음")
                        .summaryCommentEng("Good")
                        .periodEnd(OffsetDateTime.now())
                        .build());
        // when
        scheduler.analysis(PeriodType.ONE_DAY);
        // then
        Mockito.verify(bithumbApiPort, Mockito.times(2)).findCandleDays(Mockito.any());
        Mockito.verify(repositoryPort, Mockito.times(1)).findLastAnalysis(
                Mockito.any(), Mockito.any());
        Mockito.verify(openAiPort, Mockito.times(1)).analyzeDays(
                Mockito.any(), Mockito.any());
        Mockito.verify(repositoryPort, Mockito.times(1)).saveAIAnalysisResult(Mockito.any());
    }

    @Test
    @DisplayName("분석 결과가 있지만, 너무 오래된 경우 전체  재분석")
    public void existResultButTooOldAndIncrementalAnalysisTest() {
        // given
        aiAnalysisResult = AIAnalysisResult.createAIAnalysisResult(new AIAnalysisResultId(UUID.randomUUID()),
                new MarketId("KRW-BTC"),
                new AnalysisTime(OffsetDateTime.now(ZoneOffset.UTC).minusDays(19)),
                new PeriodEnd(OffsetDateTime.now(ZoneOffset.UTC).minusDays(19)),
                new PeriodStart(OffsetDateTime.now(ZoneOffset.UTC).minusDays(20)),
                new MomentumScore(BigDecimal.ONE),
                PeriodType.ONE_DAY,
                PriceTrend.UP,
                Signal.BUY,
                new SummaryComment("Good"),
                new SummaryComment("좋음"));
        Mockito.when(repositoryPort.findLastAnalysis(Mockito.any(), Mockito.any()))
                .thenReturn(Optional.of(aiAnalysisResult));
        Mockito.when(bithumbApiPort.findCandleDays(Mockito.any()))
                .thenReturn(CandleDayTestFactory.createMockDayCandles());
        Mockito.when(openAiPort.analyzeDays(Mockito.any(),
                        Mockito.any()))
                .thenReturn(AiAnalysisResponseDto.builder()
                        .periodType(PeriodType.ONE_DAY)
                        .marketId("KRW-BTC")
                        .momentumScore(BigDecimal.ONE)
                        .priceTrend(PriceTrend.UP)
                        .signal(Signal.BUY)
                        .periodStart(OffsetDateTime.now())
                        .analysisTime(OffsetDateTime.now())
                        .summaryCommentKor("좋음")
                        .summaryCommentEng("Good")
                        .periodEnd(OffsetDateTime.now())
                        .build());
        // when
        scheduler.analysis(PeriodType.ONE_DAY);
        // then
        Mockito.verify(bithumbApiPort, Mockito.times(2)).findCandleDays(Mockito.any());
        Mockito.verify(repositoryPort, Mockito.times(1)).findLastAnalysis(
                Mockito.any(), Mockito.any());
        Mockito.verify(openAiPort, Mockito.times(1)).analyzeDays(
                Mockito.any(),Mockito.any());
        Mockito.verify(repositoryPort, Mockito.times(1)).saveAIAnalysisResult(Mockito.any());
    }
}
