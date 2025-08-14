package shop.shportfolio.marketdata.insight.application.ports.output.ai;

import jakarta.validation.Valid;
import shop.shportfolio.marketdata.insight.application.command.request.AiAnalysisTrackQuery;
import shop.shportfolio.marketdata.insight.application.dto.candle.CandleDayResponseDto;
import shop.shportfolio.marketdata.insight.application.dto.candle.CandleMinuteResponseDto;
import shop.shportfolio.marketdata.insight.application.dto.candle.CandleMonthResponseDto;
import shop.shportfolio.marketdata.insight.application.dto.candle.CandleWeekResponseDto;
import shop.shportfolio.marketdata.insight.domain.entity.AIAnalysisResult;

import java.util.List;

public interface OpenAiPort {

    AIAnalysisResult analyzeThirtyMinutes(List<CandleMinuteResponseDto> dtoList);

    AIAnalysisResult analyzeOneHours(List<CandleMinuteResponseDto> dtoList);

    AIAnalysisResult analyzeDays(List<CandleDayResponseDto> dtoList);

    AIAnalysisResult analyzeWeeks(List<CandleWeekResponseDto> dtoList);

    AIAnalysisResult analyzeOneMonths(List<CandleMonthResponseDto> dtoList);
}
