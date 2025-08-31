package shop.shportfolio.matching.application.memorystore;

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

    // --- LimitOrder 관련 ---
    public void addLimitOrder(LimitOrder order) {
        limitOrders.add(order);
    }

    public boolean removeLimitOrder(LimitOrder order) {
        return limitOrders.remove(order);
    }

    public LimitOrder findLimitOrderById(String orderId) {
        return limitOrders.stream()
                .filter(o -> o.getId().getValue().equals(orderId))
                .findFirst()
                .orElse(null);
    }

    public Queue<LimitOrder> getAllLimitOrders() {
        return limitOrders;
    }

    // --- ReservationOrder 관련 ---
    public void addReservationOrder(ReservationOrder order) {
        reservationOrders.add(order);
    }

    public boolean removeReservationOrder(ReservationOrder order) {
        return reservationOrders.remove(order);
    }

    public ReservationOrder findReservationOrderById(String orderId) {
        return reservationOrders.stream()
                .filter(o -> o.getId().getValue().equals(orderId))
                .findFirst()
                .orElse(null);
    }

    public Queue<ReservationOrder> getAllReservationOrders() {
        return reservationOrders;
    }

    // --- MarketOrder 관련 ---
    public void addMarketOrder(MarketOrder order) {
        marketOrders.add(order);
    }

    public boolean removeMarketOrder(MarketOrder order) {
        return marketOrders.remove(order);
    }

    public MarketOrder findMarketOrderById(String orderId) {
        return marketOrders.stream()
                .filter(o -> o.getId().getValue().equals(orderId))
                .findFirst()
                .orElse(null);
    }

    public Queue<MarketOrder> getAllMarketOrders() {
        return marketOrders;
    }

    public void clear() {
        limitOrders.clear();
        reservationOrders.clear();
        marketOrders.clear();
    }
}
