package shop.shportfolio.trading.infrastructure.redis.order;

import org.springframework.stereotype.Component;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.MarketOrder;
import shop.shportfolio.trading.domain.entity.ReservationOrder;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
public class OrderMemoryStore {

    private final Queue<LimitOrder> limitOrders = new ConcurrentLinkedQueue<>();
    private final Queue<ReservationOrder> reservationOrders = new ConcurrentLinkedQueue<>();
    private final Queue<MarketOrder> marketOrders = new ConcurrentLinkedQueue<>();

    public Queue<LimitOrder> getLimitOrders() {
        return limitOrders;
    }

    public Queue<ReservationOrder> getReservationOrders() {
        return reservationOrders;
    }

    public Queue<MarketOrder> getMarketOrders() {
        return marketOrders;
    }
}
