package shop.shportfolio.trading.api.resources;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.shportfolio.trading.application.command.create.*;
import shop.shportfolio.trading.application.ports.input.TradingApplicationService;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api")
@Tag(name = "Order Create API", description = "주문 생성 관련 API")
public class OrderCreateResources {

    private final TradingApplicationService tradingApplicationService;

    @Autowired
    public OrderCreateResources(TradingApplicationService tradingApplicationService) {
        this.tradingApplicationService = tradingApplicationService;
    }

    @Operation(
            summary = "지정가 주문 생성",
            description = "특정 사용자의 지정가 주문을 생성합니다.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "지정가 주문 생성 성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            }
    )
    @PostMapping("/orders/limit")
    public ResponseEntity<CreateLimitOrderResponse> createLimitOrder(
            @RequestBody CreateLimitOrderCommand command,
            @RequestHeader("X-header-User-Id") UUID tokenUserId
    ) {
        command.setUserId(tokenUserId);
        CreateLimitOrderResponse response = tradingApplicationService.createLimitOrder(command);
        log.info("User {} created limit order {}", tokenUserId, response.getOrderId());
        return ResponseEntity.status(201).body(response);
    }

    @Operation(
            summary = "예약 주문 생성",
            description = "특정 사용자의 예약 주문을 생성합니다.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "예약 주문 생성 성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            }
    )
    @PostMapping("/orders/reservation")
    public ResponseEntity<CreateReservationResponse> createReservation(
            @RequestBody CreateReservationOrderCommand command,
            @RequestHeader("X-header-User-Id") UUID tokenUserId
    ) {
        command.setUserId(tokenUserId);
        CreateReservationResponse response = tradingApplicationService.createReservationOrder(command);
        log.info("User {} created reservation order {}", tokenUserId, response.getOrderId());
        return ResponseEntity.status(201).body(response);
    }

    @Operation(
            summary = "시장가 주문 생성",
            description = "특정 사용자의 시장가 주문을 생성합니다.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "시장가 주문 생성 성공"),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            }
    )
    @PostMapping("/orders/market")
    public ResponseEntity<Void> createMarketOrder(
            @RequestBody CreateMarketOrderCommand command,
            @RequestHeader("X-User-Id") UUID tokenUserId
    ) {
        command.setUserId(tokenUserId);
        tradingApplicationService.createMarketOrder(command);
        log.info("User {} created market order", tokenUserId);
        return ResponseEntity.status(201).build();
    }
}
