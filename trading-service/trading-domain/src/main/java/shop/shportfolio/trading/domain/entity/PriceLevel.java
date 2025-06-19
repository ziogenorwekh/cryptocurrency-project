package shop.shportfolio.trading.domain.entity;

import lombok.Getter;
import shop.shportfolio.common.domain.entity.BaseEntity;
import shop.shportfolio.trading.domain.valueobject.PriceLevelId;
import shop.shportfolio.trading.domain.valueobject.PriceLevelPrice;

import java.util.LinkedList;
import java.util.Queue;

@Getter
@Deprecated
public class PriceLevel extends BaseEntity<PriceLevelId> {

    private final PriceLevelPrice priceLevelPrice;
    private final Queue<Order> buyOrders;
    private final Queue<Order> sellOrders;

    public PriceLevel(PriceLevelPrice priceLevelPrice, Queue<Order> buyOrders, Queue<Order> sellOrders) {
        this.priceLevelPrice = priceLevelPrice;
        this.buyOrders = buyOrders;
        this.sellOrders = sellOrders;
    }

    public PriceLevel(PriceLevelPrice priceLevelPrice) {
        this.priceLevelPrice = priceLevelPrice;
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
}