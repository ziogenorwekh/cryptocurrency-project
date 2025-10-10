package shop.shportfolio.matching.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.matching.application.ports.output.repository.OrderStore;
import shop.shportfolio.matching.application.ports.input.kafka.CreatedOrderListener;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.MarketOrder;
import shop.shportfolio.trading.domain.entity.ReservationOrder;

@Slf4j
@Component
public class CreatedOrderListenerImpl implements CreatedOrderListener {

    private final OrderStore orderStore;

    @Autowired
    public CreatedOrderListenerImpl(OrderStore orderStore) {
        this.orderStore = orderStore;
    }


    @Override
    public void saveLimitOrder(LimitOrder limitOrder) {
        log.info("save limit order is -> {}", limitOrder.toString());
        orderStore.addLimitOrder(limitOrder);
    }

    @Override
    public void saveMarketOrder(MarketOrder marketOrder) {
        log.info("save market order is -> {}", marketOrder.toString());
        orderStore.addMarketOrder(marketOrder);
    }

    @Override
    public void saveReservationOrder(ReservationOrder reservationOrder) {
        log.info("save reservation order is -> {}", reservationOrder.toString());
        orderStore.addReservationOrder(reservationOrder);
    }
}
