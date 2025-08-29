package shop.shportfolio.matching.application.ports.input.kafka;


import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.MarketOrder;
import shop.shportfolio.trading.domain.entity.ReservationOrder;

public interface CreatedOrderListener {

    void saveLimitOrder(LimitOrder limitOrder);

    void saveMarketOrder(MarketOrder marketOrder);

    void saveReservationOrder(ReservationOrder reservationOrder);
}
