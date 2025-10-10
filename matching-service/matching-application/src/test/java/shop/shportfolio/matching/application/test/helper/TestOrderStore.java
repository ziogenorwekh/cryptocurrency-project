package shop.shportfolio.matching.application.test.helper;

import org.springframework.stereotype.Component;
import shop.shportfolio.matching.application.ports.output.repository.OrderStore;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.MarketOrder;
import shop.shportfolio.trading.domain.entity.ReservationOrder;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class TestOrderStore implements OrderStore {

    // ----------------------------
    // Comparator 정의
    // ----------------------------
    private final Comparator<LimitOrder> limitOrderComparator = (o1, o2) -> {
        int priceCompare = o1.isBuyOrder()
                ? o2.getOrderPrice().getValue().compareTo(o1.getOrderPrice().getValue())
                : o1.getOrderPrice().getValue().compareTo(o2.getOrderPrice().getValue());
        if (priceCompare != 0) return priceCompare;
        return o1.getCreatedAt().getValue().compareTo(o2.getCreatedAt().getValue());
    };

    private final Comparator<MarketOrder> marketOrderComparator =
            Comparator.comparing(o -> o.getCreatedAt().getValue());

    private final Comparator<ReservationOrder> reservationOrderComparator =
            Comparator.comparing(o -> o.getCreatedAt().getValue());

    // ----------------------------
    // Map<MarketId, PriorityQueue<Order>>
    // ----------------------------
    private final Map<String, PriorityQueue<LimitOrder>> limitOrders = new ConcurrentHashMap<>();
    private final Map<String, PriorityQueue<MarketOrder>> marketOrders = new ConcurrentHashMap<>();
    private final Map<String, PriorityQueue<ReservationOrder>> reservationOrders = new ConcurrentHashMap<>();

    // ----------------------------
    // Locks
    // ----------------------------
    private final Map<String, ReentrantLock> limitOrderLocks = new ConcurrentHashMap<>();
    private final Map<String, ReentrantLock> marketOrderLocks = new ConcurrentHashMap<>();
    private final Map<String, ReentrantLock> reservationOrderLocks = new ConcurrentHashMap<>();

    // ----------------------------
    // LimitOrder
    // ----------------------------
    public void addLimitOrder(LimitOrder order) {
        ReentrantLock lock = limitOrderLocks.computeIfAbsent(order.getMarketId().getValue(), k -> new ReentrantLock());
        lock.lock();
        try {
            limitOrders.computeIfAbsent(order.getMarketId().getValue(),
                    k -> new PriorityQueue<>(limitOrderComparator)).add(order);
        } finally {
            lock.unlock();
        }
    }

    public void removeLimitOrder(LimitOrder order) {
        ReentrantLock lock = limitOrderLocks.computeIfAbsent(order.getMarketId().getValue(), k -> new ReentrantLock());
        lock.lock();
        try {
            PriorityQueue<LimitOrder> queue = limitOrders.get(order.getMarketId().getValue());
            if (queue != null) queue.remove(order);
        } finally {
            lock.unlock();
        }
    }

    public Optional<LimitOrder> findLimitOrderById(String orderId, String marketId) {
        ReentrantLock lock = limitOrderLocks.computeIfAbsent(marketId, k -> new ReentrantLock());
        lock.lock();
        try {
            PriorityQueue<LimitOrder> queue = limitOrders.get(marketId);
            if (queue == null) return Optional.empty();
            return queue.stream().filter(o -> o.getId().getValue().equals(orderId)).findFirst();
        } finally {
            lock.unlock();
        }
    }

    public PriorityQueue<LimitOrder> getLimitOrders(String marketId) {
        ReentrantLock lock = limitOrderLocks.computeIfAbsent(marketId, k -> new ReentrantLock());
        lock.lock();
        try {
            return new PriorityQueue<>(limitOrders.getOrDefault(marketId, new PriorityQueue<>(limitOrderComparator)));
        } finally {
            lock.unlock();
        }
    }

    public Map<String, PriorityQueue<LimitOrder>> getLimitOrdersMap() {
        return limitOrders;
    }

    // ----------------------------
    // MarketOrder
    // ----------------------------
    public void addMarketOrder(MarketOrder order) {
        ReentrantLock lock = marketOrderLocks.computeIfAbsent(order.getMarketId().getValue(), k -> new ReentrantLock());
        lock.lock();
        try {
            marketOrders.computeIfAbsent(order.getMarketId().getValue(),
                    k -> new PriorityQueue<>(marketOrderComparator)).add(order);
        } finally {
            lock.unlock();
        }
    }

    public void removeMarketOrder(MarketOrder order) {
        ReentrantLock lock = marketOrderLocks.computeIfAbsent(order.getMarketId().getValue(), k -> new ReentrantLock());
        lock.lock();
        try {
            PriorityQueue<MarketOrder> queue = marketOrders.get(order.getMarketId().getValue());
            if (queue != null) queue.remove(order);
        } finally {
            lock.unlock();
        }
    }

    public Optional<MarketOrder> findMarketOrderById(String orderId, String marketId) {
        ReentrantLock lock = marketOrderLocks.computeIfAbsent(marketId, k -> new ReentrantLock());
        lock.lock();
        try {
            PriorityQueue<MarketOrder> queue = marketOrders.get(marketId);
            if (queue == null) return Optional.empty();
            return queue.stream().filter(o -> o.getId().getValue().equals(orderId)).findFirst();
        } finally {
            lock.unlock();
        }
    }

    public PriorityQueue<MarketOrder> getMarketOrders(String marketId) {
        ReentrantLock lock = marketOrderLocks.computeIfAbsent(marketId, k -> new ReentrantLock());
        lock.lock();
        try {
            return new PriorityQueue<>(marketOrders.getOrDefault(marketId, new PriorityQueue<>(marketOrderComparator)));
        } finally {
            lock.unlock();
        }
    }

    public Map<String, PriorityQueue<MarketOrder>> getMarketOrdersMap() {
        return marketOrders;
    }

    // ----------------------------
    // ReservationOrder
    // ----------------------------
    public void addReservationOrder(ReservationOrder order) {
        ReentrantLock lock = reservationOrderLocks.computeIfAbsent(order.getMarketId().getValue(), k -> new ReentrantLock());
        lock.lock();
        try {
            reservationOrders.computeIfAbsent(order.getMarketId().getValue(),
                    k -> new PriorityQueue<>(reservationOrderComparator)).add(order);
        } finally {
            lock.unlock();
        }
    }

    public void removeReservationOrder(ReservationOrder order) {
        ReentrantLock lock = reservationOrderLocks.computeIfAbsent(order.getMarketId().getValue(), k -> new ReentrantLock());
        lock.lock();
        try {
            PriorityQueue<ReservationOrder> queue = reservationOrders.get(order.getMarketId().getValue());
            if (queue != null) queue.remove(order);
        } finally {
            lock.unlock();
        }
    }

    public Optional<ReservationOrder> findReservationOrderById(String orderId, String marketId) {
        ReentrantLock lock = reservationOrderLocks.computeIfAbsent(marketId, k -> new ReentrantLock());
        lock.lock();
        try {
            PriorityQueue<ReservationOrder> queue = reservationOrders.get(marketId);
            if (queue == null) return Optional.empty();
            return queue.stream().filter(o -> o.getId().getValue().equals(orderId)).findFirst();
        } finally {
            lock.unlock();
        }
    }

    public PriorityQueue<ReservationOrder> getReservationOrders(String marketId) {
        ReentrantLock lock = reservationOrderLocks.computeIfAbsent(marketId, k -> new ReentrantLock());
        lock.lock();
        try {
            return new PriorityQueue<>(reservationOrders.getOrDefault(marketId, new PriorityQueue<>(reservationOrderComparator)));
        } finally {
            lock.unlock();
        }
    }

    public Map<String, PriorityQueue<ReservationOrder>> getReservationOrdersMap() {
        return reservationOrders;
    }

    public ReentrantLock getLimitOrderLock(String marketId) {
        return limitOrderLocks.computeIfAbsent(marketId, k -> new ReentrantLock());
    }

    public ReentrantLock getMarketOrderLock(String marketId) {
        return marketOrderLocks.computeIfAbsent(marketId, k -> new ReentrantLock());
    }

    public ReentrantLock getReservationOrderLock(String marketId) {
        return reservationOrderLocks.computeIfAbsent(marketId, k -> new ReentrantLock());
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
