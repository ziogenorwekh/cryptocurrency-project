package shop.shportfolio.trading.application.ports.output.redis;

import shop.shportfolio.trading.application.dto.OrderBookDto;

import java.util.Optional;

public interface TradingDataRedisRepositoryAdapter {

    Optional<OrderBookDto> findOrderBookByMarket(String market);

    OrderBookDto saveOrderBook(OrderBookDto orderBook);
    // 주문서(OrderBook) 관련
//    void saveOrderBook(...);
//    Optional<?> getOrderBook(...);
//    void deleteOrderBook(...);
//
//    // 체결(Trade) 관련
//    void saveTrade(...);
//    List<?> getRecentTrades(...);
}
