package shop.shportfolio.marketdata.insight.application.scheduler;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
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

    // 서버 시작 후 초기 호출
//    @PostConstruct
    public void initialAnalysis() {
        for (String market : MarketHardCodingData.marketMap.keySet()) {
            for (PeriodType period : PeriodType.values()) {
                // 큐나 비동기 호출로 던지기
                asyncFullAnalysis(market, period);
            }
        }
    }

    @Async
    public void asyncFullAnalysis(String market, PeriodType period) {
        performFullAnalysis(market, period);
    }

    // 30분마다 incremental 분석
    @Scheduled(fixedRate = 30 * 60 * 1000) // 30분
    public void incrementalThirtyMinutesAnalysis() {
        performIncrementalAnalysis(PeriodType.THIRTY_MINUTES);
    }

    // 1시간마다 incremental 분석
    @Scheduled(fixedRate = 60 * 60 * 1000) // 1시간
    public void incrementalOneHourAnalysis() {
        performIncrementalAnalysis(PeriodType.ONE_HOUR);
    }

    private void performIncrementalAnalysis(PeriodType period) {
        for (String market : MarketHardCodingData.marketMap.keySet()) {
            AIAnalysisResult lastResult = repositoryPort
                    .findLastAnalysis(market, period.name())
                    .orElse(null);

            int fetchCount = switch (period) {
                case THIRTY_MINUTES -> MarketCandleCounter.MIN_30;
                case ONE_HOUR -> MarketCandleCounter.HOUR_1;
                case ONE_DAY -> MarketCandleCounter.DAY_1;
                case ONE_WEEK -> MarketCandleCounter.WEEK_1;
                case ONE_MONTH -> MarketCandleCounter.MONTH_1;
            };

            List<?> newCandles;
            if (lastResult != null) {
                newCandles = bithumbApiPort.findCandlesSince(market, period,
                        lastResult.getAnalysisTime().getValue(), fetchCount);
            } else {
                // 마지막 결과 없으면 초기 분석과 동일하게 fetchCount만큼 가져오기
                newCandles = bithumbApiPort.findCandles(market, period, fetchCount);
            }

            if (!newCandles.isEmpty()) {
                AiAnalysisResponseDto aiAnalysisResponseDto = analyzeWithLatestCandles(newCandles, lastResult, period);
                aiAnalysisHandler.createAIAnalysisResult(aiAnalysisResponseDto);
            }
        }
    }

    private AiAnalysisResponseDto analyzeWithLatestCandles(List<?> candles, AIAnalysisResult lastResult, PeriodType period) {
        // 마지막 분석 결과가 있으면 증분 분석
        Object latestCandle = candles.get(candles.size() - 1);
        if (lastResult != null) {
            return analyzeWithLatestCandle(latestCandle, lastResult, period);
        } else {
            // 없으면 전체 분석
            return analyzeFull(candles, period);
        }
    }


    private void performFullAnalysis(String market, PeriodType period) {
        AiAnalysisResponseDto aiAnalysisResponseDto = switch (period) {
            case THIRTY_MINUTES -> {
                List<CandleMinuteResponseDto> min30Candles = bithumbApiPort.findCandleMinutes(
                        new CandleMinuteRequestDto(30, market, null, MarketCandleCounter.MIN_30));
                yield openAiPort.analyzeThirtyMinutes(min30Candles);
            }
            case ONE_HOUR -> {
                List<CandleMinuteResponseDto> oneHourCandles = bithumbApiPort.findCandleMinutes(
                        new CandleMinuteRequestDto(60, market, null, MarketCandleCounter.HOUR_1));
                yield openAiPort.analyzeOneHours(oneHourCandles);
            }
            case ONE_DAY -> {
                List<CandleDayResponseDto> dayCandles = bithumbApiPort.findCandleDays(
                        new CandleRequestDto(market, null, MarketCandleCounter.DAY_1));
                yield openAiPort.analyzeDays(dayCandles);
            }
            case ONE_WEEK -> {
                List<CandleWeekResponseDto> weekCandles = bithumbApiPort.findCandleWeeks(
                        new CandleRequestDto(market, null, MarketCandleCounter.WEEK_1));
                yield openAiPort.analyzeWeeks(weekCandles);
            }
            case ONE_MONTH -> {
                List<CandleMonthResponseDto> monthCandles = bithumbApiPort.findCandleMonths(
                        new CandleRequestDto(market, null, MarketCandleCounter.MONTH_1));
                yield openAiPort.analyzeOneMonths(monthCandles);
            }
            default -> throw new IllegalArgumentException("Unsupported PeriodType: " + period);
        };

        aiAnalysisHandler.createAIAnalysisResult(aiAnalysisResponseDto);
    }



    private AiAnalysisResponseDto analyzeWithLatestCandle(Object candle, AIAnalysisResult lastResult, PeriodType period) {
        switch (period) {
            case THIRTY_MINUTES:
                return openAiPort.analyzeThirtyMinutesWithLatestAnalyze((CandleMinuteResponseDto) candle, lastResult);
            case ONE_HOUR:
                return openAiPort.analyzeOneHoursWithLatestAnalyze((CandleMinuteResponseDto) candle, lastResult);
            case ONE_DAY:
                return openAiPort.analyzeDaysWithLatestAnalyze((CandleDayResponseDto) candle, lastResult);
            case ONE_WEEK:
                return openAiPort.analyzeWeeksWithLatestAnalyze((CandleWeekResponseDto) candle, lastResult);
            case ONE_MONTH:
                return openAiPort.analyzeOneMonthsWithLatestAnalyze((CandleMonthResponseDto) candle, lastResult);
            default:
                throw new IllegalArgumentException("Unsupported PeriodType: " + period);
        }
    }

    private AiAnalysisResponseDto analyzeFull(List<?> candles, PeriodType period) {
        switch (period) {
            case THIRTY_MINUTES:
                return openAiPort.analyzeThirtyMinutes((List<CandleMinuteResponseDto>) candles);
            case ONE_HOUR:
                return openAiPort.analyzeOneHours((List<CandleMinuteResponseDto>) candles);
            case ONE_DAY:
                return openAiPort.analyzeDays((List<CandleDayResponseDto>) candles);
            case ONE_WEEK:
                return openAiPort.analyzeWeeks((List<CandleWeekResponseDto>) candles);
            case ONE_MONTH:
                return openAiPort.analyzeOneMonths((List<CandleMonthResponseDto>) candles);
            default:
                throw new IllegalArgumentException("Unsupported PeriodType: " + period);
        }
    }
}
