package shop.shportfolio.matching.application.handler.matching.strategy;

import shop.shportfolio.matching.application.dto.order.MatchedContext;
import shop.shportfolio.matching.domain.entity.MatchingOrderBook;
import shop.shportfolio.trading.domain.entity.Order;

public interface OrderMatchingStrategy<T extends Order> {
    boolean supports(Order order);
    MatchedContext<T> match(MatchingOrderBook matchingOrderBook, T order);
}
