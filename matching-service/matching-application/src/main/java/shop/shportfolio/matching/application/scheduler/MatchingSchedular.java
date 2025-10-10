package shop.shportfolio.matching.application.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import shop.shportfolio.matching.application.handler.matching.MatchingEngine;
import shop.shportfolio.matching.application.ports.output.repository.OrderStore;

import java.util.PriorityQueue;
import java.util.concurrent.locks.Lock;

@Slf4j
@Component
public class MatchingSchedular {

    private final MatchingEngine matchingEngine;
    private final OrderStore orderStore;

    @Autowired
    public MatchingSchedular(MatchingEngine matchingEngine,
                             OrderStore orderStore) {
        this.matchingEngine = matchingEngine;
        this.orderStore = orderStore;
    }

    @Scheduled(fixedRate = 200, initialDelay = 8000)
    public void matching() {
        // Limit Orders
        orderStore.getLimitOrdersMap().forEach((marketId, queue) -> {
            Lock lock = orderStore.getLimitOrderLock(marketId);
            lock.lock();
            try {
                PriorityQueue<?> snapshot = new PriorityQueue<>(queue);
                snapshot.forEach(order -> matchingEngine.executeLimitOrder(
                        (shop.shportfolio.trading.domain.entity.LimitOrder) order));
            } finally {
                lock.unlock();
            }
        });

        // Reservation Orders
        orderStore.getReservationOrdersMap().forEach((marketId, queue) -> {
            Lock lock = orderStore.getReservationOrderLock(marketId);
            lock.lock();
            try {
                PriorityQueue<?> snapshot = new PriorityQueue<>(queue);
                snapshot.forEach(order -> matchingEngine.executeReservationOrder((shop.shportfolio.trading.domain.entity.ReservationOrder) order));
            } finally {
                lock.unlock();
            }
        });

        // Market Orders
        orderStore.getMarketOrdersMap().forEach((marketId, queue) -> {
            Lock lock = orderStore.getMarketOrderLock(marketId);
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
