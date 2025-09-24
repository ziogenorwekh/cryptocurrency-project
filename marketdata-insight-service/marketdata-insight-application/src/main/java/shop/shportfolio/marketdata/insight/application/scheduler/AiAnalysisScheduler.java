//package shop.shportfolio.marketdata.insight.application.scheduler;
//
//import jakarta.annotation.PostConstruct;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//import org.springframework.beans.factory.annotation.Autowired;
//import lombok.extern.slf4j.Slf4j;
//import shop.shportfolio.marketdata.insight.application.dto.ai.AiAnalysisResponseDto;
//import shop.shportfolio.marketdata.insight.application.dto.candle.request.CandleMinuteRequestDto;
//import shop.shportfolio.marketdata.insight.application.dto.candle.request.CandleRequestDto;
//import shop.shportfolio.marketdata.insight.application.handler.AIAnalysisHandler;
//import shop.shportfolio.marketdata.insight.application.helper.MarketCandleCounter;
//import shop.shportfolio.marketdata.insight.application.ports.output.ai.OpenAiPort;
//import shop.shportfolio.marketdata.insight.application.ports.output.bithumb.BithumbApiPort;
//import shop.shportfolio.marketdata.insight.application.ports.output.repository.AIAnalysisResultRepositoryPort;
//import shop.shportfolio.marketdata.insight.application.initializer.MarketHardCodingData;
//import shop.shportfolio.marketdata.insight.domain.entity.AIAnalysisResult;
//import shop.shportfolio.marketdata.insight.domain.valueobject.PeriodType;
//import shop.shportfolio.marketdata.insight.application.dto.candle.response.*;
//
//import java.time.LocalDateTime;
//import java.time.OffsetDateTime;
//import java.time.ZoneOffset;
//import java.util.List;
//
//@Slf4j
////@Component
//public class AiAnalysisScheduler {
//
//    private final OpenAiPort openAiPort;
//    private final BithumbApiPort bithumbApiPort;
//    private final AIAnalysisResultRepositoryPort repositoryPort;
//    private final AIAnalysisHandler aiAnalysisHandler;
//
//    @Autowired
//    public AiAnalysisScheduler(OpenAiPort openAiPort,
//                               BithumbApiPort bithumbApiPort,
//                               AIAnalysisResultRepositoryPort repositoryPort,
//                               AIAnalysisHandler aiAnalysisHandler) {
//        this.openAiPort = openAiPort;
//        this.bithumbApiPort = bithumbApiPort;
//        this.repositoryPort = repositoryPort;
//        this.aiAnalysisHandler = aiAnalysisHandler;
//    }
//
//    @PostConstruct
//    public void initialAnalysis() {
//        asyncIncrementalAnalysis(PeriodType.THIRTY_MINUTES); // 원하는 기간
//    }
//
//    @Scheduled(cron = "0 2/30 * * * *", zone = "UTC")
//    // 매 30분마다, 시작은 xx:02 UTC
//    public void incrementalThirtyMinutesAnalysis() {
//        asyncIncrementalAnalysis(PeriodType.THIRTY_MINUTES);
//    }
//
//
//    // 1시간 단위 → 매 정각 2분에 실행
//    @Scheduled(cron = "0 2 * * * *", zone = "UTC")
//    public void incrementalOneHourAnalysis() {
//        asyncIncrementalAnalysis(PeriodType.ONE_HOUR);
//    }
//
//    // 일 단위 → 매일 00:02 UTC
//    @Scheduled(cron = "0 2 0 * * *", zone = "UTC")
//    public void dailyAnalysis() {
//        asyncIncrementalAnalysis(PeriodType.ONE_DAY);
//    }
//
//    // 주 단위 → 매주 일요일 00:02 UTC
//    @Scheduled(cron = "0 2 0 * * SUN", zone = "UTC")
//    public void weeklyAnalysis() {
//        asyncIncrementalAnalysis(PeriodType.ONE_WEEK);
//    }
//
//    // 월 단위 → 매월 1일 00:02 UTC
//    @Scheduled(cron = "0 2 0 1 * *", zone = "UTC")
//    public void monthlyAnalysis() {
//        asyncIncrementalAnalysis(PeriodType.ONE_MONTH);
//    }
//
//    //    @Async
//    public void asyncIncrementalAnalysis(PeriodType period) {
//        for (String market : MarketHardCodingData.marketMap.keySet()) {
//            if (!market.equals("KRW-BTC")) {
//                log.info("[AiAnalysisScheduler] Skipping incremental analysis: {}", market);
//                continue;
//            }
//
//            log.info("[AiAnalysisScheduler] Starting incremental analysis for {}", market);
//
//            // 마지막 분석 결과 가져오기
//            AIAnalysisResult lastResult = getLastResult(market, period);
//            OffsetDateTime lastAnalysisTime = (lastResult != null)
//                    ? lastResult.getAnalysisTime().getValue()
//                    : null;
//
//            // 캔들 가져오기
//            List<?> candles = (lastAnalysisTime != null)
//                    ? bithumbApiPort.findCandlesSince(market, period, lastAnalysisTime.toLocalDateTime(), getFetchCount(period))
//                    : bithumbApiPort.findCandles(market, period, getFetchCount(period));
//
//            if (candles == null || candles.isEmpty()) {
//                log.warn("[AiAnalysisScheduler] No new candles for {} [{}]", market, period.name());
//                continue;
//            }
//
//            log.info("[AiAnalysisScheduler] Found {} candles for {} [{}]", candles.size(), market, period.name());
//
//            // 최신 캔들
//            Object latestCandle = candles.get(0);
//            OffsetDateTime latestCandleTime = parseCandleTime(latestCandle);
//            if (latestCandleTime == null) continue;
//
//            log.info("[AiAnalysisScheduler] Latest candle for {} [{}]: {}", market, period.name(), latestCandleTime);
//
//            // 이미 분석됐으면 스킵
//            if (lastAnalysisTime != null && !latestCandleTime.isAfter(lastAnalysisTime)) {
//                log.info("[AiAnalysisScheduler] Latest candle already analyzed for {} [{}], skipping OpenAI call", market, period.name());
//                continue;
//            }
//
//            // 분석
//            AiAnalysisResponseDto result;
//            if (lastResult == null) {
//                result = analyzeFull(market, candles, period);
//            } else {
//                result = analyzeWithLatestCandle(market, latestCandle, lastResult, period);
//            }
//
//            if (result != null) {
//                aiAnalysisHandler.createAIAnalysisResult(result);
//                log.info("[AiAnalysisScheduler] Analysis done for {} [{}]", market, period.name());
//            }
//        }
//    }
//
//    // candle 타입별 시간 파싱 helper
//    private OffsetDateTime parseCandleTime(Object candle) {
//        if (candle instanceof CandleMinuteResponseDto cm)
//            return LocalDateTime.parse(cm.getCandleDateTimeKst()).atOffset(ZoneOffset.UTC);
//        if (candle instanceof CandleDayResponseDto cd)
//            return LocalDateTime.parse(cd.getCandleDateTimeKst()).atOffset(ZoneOffset.UTC);
//        if (candle instanceof CandleWeekResponseDto cw)
//            return LocalDateTime.parse(cw.getCandleDateTimeKst()).atOffset(ZoneOffset.UTC);
//        if (candle instanceof CandleMonthResponseDto cm)
//            return LocalDateTime.parse(cm.getCandleDateTimeKst()).atOffset(ZoneOffset.UTC);
//
//        log.warn("[AiAnalysisScheduler] Unknown candle type: {}", candle.getClass().getSimpleName());
//        return null;
//    }
//
//    private int getFetchCount(PeriodType period) {
//        return switch (period) {
//            case THIRTY_MINUTES -> MarketCandleCounter.MIN_30;
//            case ONE_HOUR -> MarketCandleCounter.HOUR_1;
//            case ONE_DAY -> MarketCandleCounter.DAY_1;
//            case ONE_WEEK -> MarketCandleCounter.WEEK_1;
//            case ONE_MONTH -> MarketCandleCounter.MONTH_1;
//        };
//    }
//
//    private AIAnalysisResult getLastResult(String market, PeriodType period) {
//        return repositoryPort.findLastAnalysis(market, period).orElse(null);
//    }
//
//    private AiAnalysisResponseDto analyzeWithLatestCandle(String marketId, Object candle, AIAnalysisResult lastResult, PeriodType period) {
//        return switch (period) {
//            case THIRTY_MINUTES ->
//                    openAiPort.analyzeThirtyMinutesWithLatestAnalyze(marketId, (CandleMinuteResponseDto) candle, lastResult);
//            case ONE_HOUR ->
//                    openAiPort.analyzeOneHoursWithLatestAnalyze(marketId, (CandleMinuteResponseDto) candle, lastResult);
//            case ONE_DAY ->
//                    openAiPort.analyzeDaysWithLatestAnalyze(marketId, (CandleDayResponseDto) candle, lastResult);
//            case ONE_WEEK ->
//                    openAiPort.analyzeWeeksWithLatestAnalyze(marketId, (CandleWeekResponseDto) candle, lastResult);
//            case ONE_MONTH ->
//                    openAiPort.analyzeOneMonthsWithLatestAnalyze(marketId, (CandleMonthResponseDto) candle, lastResult);
//            default -> throw new IllegalArgumentException("Unsupported PeriodType: " + period);
//        };
//    }
//
//    private AiAnalysisResponseDto analyzeFull(String marketId, List<?> candles, PeriodType period) {
//        return switch (period) {
//            case THIRTY_MINUTES -> openAiPort.analyzeThirtyMinutes(marketId, (List<CandleMinuteResponseDto>) candles);
//            case ONE_HOUR -> openAiPort.analyzeOneHours(marketId, (List<CandleMinuteResponseDto>) candles);
//            case ONE_DAY -> openAiPort.analyzeDays(marketId, (List<CandleDayResponseDto>) candles);
//            case ONE_WEEK -> openAiPort.analyzeWeeks(marketId, (List<CandleWeekResponseDto>) candles);
//            case ONE_MONTH -> openAiPort.analyzeOneMonths(marketId, (List<CandleMonthResponseDto>) candles);
//            default -> throw new IllegalArgumentException("Unsupported PeriodType: " + period);
//        };
//    }
//}
