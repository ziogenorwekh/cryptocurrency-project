package shop.shportfolio.matching.application.initializer;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import shop.shportfolio.matching.application.dto.order.OrderContext;
import shop.shportfolio.matching.application.memorystore.OrderMemoryStore;
import shop.shportfolio.matching.application.ports.output.repository.MatchingRepository;

@Slf4j
@Component
public class PutAllOrderMemoryStore {

    private final OrderMemoryStore orderMemoryStore;
    private final MatchingRepository matchingRepository;

    public PutAllOrderMemoryStore(OrderMemoryStore orderMemoryStore,
                                  MatchingRepository matchingRepository) {
        this.orderMemoryStore = orderMemoryStore;
        this.matchingRepository = matchingRepository;
    }

    @PostConstruct
    public void init() {
        OrderContext allOrders = matchingRepository.findAllOrders();
        log.info("init find AllOrders size is -> {}", allOrders.getLimitOrders().size() + allOrders
                .getMarketOrders().size() + allOrders.getReservationOrders().size());
        allOrders.getLimitOrders().forEach(orderMemoryStore::addLimitOrder);
        allOrders.getReservationOrders().forEach(orderMemoryStore::addReservationOrder);
        allOrders.getMarketOrders().forEach(orderMemoryStore::addMarketOrder);
    }

}
