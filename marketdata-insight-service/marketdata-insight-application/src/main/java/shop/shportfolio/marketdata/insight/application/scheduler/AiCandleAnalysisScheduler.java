package shop.shportfolio.marketdata.insight.application.scheduler;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import shop.shportfolio.marketdata.insight.application.dto.ai.AiAnalysisResponseDto;
import shop.shportfolio.marketdata.insight.application.dto.candle.request.CandleMinuteRequestDto;
import shop.shportfolio.marketdata.insight.application.dto.candle.request.CandleRequestDto;
import shop.shportfolio.marketdata.insight.application.dto.candle.response.CandleDayResponseDto;
import shop.shportfolio.marketdata.insight.application.dto.candle.response.CandleMinuteResponseDto;
import shop.shportfolio.marketdata.insight.application.handler.AIAnalysisHandler;
import shop.shportfolio.marketdata.insight.application.helper.MarketCandleCounter;
import shop.shportfolio.marketdata.insight.application.initializer.MarketHardCodingData;
import shop.shportfolio.marketdata.insight.application.ports.output.ai.OpenAiPort;
import shop.shportfolio.marketdata.insight.application.ports.output.bithumb.BithumbApiPort;
import shop.shportfolio.marketdata.insight.application.ports.output.repository.AIAnalysisResultRepositoryPort;
import shop.shportfolio.marketdata.insight.domain.entity.AIAnalysisResult;
import shop.shportfolio.marketdata.insight.domain.valueobject.PeriodType;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class AiCandleAnalysisScheduler {

    private final OpenAiPort openAiPort;
    private final BithumbApiPort bithumbApiPort;
    private final AIAnalysisResultRepositoryPort repositoryPort;
    private final AIAnalysisHandler aiAnalysisHandler;

    @Autowired
    public AiCandleAnalysisScheduler(OpenAiPort openAiPort, BithumbApiPort bithumbApiPort,
                                     AIAnalysisResultRepositoryPort repositoryPort, AIAnalysisHandler aiAnalysisHandler) {
        this.openAiPort = openAiPort;
        this.bithumbApiPort = bithumbApiPort;
        this.repositoryPort = repositoryPort;
        this.aiAnalysisHandler = aiAnalysisHandler;
    }

//    @PostConstruct
    public void init() {
        thirtyMinutesAnalysis();
    }

    @Scheduled(cron = "0 2/30 * * * *", zone = "UTC")
    public void thirtyMinutesAnalysis() {
        analysis(PeriodType.THIRTY_MINUTES);
    }

    @Scheduled(cron = "0 0 */6 * * *", zone = "UTC")
    public void dailyAnalysis() {
        analysis(PeriodType.ONE_DAY);
    }

    @Transactional
    public void analysis(PeriodType periodType) {
        for (String marketId : MarketHardCodingData.marketMap.keySet()) {
            // 특정 마켓만 테스트
//            if (!marketId.equals("KRW-BTC")) {
//                continue;
//            }
            Optional<AIAnalysisResult> lastAnalysis = repositoryPort.findLastAnalysis(marketId, periodType);
            if (lastAnalysis.isPresent()) {
                log.info("lastAnalysis is found for marketId: {}, periodType: {}", marketId, periodType);
                analyzeIncrementalData(periodType, marketId, lastAnalysis.get());
            } else {
                log.info("No previous analysis found for marketId: {}, periodType: {}. Performing full analysis.", marketId, periodType);
                analyzeWholeData(periodType, marketId);
            }
        }
    }

    private void analyzeIncrementalData(PeriodType periodType, String marketId, AIAnalysisResult analysisResult) {
        AiAnalysisResponseDto aiResponse;

        // 마지막 분석 종료 시간 (UTC)
        OffsetDateTime lastAnalysisEndUtc = analysisResult.getPeriodEnd().getValue();

        // Bithumb API 요청용 KST 변환
        LocalDateTime lastAnalysisEndKst = lastAnalysisEndUtc.plusHours(9).toLocalDateTime();

        switch (periodType) {
            case ONE_DAY -> {
                // KST 기준으로 마지막 캔들 이후 데이터 요청
                List<CandleDayResponseDto> list = bithumbApiPort.findCandleDays(
                        CandleRequestDto.builder()
                                .market(marketId)
                                .to(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                                .count(findFetchCount(periodType))
                                .build()
                );
                log.info("[AI] Found candles size : {} day candles for marketId: {}, periodType: {}", list.size(), marketId, periodType);
//                log.info("-".repeat(20));
                log.info("lastAnalysisEndUtc => {}", lastAnalysisEndUtc);
                List<CandleDayResponseDto> filteringCandles = list.stream().filter(resp -> {
                    OffsetDateTime candleTime = LocalDateTime.parse(resp.getCandleDateTimeUtc() + "Z", DateTimeFormatter.ISO_DATE_TIME)
                            .atOffset(ZoneOffset.UTC);
                    return candleTime.isAfter(lastAnalysisEndUtc);
                }).toList();
                log.info("filteringCandles size => {}", filteringCandles.size());
                if (filteringCandles.isEmpty()) {
                    log.warn("[AI] No new candle data for marketId: {}, periodType: {}", marketId, periodType);
                    return;
                }
                if (filteringCandles.size() == list.size()) {
                    log.info("[AI] No overlap with previous analysis, " +
                            "re-running full analysis with latest candles");
                    analyzeWholeData(periodType, marketId);
                    return;
                }
                aiResponse = openAiPort.incrementAnalysisDaysWithLatestResult(marketId, filteringCandles, analysisResult);
                aiAnalysisHandler.createAIAnalysisResult(aiResponse);
            }

            case THIRTY_MINUTES -> {
                // KST 기준으로 마지막 캔들 이후 데이터 요청
                List<CandleMinuteResponseDto> list = bithumbApiPort.findCandleMinutes(
                        CandleMinuteRequestDto.builder()
                                .market(marketId)
                                .to(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                                .count(findFetchCount(periodType))
                                .unit(30)
                                .build()
                );
                log.info("[AI] Found candles size : {} 30 minutes candles for marketId: {}, periodType: {}",
                        list.size(), marketId, periodType);
//                log.info("-".repeat(20));
                log.info("lastAnalysisEndUtc => {}", lastAnalysisEndUtc);
                List<CandleMinuteResponseDto> filteringCandles = list.stream().filter(resp -> {
                    OffsetDateTime candleTime = LocalDateTime.parse(resp.getCandleDateTimeUtc(), DateTimeFormatter.ISO_DATE_TIME)
                            .atOffset(ZoneOffset.UTC);
//                    log.info("candleTime => {}", candleTime);
                    return candleTime.isAfter(lastAnalysisEndUtc);
                }).toList();
//                log.info("-".repeat(20));
                log.info("filteringCandles size => {}", filteringCandles.size());
                if (filteringCandles.isEmpty()) {
                    log.warn("[AI] No new candle data for marketId: {}, periodType: {}", marketId, periodType);
                    return;
                }
                if (filteringCandles.size() == list.size()) {
                    log.info("[AI] Too Old Data, all candles are after last analysis " +
                            "end time for marketId: {}, periodType: {}", marketId, periodType);
                    analyzeWholeData(periodType, marketId);
                    return;
                }

                aiResponse = openAiPort.incrementAnalysisThirtyMinutesWithLatestResult(marketId, filteringCandles, analysisResult);
                aiAnalysisHandler.createAIAnalysisResult(aiResponse);
            }

            default -> {
            }
        }
    }


    private void analyzeWholeData(PeriodType periodType, String marketId) {
        AiAnalysisResponseDto aiResponse;
        switch (periodType) {
            case ONE_DAY -> {
                List<CandleDayResponseDto> list = bithumbApiPort.findCandleDays(
                        CandleRequestDto.builder()
                                .market(marketId)
                                .count(findFetchCount(periodType))
                                .build());
                if (list == null || list.isEmpty()) {
                    log.warn("[AI] No candle data for marketId: {}, periodType: {}", marketId, periodType);
                    return;
                }
                aiResponse = openAiPort.analyzeDays(marketId, list);
                aiAnalysisHandler.createAIAnalysisResult(aiResponse);
            }
            case THIRTY_MINUTES -> {
                List<CandleMinuteResponseDto> list = bithumbApiPort.findCandleMinutes(
                        CandleMinuteRequestDto.builder()
                                .market(marketId)
                                .count(findFetchCount(periodType))
                                .unit(30)
                                .build()
                );
                if (list == null || list.isEmpty()) {
                    log.warn("[AI] No candle data for marketId: {}, periodType: {}", marketId, periodType);
                    return;
                }
                aiResponse = openAiPort.analyzeThirtyMinutes(marketId, list);
                aiAnalysisHandler.createAIAnalysisResult(aiResponse);
            }
            default -> {
            }
        }
    }

    private Integer findFetchCount(PeriodType periodType) {
        return switch (periodType) {
            case ONE_DAY -> MarketCandleCounter.DAY_1;
            case THIRTY_MINUTES -> MarketCandleCounter.MIN_30;
            default -> 0;
        };
    }
}
