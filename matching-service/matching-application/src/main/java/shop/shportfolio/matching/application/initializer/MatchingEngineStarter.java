package shop.shportfolio.matching.application.initializer;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.event.DomainEvent;
import shop.shportfolio.matching.application.memorystore.OrderMemoryStore;
import shop.shportfolio.matching.application.ports.output.kafka.MatchingEngineStartPublisher;

@Slf4j
@Component
public class MatchingEngineStarter {

//    private final MatchingRepository matchingRepository;
    private final MatchingEngineStartPublisher matchingEngineStartPublisher;
    public MatchingEngineStarter(
//            OrderMemoryStore orderMemoryStore,
//                                  MatchingRepository matchingRepository,
                                 MatchingEngineStartPublisher matchingEngineStartPublisher) {
//        this.orderMemoryStore = orderMemoryStore;
//        this.matchingRepository = matchingRepository;
        this.matchingEngineStartPublisher = matchingEngineStartPublisher;
    }

    @PostConstruct
    public void init() {
        matchingEngineStartPublisher.publish(new DomainEvent() {
            @Override
            public Object getDomainType() {
                return super.getDomainType();
            }
        });
//        OrderContext allOrders = matchingRepository.findAllOrders();
//        log.info("init find AllOrders size is -> {}", allOrders.getLimitOrders().size() + allOrders
//                .getMarketOrders().size() + allOrders.getReservationOrders().size());
//        allOrders.getLimitOrders().forEach(orderMemoryStore::addLimitOrder);
//        allOrders.getReservationOrders().forEach(orderMemoryStore::addReservationOrder);
//        allOrders.getMarketOrders().forEach(orderMemoryStore::addMarketOrder);
    }

}
