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
    CreateReservationResponse createReservationOrder(@Valid CreateReservationOrderCommand createReservationOrderCommand);

    /***
     * 마켓 코드로 마켓 정보 조회
     * @param orderBookTrackQuery
     * @return
     */
//    OrderBookTrackResponse findOrderBook(@Valid OrderBookTrackQuery orderBookTrackQuery);

    /***
     * 지정가 주문 조회
     * @param limitOrderTrackQuery
     * @return
     */
    LimitOrderTrackResponse findLimitOrderTrackByOrderIdAndUserId(@Valid LimitOrderTrackQuery limitOrderTrackQuery);

    ReservationOrderTrackResponse findReservationOrderTrackByOrderIdAndUserId(@Valid ReservationOrderTrackQuery reservationOrderTrackQuery);

    CancelOrderResponse cancelLimitOrder(@Valid CancelLimitOrderCommand cancelLimitOrderCommand);

    CancelOrderResponse cancelReservationOrder(@Valid CancelReservationOrderCommand cancelReservationOrderCommand);
}
