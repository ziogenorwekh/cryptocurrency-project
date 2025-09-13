package shop.shportfolio.matching.application.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import shop.shportfolio.matching.application.handler.matching.MatchingEngine;
import shop.shportfolio.matching.application.memorystore.OrderMemoryStore;

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
        orderMemoryStore.getAllLimitOrders().forEach(o -> {
            matchingEngine.executeLimitOrder(o);
        });
        orderMemoryStore.getAllReservationOrders().forEach(o -> {
            matchingEngine.executeReservationOrder(o);
        });
        orderMemoryStore.getAllMarketOrders().forEach(o -> {
            matchingEngine.executeMarketOrder(o);
        });
    }
}
