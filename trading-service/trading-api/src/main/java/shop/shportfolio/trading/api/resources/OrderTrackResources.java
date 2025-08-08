package shop.shportfolio.trading.api.resources;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.shportfolio.trading.application.command.track.request.LimitOrderTrackQuery;
import shop.shportfolio.trading.application.command.track.request.OrderBookTrackQuery;
import shop.shportfolio.trading.application.command.track.request.ReservationOrderTrackQuery;
import shop.shportfolio.trading.application.command.track.response.LimitOrderTrackResponse;
import shop.shportfolio.trading.application.command.track.response.OrderBookTrackResponse;
import shop.shportfolio.trading.application.command.track.response.ReservationOrderTrackResponse;
import shop.shportfolio.trading.application.ports.input.TradingApplicationService;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api")
public class OrderTrackResources {

    private final TradingApplicationService tradingApplicationService;

    @Autowired
    public OrderTrackResources(TradingApplicationService tradingApplicationService) {
        this.tradingApplicationService = tradingApplicationService;
    }

    @RequestMapping(path = "/track/limit/{orderId}",method = RequestMethod.GET)
    public ResponseEntity<LimitOrderTrackResponse> findLimitOrderTrack(
            @RequestBody LimitOrderTrackQuery limitOrderTrackQuery,
    @RequestHeader("X-header-User-Id")UUID tokenUserId) {
        limitOrderTrackQuery.setUserId(tokenUserId);
        LimitOrderTrackResponse response = tradingApplicationService
                .findLimitOrderTrackByOrderIdAndUserId(limitOrderTrackQuery);
        return ResponseEntity.ok(response);
    }

    @RequestMapping(path = "/track/reservation/{orderId}",method = RequestMethod.GET)
    public ResponseEntity<ReservationOrderTrackResponse> findReservationOrder(
            @RequestBody ReservationOrderTrackQuery query,
            @RequestHeader("X-header-User-Id") UUID tokenUserId
            ) {
        query.setUserId(tokenUserId);
        ReservationOrderTrackResponse response = tradingApplicationService
                .findReservationOrderTrackByOrderIdAndUserId(query);
        return ResponseEntity.ok(response);
    }

    @RequestMapping(path = "/track/orderbook",method = RequestMethod.GET)
    public ResponseEntity<OrderBookTrackResponse> findOrderBook(
            @RequestBody OrderBookTrackQuery orderBookTrackQuery) {
        OrderBookTrackResponse orderBook = tradingApplicationService.findOrderBook(orderBookTrackQuery);
        return ResponseEntity.ok(orderBook);
    }



}
