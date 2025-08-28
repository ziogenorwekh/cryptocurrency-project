package shop.shportfolio.matching.application.handler.matching.strategy;

import shop.shportfolio.matching.application.dto.order.MatchedContext;
import shop.shportfolio.trading.domain.entity.Order;
import shop.shportfolio.trading.domain.entity.orderbook.OrderBook;

public interface OrderMatchingStrategy<T extends Order> {
    boolean supports(Order order);
    MatchedContext<T> match(OrderBook orderBook, T order);
}
