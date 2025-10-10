package shop.shportfolio.matching.application.ports.output.repository;

import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.MarketOrder;
import shop.shportfolio.trading.domain.entity.ReservationOrder;

import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.concurrent.locks.ReentrantLock;

public interface OrderStore {

    void addLimitOrder(LimitOrder order);
    void removeLimitOrder(LimitOrder order);
    Optional<LimitOrder> findLimitOrderById(String orderId, String marketId);
    PriorityQueue<LimitOrder> getLimitOrders(String marketId);
    Map<String, PriorityQueue<LimitOrder>> getLimitOrdersMap();
    ReentrantLock getLimitOrderLock(String marketId);

    void addMarketOrder(MarketOrder order);
    void removeMarketOrder(MarketOrder order);
    Optional<MarketOrder> findMarketOrderById(String orderId, String marketId);
    PriorityQueue<MarketOrder> getMarketOrders(String marketId);
    Map<String, PriorityQueue<MarketOrder>> getMarketOrdersMap();
    ReentrantLock getMarketOrderLock(String marketId);

    void addReservationOrder(ReservationOrder order);
    void removeReservationOrder(ReservationOrder order);
    Optional<ReservationOrder> findReservationOrderById(String orderId, String marketId);
    PriorityQueue<ReservationOrder> getReservationOrders(String marketId);
    Map<String, PriorityQueue<ReservationOrder>> getReservationOrdersMap();
    ReentrantLock getReservationOrderLock(String marketId);

    // ----------------------------
    // 전체 클리어
    // ----------------------------
    void clear();
}
