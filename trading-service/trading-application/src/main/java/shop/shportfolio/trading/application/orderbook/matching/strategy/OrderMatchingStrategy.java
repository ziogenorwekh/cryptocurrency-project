package shop.shportfolio.trading.application.orderbook.matching.strategy;

import shop.shportfolio.trading.application.dto.context.TradeMatchingContext;
import shop.shportfolio.trading.domain.entity.Order;
import shop.shportfolio.trading.domain.entity.orderbook.OrderBook;
import shop.shportfolio.trading.domain.event.TradeCreatedEvent;

import java.util.List;

public interface OrderMatchingStrategy<T extends Order> {
    boolean supports(Order order);
    TradeMatchingContext match(OrderBook orderBook, T order);
}
