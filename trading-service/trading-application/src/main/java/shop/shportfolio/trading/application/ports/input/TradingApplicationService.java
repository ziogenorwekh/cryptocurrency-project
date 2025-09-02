package shop.shportfolio.trading.application.ports.input;

import jakarta.validation.Valid;
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

    CreateLimitOrderResponse createLimitOrder(@Valid CreateLimitOrderCommand createLimitOrderCommand);

    CreateMarketOrderResponse createMarketOrder(@Valid CreateMarketOrderCommand createMarketOrderCommand);

    CreateReservationResponse createReservationOrder(@Valid CreateReservationOrderCommand createReservationOrderCommand);

    OrderBookTrackResponse findOrderBook(@Valid OrderBookTrackQuery orderBookTrackQuery);

    LimitOrderTrackResponse findLimitOrderTrackByOrderIdAndUserId(@Valid LimitOrderTrackQuery limitOrderTrackQuery);

    ReservationOrderTrackResponse findReservationOrderTrackByOrderIdAndUserId(@Valid ReservationOrderTrackQuery reservationOrderTrackQuery);

    CancelOrderResponse cancelLimitOrder(@Valid CancelLimitOrderCommand cancelLimitOrderCommand);

    CancelOrderResponse cancelReservationOrder(@Valid CancelReservationOrderCommand cancelReservationOrderCommand);
}
