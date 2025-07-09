package shop.shportfolio.trading.application.ports.output.marketdata;

import shop.shportfolio.trading.application.dto.marketdata.CandleRequestDto;
import shop.shportfolio.trading.application.dto.marketdata.CandleDayResponseDto;
import shop.shportfolio.trading.application.dto.marketdata.MarketItemBithumbDto;
import shop.shportfolio.trading.application.dto.orderbook.OrderBookBithumbDto;

public interface BithumbApiPort {

    OrderBookBithumbDto getOrderBook(String marketId);

    MarketItemBithumbDto getMarketItem(String marketId);

    CandleDayResponseDto  getCandleDay(CandleRequestDto requestDto);

}
