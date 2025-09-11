package shop.shportfolio.trading.api.resources;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.shportfolio.trading.application.command.update.CancelLimitOrderCommand;
import shop.shportfolio.trading.application.command.update.CancelOrderResponse;
import shop.shportfolio.trading.application.command.update.CancelReservationOrderCommand;
import shop.shportfolio.trading.application.ports.input.TradingApplicationService;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api")
@Tag(name = "Order Cancel API", description = "주문 취소 관련 API")
public class OrderCancelResources {

    private final TradingApplicationService tradingApplicationService;

    @Autowired
    public OrderCancelResources(TradingApplicationService tradingApplicationService) {
        this.tradingApplicationService = tradingApplicationService;
    }

    @Operation(
            summary = "지정가 주문 취소",
            description = "특정 사용자의 지정가 주문을 취소합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "주문 취소 성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                    @ApiResponse(responseCode = "404", description = "해당 주문을 찾을 수 없음"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            }
    )
    @RequestMapping(path = "/orders/limit/cancel",method = RequestMethod.POST)
    public ResponseEntity<CancelOrderResponse> cancelLimitOrder(@RequestBody CancelLimitOrderCommand cancelLimitOrderCommand,
                                                 @RequestHeader("X-header-User-Id") UUID tokenUserId) {
        cancelLimitOrderCommand.setUserId(tokenUserId);
        CancelOrderResponse response = tradingApplicationService.cancelLimitOrder(cancelLimitOrderCommand);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(
            summary = "예약 주문 취소",
            description = "특정 사용자의 예약 주문을 취소합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "주문 취소 성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                    @ApiResponse(responseCode = "404", description = "해당 주문을 찾을 수 없음"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            }
    )
    @RequestMapping(path = "/orders/reservation/cancel",method = RequestMethod.POST)
    public ResponseEntity<CancelOrderResponse> cancelReservationOrder(
            @RequestBody CancelReservationOrderCommand command,
            @RequestHeader("X-header-User-Id")  UUID tokenUserId
            ) {
        command.setUserId(tokenUserId);
        CancelOrderResponse response = tradingApplicationService.cancelReservationOrder(command);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
