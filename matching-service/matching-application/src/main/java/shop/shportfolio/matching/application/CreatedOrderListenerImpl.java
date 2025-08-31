package shop.shportfolio.matching.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.matching.application.memorystore.OrderMemoryStore;
import shop.shportfolio.matching.application.ports.input.kafka.CreatedOrderListener;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.MarketOrder;
import shop.shportfolio.trading.domain.entity.ReservationOrder;

@Component
public class CreatedOrderListenerImpl implements CreatedOrderListener {

    private final OrderMemoryStore orderMemoryStore;

    @Autowired
    public CreatedOrderListenerImpl(OrderMemoryStore orderMemoryStore) {
        this.orderMemoryStore = orderMemoryStore;
    }


    @Override
    public void saveLimitOrder(LimitOrder limitOrder) {
        orderMemoryStore.addLimitOrder(limitOrder);
    }

    @Override
    public void saveMarketOrder(MarketOrder marketOrder) {
        orderMemoryStore.addMarketOrder(marketOrder);
    }

    @Override
    public void saveReservationOrder(ReservationOrder reservationOrder) {
        orderMemoryStore.addReservationOrder(reservationOrder);
    }
}
