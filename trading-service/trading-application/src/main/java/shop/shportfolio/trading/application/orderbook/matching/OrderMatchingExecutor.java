package shop.shportfolio.trading.application.orderbook.matching;

import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.MarketOrder;
import shop.shportfolio.trading.domain.entity.ReservationOrder;

public interface OrderMatchingExecutor {
    void executeMarketOrder(MarketOrder marketOrder);
    void executeLimitOrder(LimitOrder limitOrder);
    void executeReservationOrder(ReservationOrder reservationOrder);
}
