package shop.shportfolio.matching.application.handler.matching.strategy;

import shop.shportfolio.matching.application.dto.order.MatchedContext;
import shop.shportfolio.trading.domain.entity.MarketOrder;
import shop.shportfolio.trading.domain.entity.Order;
import shop.shportfolio.trading.domain.entity.orderbook.OrderBook;
import shop.shportfolio.trading.domain.valueobject.OrderType;

public class MarketOrderMatchingStrategy implements OrderMatchingStrategy<MarketOrder> {
    @Override
    public boolean supports(Order order) {
        return OrderType.MARKET.equals(order.getOrderType());
    }

    @Override
    public MatchedContext<MarketOrder> match(OrderBook orderBook, MarketOrder order) {

    }
}
