package shop.shportfolio.marketdata.insight.application.ports.output.bithumb;

import shop.shportfolio.marketdata.insight.application.dto.candle.request.CandleMinuteRequestDto;
import shop.shportfolio.marketdata.insight.application.dto.candle.request.CandleRequestDto;
import shop.shportfolio.marketdata.insight.application.dto.candle.response.CandleDayResponseDto;
import shop.shportfolio.marketdata.insight.application.dto.candle.response.CandleMinuteResponseDto;
import shop.shportfolio.marketdata.insight.application.dto.candle.response.CandleMonthResponseDto;
import shop.shportfolio.marketdata.insight.application.dto.candle.response.CandleWeekResponseDto;
import shop.shportfolio.marketdata.insight.application.dto.marketdata.MarketItemBithumbDto;

import java.util.List;

public interface BithumbApiPort {

    // 스케쥴러에서 쓸거
    List<MarketItemBithumbDto> findMarketItems();

    List<CandleDayResponseDto> findCandleDays(CandleRequestDto requestDto);

    List<CandleWeekResponseDto> findCandleWeeks(CandleRequestDto requestDto);

    List<CandleMonthResponseDto> findCandleMonths(CandleRequestDto requestDto);

    List<CandleMinuteResponseDto> findCandleMinutes(CandleMinuteRequestDto requestDto);
}
