package shop.shportfolio.trading.application.orderbook.matching;

import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.MarketOrder;
import shop.shportfolio.trading.domain.entity.ReservationOrder;
import shop.shportfolio.trading.domain.event.TradeCreatedEvent;

import java.util.List;

public interface OrderMatchingExecutor {
    List<TradeCreatedEvent> executeMarketOrder(MarketOrder marketOrder);
    List<TradeCreatedEvent> executeLimitOrder(LimitOrder limitOrder);
    List<TradeCreatedEvent> executeReservationOrder(ReservationOrder reservationOrder);
}
