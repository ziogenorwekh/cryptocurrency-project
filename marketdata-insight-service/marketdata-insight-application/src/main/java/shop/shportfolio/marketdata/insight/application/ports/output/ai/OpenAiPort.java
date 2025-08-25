package shop.shportfolio.marketdata.insight.application.ports.output.ai;

import shop.shportfolio.marketdata.insight.application.dto.ai.AiAnalysisResponseDto;
import shop.shportfolio.marketdata.insight.application.dto.candle.response.CandleDayResponseDto;
import shop.shportfolio.marketdata.insight.application.dto.candle.response.CandleMinuteResponseDto;
import shop.shportfolio.marketdata.insight.application.dto.candle.response.CandleMonthResponseDto;
import shop.shportfolio.marketdata.insight.application.dto.candle.response.CandleWeekResponseDto;

import java.util.List;

public interface OpenAiPort {

    AiAnalysisResponseDto analyzeThirtyMinutes(List<CandleMinuteResponseDto> dtoList);

    AiAnalysisResponseDto analyzeOneHours(List<CandleMinuteResponseDto> dtoList);

    AiAnalysisResponseDto analyzeDays(List<CandleDayResponseDto> dtoList);

    AiAnalysisResponseDto analyzeWeeks(List<CandleWeekResponseDto> dtoList);

    AiAnalysisResponseDto analyzeOneMonths(List<CandleMonthResponseDto> dtoList);
}
