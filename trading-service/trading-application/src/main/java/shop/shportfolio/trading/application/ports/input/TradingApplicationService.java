package shop.shportfolio.trading.application.ports.input;

import jakarta.validation.Valid;
import shop.shportfolio.trading.application.command.create.*;
import shop.shportfolio.trading.application.command.track.request.LimitOrderTrackQuery;
import shop.shportfolio.trading.application.command.track.request.OrderTrackQuery;
import shop.shportfolio.trading.application.command.track.request.ReservationOrderTrackQuery;
import shop.shportfolio.trading.application.command.track.response.LimitOrderTrackResponse;
import shop.shportfolio.trading.application.command.track.response.OrderTrackResponse;
import shop.shportfolio.trading.application.command.track.response.ReservationOrderTrackResponse;
import shop.shportfolio.trading.application.command.update.CancelLimitOrderCommand;
import shop.shportfolio.trading.application.command.update.CancelOrderResponse;
import shop.shportfolio.trading.application.command.update.CancelReservationOrderCommand;

import java.util.List;

public interface TradingApplicationService {


    /***
     * 지정가 주문
     * @param createLimitOrderCommand
     * @return
     */
    CreateLimitOrderResponse createLimitOrder(@Valid CreateLimitOrderCommand createLimitOrderCommand);


    /***
     * 시장가 주문
     * @param createMarketOrderCommand
     * @return
     */
    CreateMarketOrderResponse createMarketOrder(@Valid CreateMarketOrderCommand createMarketOrderCommand);

    /***
     * 예약 주문
     * @param createReservationOrderCommand
     * @return
     */
    CreateReservationResponse createReservationOrder
    (@Valid CreateReservationOrderCommand createReservationOrderCommand);

    /***
     * 지정가 주문 조회
     * @param limitOrderTrackQuery
     * @return
     */
    LimitOrderTrackResponse findLimitOrderTrackByOrderIdAndUserId(@Valid LimitOrderTrackQuery limitOrderTrackQuery);

    ReservationOrderTrackResponse findReservationOrderTrackByOrderIdAndUserId(@Valid ReservationOrderTrackQuery reservationOrderTrackQuery);

    CancelOrderResponse cancelRequestLimitOrder(@Valid CancelLimitOrderCommand cancelLimitOrderCommand);

    CancelOrderResponse cancelRequestReservationOrder(@Valid CancelReservationOrderCommand cancelReservationOrderCommand);

    List<OrderTrackResponse> findAllOrderByMarketId(@Valid OrderTrackQuery orderTrackQuery);

}
