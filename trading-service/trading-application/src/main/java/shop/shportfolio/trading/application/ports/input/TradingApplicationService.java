package shop.shportfolio.trading.application.ports.input;

import shop.shportfolio.trading.application.command.create.*;
import shop.shportfolio.trading.application.command.track.request.LimitOrderTrackQuery;
import shop.shportfolio.trading.application.command.track.request.OrderBookTrackQuery;
import shop.shportfolio.trading.application.command.track.request.ReservationOrderTrackQuery;
import shop.shportfolio.trading.application.command.track.request.TickerTrackQuery;
import shop.shportfolio.trading.application.command.track.response.LimitOrderTrackResponse;
import shop.shportfolio.trading.application.command.track.response.OrderBookTrackResponse;
import shop.shportfolio.trading.application.command.track.response.ReservationOrderTrackResponse;
import shop.shportfolio.trading.application.command.track.response.TickerTrackResponse;
import shop.shportfolio.trading.application.command.update.CancelLimitOrderCommand;
import shop.shportfolio.trading.application.command.update.CancelOrderResponse;
import shop.shportfolio.trading.application.command.update.CancelReservationOrderCommand;

public interface TradingApplicationService {

    CreateLimitOrderResponse createLimitOrder(CreateLimitOrderCommand createLimitOrderCommand);

    void createMarketOrder(CreateMarketOrderCommand createMarketOrderCommand);

    CreateReservationResponse createReservationOrder(CreateReservationOrderCommand createReservationOrderCommand);

    OrderBookTrackResponse findOrderBook(OrderBookTrackQuery orderBookTrackQuery);

    LimitOrderTrackResponse findLimitOrderTrackByOrderIdAndUserId(LimitOrderTrackQuery limitOrderTrackQuery);

    ReservationOrderTrackResponse findReservationOrderTrackByOrderIdAndUserId(ReservationOrderTrackQuery reservationOrderTrackQuery);

    CancelOrderResponse cancelLimitOrder(CancelLimitOrderCommand cancelLimitOrderCommand);

    CancelOrderResponse cancelReservationOrder(CancelReservationOrderCommand cancelReservationOrderCommand);

    TickerTrackResponse  findTickerByMarketId(TickerTrackQuery tickerTrackQuery);
}
