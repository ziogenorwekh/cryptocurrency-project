package shop.shportfolio.matching.application.memorystore;

import org.springframework.stereotype.Component;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.MarketOrder;
import shop.shportfolio.trading.domain.entity.ReservationOrder;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class OrderMemoryStore {

    // ----------------------------
    // Comparator 정의
    // ----------------------------
    // LimitOrder: 가격 우선, 생성시간 tie-break
    private final Comparator<LimitOrder> limitOrderComparator = (o1, o2) -> {
        int priceCompare;
        if (o1.isBuyOrder()) {
            // 매수: 가격 높을수록 우선
            priceCompare = o2.getOrderPrice().getValue().compareTo(o1.getOrderPrice().getValue());
        } else {
            // 매도: 가격 낮을수록 우선
            priceCompare = o1.getOrderPrice().getValue().compareTo(o2.getOrderPrice().getValue());
        }
        if (priceCompare != 0) return priceCompare;
        // 가격 같으면 생성 시간 빠른 순
        return o1.getCreatedAt().getValue().compareTo(o2.getCreatedAt().getValue());
    };

    // MarketOrder: 생성 시간 순
    private final Comparator<MarketOrder> marketOrderComparator =
            Comparator.comparing(o -> o.getCreatedAt().getValue());

    // ReservationOrder: 생성 시간 순
    private final Comparator<ReservationOrder> reservationOrderComparator =
            Comparator.comparing(o -> o.getCreatedAt().getValue());

    // ----------------------------
    // Map<MarketId, PriorityQueue<Order>>
    // ----------------------------
    private final Map<String, PriorityQueue<LimitOrder>> limitOrders = new ConcurrentHashMap<>();
    private final Map<String, PriorityQueue<MarketOrder>> marketOrders = new ConcurrentHashMap<>();
    private final Map<String, PriorityQueue<ReservationOrder>> reservationOrders = new ConcurrentHashMap<>();

    // ----------------------------
    // LimitOrder
    // ----------------------------
    public void addLimitOrder(LimitOrder order) {
        limitOrders.computeIfAbsent(order.getMarketId().getValue(),
                k -> new PriorityQueue<>(limitOrderComparator)).add(order);
    }

    public void removeLimitOrder(LimitOrder order) {
        PriorityQueue<LimitOrder> queue = limitOrders.get(order.getMarketId().getValue());
        if (queue != null) queue.remove(order);
    }

    public Optional<LimitOrder> findLimitOrderById(String orderId, String marketId) {
        PriorityQueue<LimitOrder> queue = limitOrders.get(marketId);
        if (queue == null) return Optional.empty();
        return queue.stream().filter(o -> o.getId().getValue().equals(orderId)).findFirst();
    }

    public PriorityQueue<LimitOrder> getLimitOrders(String marketId) {
        return limitOrders.getOrDefault(marketId, new PriorityQueue<>(limitOrderComparator));
    }

    // ----------------------------
    // MarketOrder
    // ----------------------------
    public void addMarketOrder(MarketOrder order) {
        marketOrders.computeIfAbsent(order.getMarketId().getValue(),
                k -> new PriorityQueue<>(marketOrderComparator)).add(order);
    }

    public void removeMarketOrder(MarketOrder order) {
        PriorityQueue<MarketOrder> queue = marketOrders.get(order.getMarketId().getValue());
        if (queue != null) queue.remove(order);
    }

    public Optional<MarketOrder> findMarketOrderById(String orderId, String marketId) {
        PriorityQueue<MarketOrder> queue = marketOrders.get(marketId);
        if (queue == null) return Optional.empty();
        return queue.stream().filter(o -> o.getId().getValue().equals(orderId)).findFirst();
    }

    public PriorityQueue<MarketOrder> getMarketOrders(String marketId) {
        return marketOrders.getOrDefault(marketId, new PriorityQueue<>(marketOrderComparator));
    }

    // ----------------------------
    // ReservationOrder
    // ----------------------------
    public void addReservationOrder(ReservationOrder order) {
        reservationOrders.computeIfAbsent(order.getMarketId().getValue(),
                k -> new PriorityQueue<>(reservationOrderComparator)).add(order);
    }

    public void removeReservationOrder(ReservationOrder order) {
        PriorityQueue<ReservationOrder> queue = reservationOrders.get(order.getMarketId().getValue());
        if (queue != null) queue.remove(order);
    }

    public Optional<ReservationOrder> findReservationOrderById(String orderId, String marketId) {
        PriorityQueue<ReservationOrder> queue = reservationOrders.get(marketId);
        if (queue == null) return Optional.empty();
        return queue.stream().filter(o -> o.getId().getValue().equals(orderId)).findFirst();
    }

    public PriorityQueue<ReservationOrder> getReservationOrders(String marketId) {
        return reservationOrders.getOrDefault(marketId, new PriorityQueue<>(reservationOrderComparator));
    }

    public Map<String, PriorityQueue<LimitOrder>> getLimitOrdersMap() {
        return limitOrders;
    }

    public Map<String, PriorityQueue<ReservationOrder>> getReservationOrdersMap() {
        return reservationOrders;
    }

    public Map<String, PriorityQueue<MarketOrder>> getMarketOrdersMap() {
        return marketOrders;
    }

    // ----------------------------
    // 전체 클리어
    // ----------------------------
    public void clear() {
        limitOrders.clear();
        marketOrders.clear();
        reservationOrders.clear();
    }
}
