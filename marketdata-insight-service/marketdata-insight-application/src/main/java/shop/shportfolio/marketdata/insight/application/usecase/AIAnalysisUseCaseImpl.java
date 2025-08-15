package shop.shportfolio.marketdata.insight.application.usecase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.marketdata.insight.application.command.request.AiAnalysisTrackQuery;
import shop.shportfolio.marketdata.insight.application.dto.ai.AiAnalysisResponseDto;
import shop.shportfolio.marketdata.insight.application.dto.candle.*;
import shop.shportfolio.marketdata.insight.application.exception.NotSupportAnalysisTypeException;
import shop.shportfolio.marketdata.insight.application.handler.AIAnalysisHandler;
import shop.shportfolio.marketdata.insight.application.ports.input.AIAnalysisUseCase;
import shop.shportfolio.marketdata.insight.application.ports.output.ai.OpenAiPort;
import shop.shportfolio.marketdata.insight.application.ports.output.bithumb.BithumbApiPort;
import shop.shportfolio.marketdata.insight.domain.entity.AIAnalysisResult;

import java.util.List;
import java.util.Optional;

@Component
public class AIAnalysisUseCaseImpl implements AIAnalysisUseCase {

    private final BithumbApiPort bithumbApiPort;
    private final AIAnalysisHandler aiAnalysisHandler;
    private final OpenAiPort openAiPort;

    @Autowired
    public AIAnalysisUseCaseImpl(BithumbApiPort bithumbApiPort,
                                 AIAnalysisHandler aiAnalysisHandler, OpenAiPort openAiPort) {
        this.bithumbApiPort = bithumbApiPort;
        this.aiAnalysisHandler = aiAnalysisHandler;
        this.openAiPort = openAiPort;
    }

    @Override
    public AIAnalysisResult trackAiAnalysis(AiAnalysisTrackQuery query) {
        return aiAnalysisHandler
                .trackAiAnalysis(query.getMarketId(),query.getPeriodType())
                .orElseGet(() -> createAnalysis(query));
    }

    private AIAnalysisResult createAnalysis(AiAnalysisTrackQuery query) {
        switch (query.getPeriodType()) {
            case THIRTY_MINUTES -> {
                CandleMinuteRequestDto dto = new CandleMinuteRequestDto(30,
                        query.getMarketId(), null, 100);
                List<CandleMinuteResponseDto> candleMinutes = bithumbApiPort.findCandleMinutes(dto);
                AiAnalysisResponseDto aiAnalysisResponseDto = openAiPort.analyzeThirtyMinutes(candleMinutes);
                return aiAnalysisHandler.createAIAnalysisResult(aiAnalysisResponseDto);
            }
            case ONE_HOUR -> {
                CandleMinuteRequestDto dto = new CandleMinuteRequestDto(60,
                        query.getMarketId(), null, 100);
                List<CandleMinuteResponseDto> candleMinutes = bithumbApiPort.findCandleMinutes(dto);
                AiAnalysisResponseDto aiAnalysisResponseDto = openAiPort.analyzeOneHours(candleMinutes);
                return aiAnalysisHandler.createAIAnalysisResult(aiAnalysisResponseDto);
            }
            case ONE_DAY -> {
                CandleRequestDto dto = new CandleRequestDto(query.getMarketId(), null, 100);
                List<CandleDayResponseDto> candleDays = bithumbApiPort.findCandleDays(dto);
                AiAnalysisResponseDto aiAnalysisResponseDto = openAiPort.analyzeDays(candleDays);
                return aiAnalysisHandler.createAIAnalysisResult(aiAnalysisResponseDto);
            }
            case ONE_WEEK -> {
                CandleRequestDto dto = new CandleRequestDto(query.getMarketId(), null, 100);
                List<CandleWeekResponseDto> candleWeeks = bithumbApiPort.findCandleWeeks(dto);
                AiAnalysisResponseDto aiAnalysisResponseDto = openAiPort.analyzeWeeks(candleWeeks);
                return aiAnalysisHandler.createAIAnalysisResult(aiAnalysisResponseDto);
            }
            case ONE_MONTH -> {
                CandleRequestDto dto = new CandleRequestDto(query.getMarketId(), null, 100);
                List<CandleMonthResponseDto> candleMonths = bithumbApiPort.findCandleMonths(dto);
                AiAnalysisResponseDto aiAnalysisResponseDto = openAiPort.analyzeOneMonths(candleMonths);
                return aiAnalysisHandler.createAIAnalysisResult(aiAnalysisResponseDto);
            }
        }
        throw new NotSupportAnalysisTypeException(String.format("Unsupported period type: %s", query.getPeriodType()));
    }
}
