package shop.shportfolio.trading.application.handler.matching.strategy;

import shop.shportfolio.trading.domain.entity.Order;
import shop.shportfolio.trading.domain.entity.orderbook.OrderBook;
import shop.shportfolio.trading.domain.event.TradingRecordedEvent;

import java.util.List;

public interface OrderMatchingStrategy<T extends Order> {
    boolean supports(Order order);
    List<TradingRecordedEvent> match(OrderBook orderBook, T order);
}
