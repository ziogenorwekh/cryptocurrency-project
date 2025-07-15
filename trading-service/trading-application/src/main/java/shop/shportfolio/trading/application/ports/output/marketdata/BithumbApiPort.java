package shop.shportfolio.trading.application.ports.output.marketdata;

import shop.shportfolio.trading.application.dto.marketdata.*;
import shop.shportfolio.trading.application.dto.marketdata.candle.*;
import shop.shportfolio.trading.application.dto.marketdata.ticker.MarketTickerRequestDto;
import shop.shportfolio.trading.application.dto.marketdata.ticker.MarketTickerResponseDto;
import shop.shportfolio.trading.application.dto.orderbook.OrderBookBithumbDto;

import java.util.List;

public interface BithumbApiPort {

    OrderBookBithumbDto findOrderBookByMarketId(String marketId);

    MarketItemBithumbDto findMarketItemByMarketId(String marketId);

    List<CandleDayResponseDto> findCandleDays(CandleRequestDto requestDto);

    List<CandleWeekResponseDto> findCandleWeeks(CandleRequestDto requestDto);

    List<CandleMonthResponseDto> findCandleMonths(CandleRequestDto requestDto);

    List<CandleMinuteResponseDto> findCandleMinutes(CandleMinuteRequestDto requestDto);

    MarketTickerResponseDto findTickerByMarketId(MarketTickerRequestDto marketTickerRequestDto);
}
