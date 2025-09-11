package shop.shportfolio.marketdata.insight.application.ports.output.ai;

import shop.shportfolio.marketdata.insight.application.dto.ai.AiAnalysisResponseDto;
import shop.shportfolio.marketdata.insight.application.dto.candle.response.CandleDayResponseDto;
import shop.shportfolio.marketdata.insight.application.dto.candle.response.CandleMinuteResponseDto;
import shop.shportfolio.marketdata.insight.application.dto.candle.response.CandleMonthResponseDto;
import shop.shportfolio.marketdata.insight.application.dto.candle.response.CandleWeekResponseDto;
import shop.shportfolio.marketdata.insight.domain.entity.AIAnalysisResult;

import java.util.List;

public interface OpenAiPort {

    AiAnalysisResponseDto analyzeThirtyMinutes(List<CandleMinuteResponseDto> dtoList);
    AiAnalysisResponseDto analyzeThirtyMinutesWithLatestAnalyze(CandleMinuteResponseDto dto, AIAnalysisResult result);

    AiAnalysisResponseDto analyzeOneHours(List<CandleMinuteResponseDto> dtoList);
    AiAnalysisResponseDto analyzeOneHoursWithLatestAnalyze(CandleMinuteResponseDto dto, AIAnalysisResult result);

    AiAnalysisResponseDto analyzeDays(List<CandleDayResponseDto> dtoList);
    AiAnalysisResponseDto analyzeDaysWithLatestAnalyze(CandleDayResponseDto dto, AIAnalysisResult result);


    AiAnalysisResponseDto analyzeWeeks(List<CandleWeekResponseDto> dtoList);
    AiAnalysisResponseDto analyzeWeeksWithLatestAnalyze(CandleWeekResponseDto dto, AIAnalysisResult result);

    AiAnalysisResponseDto analyzeOneMonths(List<CandleMonthResponseDto> dtoList);
    AiAnalysisResponseDto analyzeOneMonthsWithLatestAnalyze(CandleMonthResponseDto dto, AIAnalysisResult result);
}
