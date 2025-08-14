package shop.shportfolio.marketdata.insight.application.ports.output.ai;

import jakarta.validation.Valid;
import shop.shportfolio.marketdata.insight.application.command.request.AiAnalysisTrackQuery;
import shop.shportfolio.marketdata.insight.application.dto.ai.AiAnalysisResponseDto;
import shop.shportfolio.marketdata.insight.application.dto.candle.CandleDayResponseDto;
import shop.shportfolio.marketdata.insight.application.dto.candle.CandleMinuteResponseDto;
import shop.shportfolio.marketdata.insight.application.dto.candle.CandleMonthResponseDto;
import shop.shportfolio.marketdata.insight.application.dto.candle.CandleWeekResponseDto;
import shop.shportfolio.marketdata.insight.domain.entity.AIAnalysisResult;

import java.util.List;

public interface OpenAiPort {

    AiAnalysisResponseDto analyzeThirtyMinutes(List<CandleMinuteResponseDto> dtoList);

    AiAnalysisResponseDto analyzeOneHours(List<CandleMinuteResponseDto> dtoList);

    AiAnalysisResponseDto analyzeDays(List<CandleDayResponseDto> dtoList);

    AiAnalysisResponseDto analyzeWeeks(List<CandleWeekResponseDto> dtoList);

    AiAnalysisResponseDto analyzeOneMonths(List<CandleMonthResponseDto> dtoList);
}
