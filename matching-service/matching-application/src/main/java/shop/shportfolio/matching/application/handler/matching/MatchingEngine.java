package shop.shportfolio.matching.application.handler.matching;

import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.MarketOrder;
import shop.shportfolio.trading.domain.entity.ReservationOrder;

public interface MatchingEngine {
    void executeMarketOrder(MarketOrder marketOrder);
    void executeLimitOrder(LimitOrder limitOrder);
    void executeReservationOrder(ReservationOrder reservationOrder);
}
