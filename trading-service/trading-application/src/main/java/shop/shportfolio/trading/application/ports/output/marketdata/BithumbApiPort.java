package shop.shportfolio.trading.application.ports.output.marketdata;

import shop.shportfolio.trading.application.dto.marketdata.*;
import shop.shportfolio.trading.application.dto.orderbook.OrderBookBithumbDto;

public interface BithumbApiPort {

    OrderBookBithumbDto getOrderBook(String marketId);

    MarketItemBithumbDto getMarketItem(String marketId);

    CandleDayResponseDto  getCandleDay(CandleRequestDto requestDto);

    CandleWeekResponseDto  getCandleWeek(CandleRequestDto requestDto);

    CandleMonthResponseDto getCandleMonth(CandleRequestDto requestDto);

    CandleMinuteResponseDto getCandleMinute(CandleRequestMinuteDto requestDto);
}
