package shop.shportfolio.matching.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.matching.application.memorystore.OrderMemoryStore;
import shop.shportfolio.matching.application.ports.input.kafka.CreatedOrderListener;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.MarketOrder;
import shop.shportfolio.trading.domain.entity.ReservationOrder;

@Slf4j
@Component
public class CreatedOrderListenerImpl implements CreatedOrderListener {

    private final OrderMemoryStore orderMemoryStore;

    @Autowired
    public CreatedOrderListenerImpl(OrderMemoryStore orderMemoryStore) {
        this.orderMemoryStore = orderMemoryStore;
    }


    @Override
    public void saveLimitOrder(LimitOrder limitOrder) {
        log.info("save limit order is -> {}", limitOrder.toString());
        orderMemoryStore.addLimitOrder(limitOrder);
    }

    @Override
    public void saveMarketOrder(MarketOrder marketOrder) {
        log.info("save market order is -> {}", marketOrder.toString());
        orderMemoryStore.addMarketOrder(marketOrder);
    }

    @Override
    public void saveReservationOrder(ReservationOrder reservationOrder) {
        log.info("save reservation order is -> {}", reservationOrder.toString());
        orderMemoryStore.addReservationOrder(reservationOrder);
    }
}
