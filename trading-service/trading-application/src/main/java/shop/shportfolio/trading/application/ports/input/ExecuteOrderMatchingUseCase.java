package shop.shportfolio.trading.application.ports.input;

import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.MarketOrder;
import shop.shportfolio.trading.domain.entity.ReservationOrder;

public interface ExecuteOrderMatchingUseCase {

    void executeMarketOrder(MarketOrder marketOrder);
    void executeLimitOrder(LimitOrder limitOrder);
    void executeReservationOrder(ReservationOrder reservationOrder);
}
