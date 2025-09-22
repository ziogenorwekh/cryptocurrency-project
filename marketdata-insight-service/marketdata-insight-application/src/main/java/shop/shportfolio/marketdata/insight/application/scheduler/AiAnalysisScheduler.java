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

//     @PostConstruct
    public void initialAnalysis() {
        for (String market : MarketHardCodingData.marketMap.keySet()) {
            for (PeriodType period : PeriodType.values()) {
                asyncFullAnalysis(market, period);
            }
        }
    }

    @Async
    public void asyncFullAnalysis(String market, PeriodType period) {
        performFullAnalysis(market, period);
    }

    @Scheduled(cron = "0 0/30 * * * *", zone = "UTC")
    public void incrementalThirtyMinutesAnalysis() {
        asyncIncrementalAnalysis(PeriodType.THIRTY_MINUTES);
    }

    @Scheduled(cron = "0 0 * * * *", zone = "UTC")
    public void incrementalOneHourAnalysis() {
        asyncIncrementalAnalysis(PeriodType.ONE_HOUR);
    }

    @Scheduled(cron = "0 0 0 * * *", zone = "UTC")
    public void dailyAnalysis() {
        asyncIncrementalAnalysis(PeriodType.ONE_DAY);
    }

    @Scheduled(cron = "0 0 0 * * SUN", zone = "UTC")
    public void weeklyAnalysis() {
        asyncIncrementalAnalysis(PeriodType.ONE_WEEK);
    }

    @Scheduled(cron = "0 0 0 1 * *", zone = "UTC")
    public void monthlyAnalysis() {
        asyncIncrementalAnalysis(PeriodType.ONE_MONTH);
    }

    @Async
    public void asyncIncrementalAnalysis(PeriodType period) {
        for (String market : MarketHardCodingData.marketMap.keySet()) {
            long start = System.currentTimeMillis();
            long maxWait = 15000; // 최대 15초 대기
            List<?> candles = null;

            while (System.currentTimeMillis() - start < maxWait) {
                candles = bithumbApiPort.findCandlesSince(market, period,
                        getLastAnalysisTime(market, period), getFetchCount(period));
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

            AiAnalysisResponseDto result = analyzeWithLatestCandles(candles, getLastResult(market, period), period);
            if (result != null) {
                aiAnalysisHandler.createAIAnalysisResult(result);
                log.info("[AiAnalysisScheduler] Analysis done for {} [{}]", market, period.name());
            }
        }
    }

    private LocalDateTime getLastAnalysisTime(String market, PeriodType period) {
        return repositoryPort.findLastAnalysis(market, period.name())
                .map(r -> r.getAnalysisTime().getValue().toLocalDateTime())
                .orElse(null);
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
        return repositoryPort.findLastAnalysis(market, period.name()).orElse(null);
    }

    private AiAnalysisResponseDto analyzeWithLatestCandles(List<?> candles, AIAnalysisResult lastResult, PeriodType period) {
        Object latestCandle = candles.get(candles.size() - 1);
        if (lastResult != null) {
            return analyzeWithLatestCandle(latestCandle, lastResult, period);
        } else {
            return analyzeFull(candles, period);
        }
    }

    private void performFullAnalysis(String market, PeriodType period) {
        AiAnalysisResponseDto aiAnalysisResponseDto = switch (period) {
            case THIRTY_MINUTES -> openAiPort.analyzeThirtyMinutes(
                    bithumbApiPort.findCandleMinutes(new CandleMinuteRequestDto(30, market, null, MarketCandleCounter.MIN_30)));
            case ONE_HOUR -> openAiPort.analyzeOneHours(
                    bithumbApiPort.findCandleMinutes(new CandleMinuteRequestDto(60, market, null, MarketCandleCounter.HOUR_1)));
            case ONE_DAY -> openAiPort.analyzeDays(
                    bithumbApiPort.findCandleDays(new CandleRequestDto(market, null, MarketCandleCounter.DAY_1)));
            case ONE_WEEK -> openAiPort.analyzeWeeks(
                    bithumbApiPort.findCandleWeeks(new CandleRequestDto(market, null, MarketCandleCounter.WEEK_1)));
            case ONE_MONTH -> openAiPort.analyzeOneMonths(
                    bithumbApiPort.findCandleMonths(new CandleRequestDto(market, null, MarketCandleCounter.MONTH_1)));
            default -> throw new IllegalArgumentException("Unsupported PeriodType: " + period);
        };

        if (aiAnalysisResponseDto != null) {
            log.info("[AiAnalysisScheduler] Full analysis result for {} [{}]: signal={}, momentum={}",
                    market, period.name(),
                    aiAnalysisResponseDto.getSignal(), aiAnalysisResponseDto.getMomentumScore());
            aiAnalysisHandler.createAIAnalysisResult(aiAnalysisResponseDto);
        }
    }

    private AiAnalysisResponseDto analyzeWithLatestCandle(Object candle, AIAnalysisResult lastResult, PeriodType period) {
        return switch (period) {
            case THIRTY_MINUTES -> openAiPort.analyzeThirtyMinutesWithLatestAnalyze((CandleMinuteResponseDto) candle, lastResult);
            case ONE_HOUR -> openAiPort.analyzeOneHoursWithLatestAnalyze((CandleMinuteResponseDto) candle, lastResult);
            case ONE_DAY -> openAiPort.analyzeDaysWithLatestAnalyze((CandleDayResponseDto) candle, lastResult);
            case ONE_WEEK -> openAiPort.analyzeWeeksWithLatestAnalyze((CandleWeekResponseDto) candle, lastResult);
            case ONE_MONTH -> openAiPort.analyzeOneMonthsWithLatestAnalyze((CandleMonthResponseDto) candle, lastResult);
            default -> throw new IllegalArgumentException("Unsupported PeriodType: " + period);
        };
    }

    private AiAnalysisResponseDto analyzeFull(List<?> candles, PeriodType period) {
        return switch (period) {
            case THIRTY_MINUTES -> openAiPort.analyzeThirtyMinutes((List<CandleMinuteResponseDto>) candles);
            case ONE_HOUR -> openAiPort.analyzeOneHours((List<CandleMinuteResponseDto>) candles);
            case ONE_DAY -> openAiPort.analyzeDays((List<CandleDayResponseDto>) candles);
            case ONE_WEEK -> openAiPort.analyzeWeeks((List<CandleWeekResponseDto>) candles);
            case ONE_MONTH -> openAiPort.analyzeOneMonths((List<CandleMonthResponseDto>) candles);
            default -> throw new IllegalArgumentException("Unsupported PeriodType: " + period);
        };
    }
}
