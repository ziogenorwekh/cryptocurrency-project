package shop.shportfolio.matching.application.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import shop.shportfolio.matching.application.handler.matching.MatchingEngine;
import shop.shportfolio.matching.application.memorystore.OrderMemoryStore;
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

    @Scheduled(fixedRate = 200)
    public void matching() {
        orderMemoryStore.getLimitOrdersMap().forEach((marketId, queue) -> {
            queue.forEach(matchingEngine::executeLimitOrder);
        });

        orderMemoryStore.getReservationOrdersMap().forEach((marketId, queue) -> {
            queue.forEach(matchingEngine::executeReservationOrder);
        });

        orderMemoryStore.getMarketOrdersMap().forEach((marketId, queue) -> {
            queue.forEach(matchingEngine::executeMarketOrder);
        });
    }
}
