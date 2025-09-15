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
                               AIAnalysisResultRepositoryPort repositoryPort, AIAnalysisHandler aiAnalysisHandler) {
        this.openAiPort = openAiPort;
        this.bithumbApiPort = bithumbApiPort;
        this.repositoryPort = repositoryPort;
        this.aiAnalysisHandler = aiAnalysisHandler;
    }

//    @PostConstruct
    public void initialAnalysis() {
//        for (String market : MarketHardCodingData.marketMap.keySet()) {
            for (PeriodType period : PeriodType.values()) {
                asyncFullAnalysis("KRW-SAND", period);
            }
//        }
    }

    @Async
    public void asyncFullAnalysis(String market, PeriodType period) {
        performFullAnalysis(market, period);
    }

    @Scheduled(cron = "0 0/30 * * * *", zone = "UTC")
    public void incrementalThirtyMinutesAnalysis() {
        performIncrementalAnalysis(PeriodType.THIRTY_MINUTES);
    }

    @Scheduled(cron = "0 0 * * * *", zone = "UTC")
    public void incrementalOneHourAnalysis() {
        performIncrementalAnalysis(PeriodType.ONE_HOUR);
    }

    @Scheduled(cron = "0 0 0 * * *", zone = "UTC")
    public void dailyAnalysis() {
        performIncrementalAnalysis(PeriodType.ONE_DAY);
    }

    @Scheduled(cron = "0 0 0 * * SUN", zone = "UTC")
    public void weeklyAnalysis() {
        performIncrementalAnalysis(PeriodType.ONE_WEEK);
    }

    @Scheduled(cron = "0 0 0 1 * *", zone = "UTC")
    public void monthlyAnalysis() {
        performIncrementalAnalysis(PeriodType.ONE_MONTH);
    }

    private void performIncrementalAnalysis(PeriodType period) {
        for (String market : MarketHardCodingData.marketMap.keySet()) {
            AIAnalysisResult lastResult = repositoryPort.findLastAnalysis(market, period.name()).orElse(null);

            int fetchCount = switch (period) {
                case THIRTY_MINUTES -> MarketCandleCounter.MIN_30;
                case ONE_HOUR -> MarketCandleCounter.HOUR_1;
                case ONE_DAY -> MarketCandleCounter.DAY_1;
                case ONE_WEEK -> MarketCandleCounter.WEEK_1;
                case ONE_MONTH -> MarketCandleCounter.MONTH_1;
            };

            List<?> newCandles;
            if (lastResult != null) {
                log.info("[AiAnalysisScheduler] Last analysis exists for {} [{}] at {}", market, period.name(), lastResult.getAnalysisTime());
                newCandles = bithumbApiPort.findCandlesSince(market, period,
                        lastResult.getAnalysisTime().getValue().toLocalDateTime(), fetchCount);
            } else {
                log.info("[AiAnalysisScheduler] No previous analysis for {} [{}], performing full fetch", market, period.name());
                newCandles = bithumbApiPort.findCandles(market, period, fetchCount);
            }

            if (!newCandles.isEmpty()) {
                AiAnalysisResponseDto aiAnalysisResponseDto = analyzeWithLatestCandles(newCandles, lastResult, period);
                if (aiAnalysisResponseDto != null) {
                    log.info("[AiAnalysisScheduler] AI Analysis result ready for {} [{}]: signal={}, momentum={}",
                            market, period.name(),
                            aiAnalysisResponseDto.getSignal(), aiAnalysisResponseDto.getMomentumScore());
                    aiAnalysisHandler.createAIAnalysisResult(aiAnalysisResponseDto);
                }
            } else {
                log.info("[AiAnalysisScheduler] No new candles to analyze for {} [{}]", market, period.name());
            }
        }
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
