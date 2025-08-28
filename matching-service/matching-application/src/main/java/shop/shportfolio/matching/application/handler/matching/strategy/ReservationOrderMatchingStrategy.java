package shop.shportfolio.matching.application.handler.matching.strategy;

import shop.shportfolio.trading.domain.entity.Order;
import shop.shportfolio.trading.domain.entity.ReservationOrder;
import shop.shportfolio.trading.domain.entity.orderbook.OrderBook;
import shop.shportfolio.trading.domain.valueobject.OrderType;

public class ReservationOrderMatchingStrategy implements OrderMatchingStrategy<ReservationOrder> {
    @Override
    public boolean supports(Order order) {
        return OrderType.RESERVATION.equals(order.getOrderType());
    }

    @Override
    public void match(OrderBook orderBook, ReservationOrder order) {

    }
}
