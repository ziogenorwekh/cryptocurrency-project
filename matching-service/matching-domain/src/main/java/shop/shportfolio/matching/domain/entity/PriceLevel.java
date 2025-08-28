package shop.shportfolio.matching.domain.entity;

import lombok.Getter;
import shop.shportfolio.common.domain.entity.BaseEntity;
import shop.shportfolio.trading.domain.entity.Order;
import shop.shportfolio.trading.domain.valueobject.PriceLevelId;
import shop.shportfolio.trading.domain.valueobject.TickPrice;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Getter
public class PriceLevel extends BaseEntity<PriceLevelId> {

    private final TickPrice tickPrice;
    private final Queue<Order> orders;

    public PriceLevel(TickPrice tickPrice) {
        this.tickPrice = tickPrice;
        this.orders = new ConcurrentLinkedQueue<>();
    }

    public void addOrder(Order order) {
        orders.add(order);
    }

    public Order peekOrder() {
        return orders.peek();
    }

    public Order popOrder() {
        return orders.poll();
    }

    public boolean isEmpty() {
        return orders.isEmpty();
    }
}