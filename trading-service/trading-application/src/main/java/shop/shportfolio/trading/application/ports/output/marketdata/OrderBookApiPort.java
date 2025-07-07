package shop.shportfolio.trading.application.ports.output.marketdata;

import shop.shportfolio.trading.application.dto.orderbook.OrderBookBithumbDto;

public interface OrderBookApiPort {

    OrderBookBithumbDto getOrderBook(String marketId);
}
