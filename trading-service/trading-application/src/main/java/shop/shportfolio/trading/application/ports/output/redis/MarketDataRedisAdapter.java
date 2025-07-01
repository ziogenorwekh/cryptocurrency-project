package shop.shportfolio.trading.application.ports.output.redis;

import shop.shportfolio.trading.application.dto.OrderBookDto;
import shop.shportfolio.trading.domain.entity.LimitOrder;

import java.util.Optional;

public interface MarketDataRedisAdapter {

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


    void saveLimitOrder(String key, LimitOrder limitOrder);

    void deleteLimitOrder(String key);
}
