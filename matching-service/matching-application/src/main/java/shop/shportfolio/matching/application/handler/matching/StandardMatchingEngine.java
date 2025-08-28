package shop.shportfolio.matching.application.handler.matching;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.matching.application.handler.OrderBookManager;
import shop.shportfolio.matching.application.handler.matching.strategy.OrderMatchingStrategy;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.MarketOrder;
import shop.shportfolio.trading.domain.entity.Order;
import shop.shportfolio.trading.domain.entity.ReservationOrder;

import java.util.List;

@Component
public class StandardMatchingEngine implements MatchingEngine {

    private final List<OrderMatchingStrategy<? extends Order>> strategies;
    private final OrderBookManager orderBookManager;

    @Autowired
    public StandardMatchingEngine(List<OrderMatchingStrategy<? extends Order>> strategies,
                                  OrderBookManager orderBookManager) {
        this.strategies = strategies;
        this.orderBookManager = orderBookManager;
    }

    @Override
    public void executeMarketOrder(MarketOrder marketOrder) {

    }

    @Override
    public void executeLimitOrder(LimitOrder limitOrder) {

    }

    @Override
    public void executeReservationOrder(ReservationOrder reservationOrder) {

    }
}
