package shop.shportfolio.trading.application.ports.output.marketdata;

import shop.shportfolio.trading.application.dto.orderbook.OrderBookDto;

public interface OrderBookApiPort {

    OrderBookDto getOrderBook(String marketId);
}
