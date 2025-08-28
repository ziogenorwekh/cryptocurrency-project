package shop.shportfolio.matching.application.handler.matching.strategy;

import shop.shportfolio.trading.domain.entity.Order;
import shop.shportfolio.trading.domain.entity.orderbook.OrderBook;

public interface OrderMatchingStrategy<T extends Order> {
    boolean supports(Order order);
    void match(OrderBook orderBook, T order);
}
