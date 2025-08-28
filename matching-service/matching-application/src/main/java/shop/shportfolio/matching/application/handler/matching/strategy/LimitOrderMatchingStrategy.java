package shop.shportfolio.matching.application.handler.matching.strategy;

import org.springframework.stereotype.Component;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.Order;
import shop.shportfolio.trading.domain.entity.orderbook.OrderBook;
import shop.shportfolio.trading.domain.valueobject.OrderType;

@Component
public class LimitOrderMatchingStrategy implements OrderMatchingStrategy<LimitOrder> {

    @Override
    public boolean supports(Order order) {
        return OrderType.LIMIT.equals(order.getOrderType());
    }

    @Override
    public void match(OrderBook orderBook, LimitOrder order) {

    }
}
