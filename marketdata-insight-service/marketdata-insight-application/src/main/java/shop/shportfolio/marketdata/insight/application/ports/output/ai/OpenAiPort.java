package shop.shportfolio.marketdata.insight.application.ports.output.ai;

import shop.shportfolio.marketdata.insight.application.dto.ai.AiAnalysisResponseDto;
import shop.shportfolio.marketdata.insight.application.dto.candle.response.CandleDayResponseDto;
import shop.shportfolio.marketdata.insight.application.dto.candle.response.CandleMinuteResponseDto;
import shop.shportfolio.marketdata.insight.application.dto.candle.response.CandleMonthResponseDto;
import shop.shportfolio.marketdata.insight.application.dto.candle.response.CandleWeekResponseDto;
import shop.shportfolio.marketdata.insight.domain.entity.AIAnalysisResult;

import java.util.List;

public interface OpenAiPort {

    AiAnalysisResponseDto analyzeThirtyMinutes(String marketId, List<CandleMinuteResponseDto> dtoList);

//    AiAnalysisResponseDto analyzeThirtyMinutesWithLatestAnalyze(String marketId, CandleMinuteResponseDto dto, AIAnalysisResult result);

    AiAnalysisResponseDto incrementAnalysisThirtyMinutesWithLatestResult(String marketId,
                                                                         List<CandleMinuteResponseDto> dtos,
                                                                         AIAnalysisResult result);

    AiAnalysisResponseDto analyzeOneHours(String marketId, List<CandleMinuteResponseDto> dtoList);

//    AiAnalysisResponseDto analyzeOneHoursWithLatestAnalyze(String marketId, CandleMinuteResponseDto dto, AIAnalysisResult result);

    AiAnalysisResponseDto analyzeDays(String marketId, List<CandleDayResponseDto> dtoList);

//    AiAnalysisResponseDto analyzeDaysWithLatestAnalyze(String marketId, CandleDayResponseDto dto, AIAnalysisResult result);

    AiAnalysisResponseDto incrementAnalysisDaysWithLatestResult(String marketId, List<CandleDayResponseDto> dtos, AIAnalysisResult result);

    AiAnalysisResponseDto analyzeWeeks(String marketId, List<CandleWeekResponseDto> dtoList);

//    AiAnalysisResponseDto analyzeWeeksWithLatestAnalyze(String marketId, CandleWeekResponseDto dto, AIAnalysisResult result);

    AiAnalysisResponseDto analyzeOneMonths(String marketId, List<CandleMonthResponseDto> dtoList);

//    AiAnalysisResponseDto analyzeOneMonthsWithLatestAnalyze(String marketId, CandleMonthResponseDto dto, AIAnalysisResult result);
}
