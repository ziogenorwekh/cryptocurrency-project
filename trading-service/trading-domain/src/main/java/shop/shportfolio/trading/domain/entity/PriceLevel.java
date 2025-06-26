package shop.shportfolio.trading.domain.entity;

import lombok.Getter;
import shop.shportfolio.common.domain.entity.BaseEntity;
import shop.shportfolio.trading.domain.valueobject.PriceLevelId;
import shop.shportfolio.trading.domain.valueobject.TickPrice;

import java.util.LinkedList;
import java.util.Queue;

@Getter
public class PriceLevel extends BaseEntity<PriceLevelId> {

    private final TickPrice tickPrice;
    private final Queue<Order> buyOrders;
    private final Queue<Order> sellOrders;

    public PriceLevel(TickPrice tickPrice, Queue<Order> buyOrders, Queue<Order> sellOrders) {
        this.tickPrice = tickPrice;
        this.buyOrders = buyOrders;
        this.sellOrders = sellOrders;
    }

    public PriceLevel(TickPrice tickPrice) {
        this.tickPrice = tickPrice;
        this.buyOrders = new LinkedList<>();
        this.sellOrders = new LinkedList<>();
    }

    public void addOrder(Order order) {
        if (order.isBuyOrder()) {
            buyOrders.add(order);
        } else {
            sellOrders.add(order);
        }
    }
    public boolean isEmpty() {
        return buyOrders.isEmpty() && sellOrders.isEmpty();
    }
}