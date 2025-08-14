package shop.shportfolio.marketdata.insight.application.ports.input;

import shop.shportfolio.marketdata.insight.application.dto.candle.*;
import shop.shportfolio.marketdata.insight.domain.entity.MarketItem;

import java.util.List;

public interface MarketDataTrackUseCase {

    MarketItem findMarketItemByMarketCode(String marketId);

    List<MarketItem> findAllMarketItems();

    List<CandleDayResponseDto> findCandleDayByMarket(CandleRequestDto dto);

    List<CandleWeekResponseDto> findCandleWeekByMarket(CandleRequestDto dto);

    List<CandleMonthResponseDto> findCandleMonthByMarket(CandleRequestDto dto);

    List<CandleMinuteResponseDto> findCandleMinuteByMarket(CandleMinuteRequestDto dto);
}
