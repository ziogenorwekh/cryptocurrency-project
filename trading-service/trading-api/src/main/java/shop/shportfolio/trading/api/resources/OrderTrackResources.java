package shop.shportfolio.trading.api.resources;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.shportfolio.trading.application.command.track.request.LimitOrderTrackQuery;
import shop.shportfolio.trading.application.command.track.request.OrderTrackQuery;
import shop.shportfolio.trading.application.command.track.request.ReservationOrderTrackQuery;
import shop.shportfolio.trading.application.command.track.response.LimitOrderTrackResponse;
import shop.shportfolio.trading.application.command.track.response.OrderTrackResponse;
import shop.shportfolio.trading.application.command.track.response.ReservationOrderTrackResponse;
import shop.shportfolio.trading.application.ports.input.TradingApplicationService;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api")
@Tag(name = "Order Tracking API", description = "주문 추적 관련 API")
public class OrderTrackResources {

    private final TradingApplicationService tradingApplicationService;

    @Autowired
    public OrderTrackResources(TradingApplicationService tradingApplicationService) {
        this.tradingApplicationService = tradingApplicationService;
    }

    @Operation(
            summary = "지정가 주문 추적",
            description = "주문 ID와 사용자 ID로 지정가 주문 추적 정보를 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                    @ApiResponse(responseCode = "404", description = "주문을 찾을 수 없음"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            }
    )
    @RequestMapping(path = "/track/limit/{orderId}", method = RequestMethod.GET)
    public ResponseEntity<LimitOrderTrackResponse> findLimitOrder(
            @PathVariable String orderId,
            @RequestHeader("X-header-User-Id") UUID tokenUserId) {
        LimitOrderTrackQuery limitOrderTrackQuery = new LimitOrderTrackQuery();
        limitOrderTrackQuery.setUserId(tokenUserId);
        limitOrderTrackQuery.setOrderId(orderId);
        LimitOrderTrackResponse response = tradingApplicationService
                .findLimitOrderTrackByOrderIdAndUserId(limitOrderTrackQuery);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "예약 주문 추적",
            description = "주문 ID와 사용자 ID로 예약 주문 추적 정보를 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                    @ApiResponse(responseCode = "404", description = "주문을 찾을 수 없음"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            }
    )
    @RequestMapping(path = "/track/reservation/{orderId}", method = RequestMethod.GET)
    public ResponseEntity<ReservationOrderTrackResponse> findReservationOrder(
            @PathVariable String orderId,
            @RequestHeader("X-header-User-Id") UUID tokenUserId) {
        ReservationOrderTrackQuery query = new ReservationOrderTrackQuery();
        query.setUserId(tokenUserId);
        query.setOrderId(orderId);
        ReservationOrderTrackResponse response = tradingApplicationService
                .findReservationOrderTrackByOrderIdAndUserId(query);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "마켓 기준 전체 주문 조회",
            description = "특정 마켓의 모든 주문(지정가, 시장가, 예약)을 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                    @ApiResponse(responseCode = "404", description = "주문을 찾을 수 없음"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            }
    )
    @RequestMapping(path = "/track/orders/{marketId}", method = RequestMethod.GET)
    public ResponseEntity<List<OrderTrackResponse>> findOrdersByMarketId(
            @PathVariable String marketId,
            @RequestHeader("X-header-User-Id") UUID tokenUserId
    ) {
        OrderTrackQuery query = new OrderTrackQuery();
        query.setMarketId(marketId);
        query.setUserId(tokenUserId);
        List<OrderTrackResponse> responseList = tradingApplicationService.findAllOrderByMarketId(query);
        return ResponseEntity.ok(responseList);
    }
}
