package shop.shportfolio.marketdata.insight.application.scheduler;

import jakarta.annotation.PostConstruct;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;
import shop.shportfolio.marketdata.insight.application.dto.ai.AiAnalysisResponseDto;
import shop.shportfolio.marketdata.insight.application.dto.candle.request.CandleMinuteRequestDto;
import shop.shportfolio.marketdata.insight.application.dto.candle.request.CandleRequestDto;
import shop.shportfolio.marketdata.insight.application.handler.AIAnalysisHandler;
import shop.shportfolio.marketdata.insight.application.helper.MarketCandleCounter;
import shop.shportfolio.marketdata.insight.application.ports.output.ai.OpenAiPort;
import shop.shportfolio.marketdata.insight.application.ports.output.bithumb.BithumbApiPort;
import shop.shportfolio.marketdata.insight.application.ports.output.repository.AIAnalysisResultRepositoryPort;
import shop.shportfolio.marketdata.insight.application.initializer.MarketHardCodingData;
import shop.shportfolio.marketdata.insight.domain.entity.AIAnalysisResult;
import shop.shportfolio.marketdata.insight.domain.valueobject.PeriodType;
import shop.shportfolio.marketdata.insight.application.dto.candle.response.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Slf4j
@Component
public class AiAnalysisScheduler {

    private final OpenAiPort openAiPort;
    private final BithumbApiPort bithumbApiPort;
    private final AIAnalysisResultRepositoryPort repositoryPort;
    private final AIAnalysisHandler aiAnalysisHandler;

    @Autowired
    public AiAnalysisScheduler(OpenAiPort openAiPort,
                               BithumbApiPort bithumbApiPort,
                               AIAnalysisResultRepositoryPort repositoryPort,
                               AIAnalysisHandler aiAnalysisHandler) {
        this.openAiPort = openAiPort;
        this.bithumbApiPort = bithumbApiPort;
        this.repositoryPort = repositoryPort;
        this.aiAnalysisHandler = aiAnalysisHandler;
    }

    @PostConstruct
    public void initialAnalysis() {
        asyncIncrementalAnalysis(PeriodType.THIRTY_MINUTES); // 원하는 기간
    }

    @Scheduled(cron = "0 2/30 * * * *", zone = "UTC")
    // 매 30분마다, 시작은 xx:02 UTC
    public void incrementalThirtyMinutesAnalysis() {
        asyncIncrementalAnalysis(PeriodType.THIRTY_MINUTES);
    }


    // 1시간 단위 → 매 정각 2분에 실행
    @Scheduled(cron = "0 2 * * * *", zone = "UTC")
    public void incrementalOneHourAnalysis() {
        asyncIncrementalAnalysis(PeriodType.ONE_HOUR);
    }

    // 일 단위 → 매일 00:02 UTC
    @Scheduled(cron = "0 2 0 * * *", zone = "UTC")
    public void dailyAnalysis() {
        asyncIncrementalAnalysis(PeriodType.ONE_DAY);
    }

    // 주 단위 → 매주 일요일 00:02 UTC
    @Scheduled(cron = "0 2 0 * * SUN", zone = "UTC")
    public void weeklyAnalysis() {
        asyncIncrementalAnalysis(PeriodType.ONE_WEEK);
    }

    // 월 단위 → 매월 1일 00:02 UTC
    @Scheduled(cron = "0 2 0 1 * *", zone = "UTC")
    public void monthlyAnalysis() {
        asyncIncrementalAnalysis(PeriodType.ONE_MONTH);
    }

    @Async
    public void asyncIncrementalAnalysis(PeriodType period) {
        for (String market : MarketHardCodingData.marketMap.keySet()) {
            if (!market.equals("KRW-BTC") && !market.equals("KRW-ETH") && !market.equals("KRW-XRP")) {
                log.info("[AiAnalysisScheduler] Skipping incremental analysis: {}", market);
                continue;
            }
            long start = System.currentTimeMillis();
            long maxWait = 15000; // 최대 15초 대기
            List<?> candles = null;
            log.info("[AiAnalysisScheduler] Starting incremental analysis");

            // 마지막 분석 결과 가져오기
            AIAnalysisResult lastResult = getLastResult(market, period);
            OffsetDateTime lastAnalysisTime = (lastResult != null)
                    ? lastResult.getAnalysisTime().getValue()
                    : null;
            while (System.currentTimeMillis() - start < maxWait) {
                OffsetDateTime since = lastAnalysisTime;

                candles = (since != null)
                        ? bithumbApiPort.findCandlesSince(market, period, since.toLocalDateTime(), getFetchCount(period))
                        : bithumbApiPort.findCandles(market, period, getFetchCount(period));

                if (candles != null && !candles.isEmpty()) break;

                try {
                    Thread.sleep(1000); // 1초 간격 폴링
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.error("Interrupted while waiting for candles: {}", e.getMessage());
                    return;
                }
            }

            if (candles == null || candles.isEmpty()) {
                log.warn("[AiAnalysisScheduler] No new candles for {} [{}] after waiting {} ms", market, period.name(), maxWait);
                continue;
            }

            // 최신 캔들 로그 및 시간 파싱
            Object latestCandle = candles.get(candles.size() - 1);
            OffsetDateTime latestCandleTime;

            if (latestCandle instanceof CandleMinuteResponseDto cm) {
                latestCandleTime = LocalDateTime.parse(cm.getCandleDateTimeUtc())
                        .atOffset(ZoneOffset.UTC);
            } else if (latestCandle instanceof CandleDayResponseDto cd) {
                latestCandleTime = LocalDateTime.parse(cd.getCandleDateTimeUtc())
                        .atOffset(ZoneOffset.UTC);
            } else if (latestCandle instanceof CandleWeekResponseDto cw) {
                latestCandleTime = LocalDateTime.parse(cw.getCandleDateTimeUtc())
                        .atOffset(ZoneOffset.UTC);
            } else if (latestCandle instanceof CandleMonthResponseDto cm) {
                latestCandleTime = LocalDateTime.parse(cm.getCandleDateTimeUtc())
                        .atOffset(ZoneOffset.UTC);
            } else {
                log.warn("[AiAnalysisScheduler] Unknown candle type for market {} [{}]",
                        market, period.name());
                continue;
            }

            log.info("[AiAnalysisScheduler] Latest candle for {} [{}]: {}", market, period.name(), latestCandleTime);

            // 이미 분석된 데이터면 OpenAI 호출하지 않고 건너뜀
            if (lastAnalysisTime != null && !latestCandleTime.isAfter(lastAnalysisTime)) {
                log.info("[AiAnalysisScheduler] Latest candle already analyzed for {} [{}], skipping OpenAI call", market, period.name());
                continue;
            }

            AiAnalysisResponseDto result;
            if (lastResult == null) {
                // 저장된 결과가 없으면 fetchCount 만큼 가져온 candles로 Full 분석
                result = analyzeFull(market, candles, period);
            } else {
                // lastResult가 있으면 최신 캔들 기준 incremental 분석
                log.info("[AiAnalysisScheduler] latest candle is {}", latestCandle.toString());
                result = analyzeWithLatestCandle(market, latestCandle, lastResult, period);
            }

            if (result != null) {
                aiAnalysisHandler.createAIAnalysisResult(result);
                log.info("[AiAnalysisScheduler] Analysis done for {} [{}]", market, period.name());
            }
        }
    }

    private int getFetchCount(PeriodType period) {
        return switch (period) {
            case THIRTY_MINUTES -> MarketCandleCounter.MIN_30;
            case ONE_HOUR -> MarketCandleCounter.HOUR_1;
            case ONE_DAY -> MarketCandleCounter.DAY_1;
            case ONE_WEEK -> MarketCandleCounter.WEEK_1;
            case ONE_MONTH -> MarketCandleCounter.MONTH_1;
        };
    }

    private AIAnalysisResult getLastResult(String market, PeriodType period) {
        return repositoryPort.findLastAnalysis(market, period).orElse(null);
    }

    private AiAnalysisResponseDto analyzeWithLatestCandle(String marketId, Object candle, AIAnalysisResult lastResult, PeriodType period) {
        return switch (period) {
            case THIRTY_MINUTES ->
                    openAiPort.analyzeThirtyMinutesWithLatestAnalyze(marketId, (CandleMinuteResponseDto) candle, lastResult);
            case ONE_HOUR ->
                    openAiPort.analyzeOneHoursWithLatestAnalyze(marketId, (CandleMinuteResponseDto) candle, lastResult);
            case ONE_DAY ->
                    openAiPort.analyzeDaysWithLatestAnalyze(marketId, (CandleDayResponseDto) candle, lastResult);
            case ONE_WEEK ->
                    openAiPort.analyzeWeeksWithLatestAnalyze(marketId, (CandleWeekResponseDto) candle, lastResult);
            case ONE_MONTH ->
                    openAiPort.analyzeOneMonthsWithLatestAnalyze(marketId, (CandleMonthResponseDto) candle, lastResult);
            default -> throw new IllegalArgumentException("Unsupported PeriodType: " + period);
        };
    }

    private AiAnalysisResponseDto analyzeFull(String marketId, List<?> candles, PeriodType period) {
        return switch (period) {
            case THIRTY_MINUTES -> openAiPort.analyzeThirtyMinutes(marketId, (List<CandleMinuteResponseDto>) candles);
            case ONE_HOUR -> openAiPort.analyzeOneHours(marketId, (List<CandleMinuteResponseDto>) candles);
            case ONE_DAY -> openAiPort.analyzeDays(marketId, (List<CandleDayResponseDto>) candles);
            case ONE_WEEK -> openAiPort.analyzeWeeks(marketId, (List<CandleWeekResponseDto>) candles);
            case ONE_MONTH -> openAiPort.analyzeOneMonths(marketId, (List<CandleMonthResponseDto>) candles);
            default -> throw new IllegalArgumentException("Unsupported PeriodType: " + period);
        };
    }
}
