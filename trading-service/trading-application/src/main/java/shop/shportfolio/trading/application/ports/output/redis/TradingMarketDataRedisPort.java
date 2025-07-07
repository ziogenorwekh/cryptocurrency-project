package shop.shportfolio.trading.application.ports.output.redis;

import shop.shportfolio.trading.application.dto.orderbook.OrderBookBithumbDto;

import java.util.Optional;

public interface TradingMarketDataRedisPort {

    Optional<OrderBookBithumbDto> findOrderBookByMarket(String market);
    void saveOrderBook(String key, OrderBookBithumbDto orderBook);
}
