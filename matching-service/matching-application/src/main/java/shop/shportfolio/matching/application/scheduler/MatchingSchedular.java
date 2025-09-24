package shop.shportfolio.matching.application.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import shop.shportfolio.matching.application.handler.matching.MatchingEngine;
import shop.shportfolio.matching.application.memorystore.OrderMemoryStore;

import java.util.PriorityQueue;
import java.util.concurrent.locks.Lock;

@Slf4j
@Component
public class MatchingSchedular {

    private final MatchingEngine matchingEngine;
    private final OrderMemoryStore orderMemoryStore;

    @Autowired
    public MatchingSchedular(MatchingEngine matchingEngine,
                             OrderMemoryStore orderMemoryStore) {
        this.matchingEngine = matchingEngine;
        this.orderMemoryStore = orderMemoryStore;
    }

    @Scheduled(fixedRate = 200, initialDelay = 8000)
    public void matching() {
        // Limit Orders
        orderMemoryStore.getLimitOrdersMap().forEach((marketId, queue) -> {
            Lock lock = orderMemoryStore.getLimitOrderLock(marketId);
            lock.lock();
            try {
                // 복사본으로 안전하게 순회
                PriorityQueue<?> snapshot = new PriorityQueue<>(queue);
                snapshot.forEach(order -> matchingEngine.executeLimitOrder((shop.shportfolio.trading.domain.entity.LimitOrder) order));
            } finally {
                lock.unlock();
            }
        });

        // Reservation Orders
        orderMemoryStore.getReservationOrdersMap().forEach((marketId, queue) -> {
            Lock lock = orderMemoryStore.getReservationOrderLock(marketId);
            lock.lock();
            try {
                PriorityQueue<?> snapshot = new PriorityQueue<>(queue);
                snapshot.forEach(order -> matchingEngine.executeReservationOrder((shop.shportfolio.trading.domain.entity.ReservationOrder) order));
            } finally {
                lock.unlock();
            }
        });

        // Market Orders
        orderMemoryStore.getMarketOrdersMap().forEach((marketId, queue) -> {
            Lock lock = orderMemoryStore.getMarketOrderLock(marketId);
            lock.lock();
            try {
                PriorityQueue<?> snapshot = new PriorityQueue<>(queue);
                snapshot.forEach(order -> matchingEngine.executeMarketOrder((shop.shportfolio.trading.domain.entity.MarketOrder) order));
            } finally {
                lock.unlock();
            }
        });
    }
}
