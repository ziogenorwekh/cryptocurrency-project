package shop.shportfolio.trading.application.ports.input;

import jakarta.validation.Valid;
import shop.shportfolio.trading.application.command.create.*;
import shop.shportfolio.trading.application.command.track.*;
import shop.shportfolio.trading.application.command.update.CancelLimitOrderCommand;
import shop.shportfolio.trading.application.command.update.CancelOrderResponse;
import shop.shportfolio.trading.application.command.update.CancelReservationOrderCommand;

import java.util.List;

public interface TradingApplicationService {

    CreateLimitOrderResponse createLimitOrder(@Valid CreateLimitOrderCommand createLimitOrderCommand);

    void createMarketOrder(@Valid CreateMarketOrderCommand createMarketOrderCommand);

    CreateReservationResponse createReservationOrder(@Valid CreateReservationOrderCommand createReservationOrderCommand);

    OrderBookTrackResponse findOrderBook(@Valid OrderBookTrackQuery orderBookTrackQuery);

    LimitOrderTrackResponse findLimitOrderTrackByOrderIdAndUserId(@Valid LimitOrderTrackQuery limitOrderTrackQuery);

    ReservationOrderTrackResponse findReservationOrderTrackByOrderIdAndUserId(@Valid ReservationOrderTrackQuery reservationOrderTrackQuery);

    CancelOrderResponse cancelLimitOrder(@Valid CancelLimitOrderCommand cancelLimitOrderCommand);

    CancelOrderResponse cancelReservationOrder(@Valid CancelReservationOrderCommand cancelReservationOrderCommand);


    MarketCodeTrackResponse findMarketById(@Valid MarketTrackQuery marketTrackQuery);

    List<MarketCodeTrackResponse> findAllMarkets();


}
