package shop.shportfolio.trading.application.ports.input;

import shop.shportfolio.trading.application.command.track.LimitOrderTrackQuery;
import shop.shportfolio.trading.application.command.track.OrderBookTrackQuery;
import shop.shportfolio.trading.application.command.track.ReservationOrderTrackQuery;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.OrderBook;
import shop.shportfolio.trading.domain.entity.ReservationOrder;

public interface TradingTrackUseCase {

    OrderBook  findOrderBook(OrderBookTrackQuery orderBookTrackQuery);

    LimitOrder findLimitOrderByOrderId(LimitOrderTrackQuery limitOrderTrackQuery);

    ReservationOrder findReservationOrderByOrderIdAndUserId(ReservationOrderTrackQuery query);
}
