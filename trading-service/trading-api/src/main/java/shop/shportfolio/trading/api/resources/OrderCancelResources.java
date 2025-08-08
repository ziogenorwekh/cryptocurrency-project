package shop.shportfolio.trading.api.resources;

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
public class OrderCancelResources {

    private final TradingApplicationService tradingApplicationService;

    @Autowired
    public OrderCancelResources(TradingApplicationService tradingApplicationService) {
        this.tradingApplicationService = tradingApplicationService;
    }

    @RequestMapping(path = "/orders/limit/cancel",method = RequestMethod.POST)
    public ResponseEntity<CancelOrderResponse> cancelLimitOrder(@RequestBody CancelLimitOrderCommand cancelLimitOrderCommand,
                                                 @RequestHeader("X-header-User-Id") UUID tokenUserId) {
        cancelLimitOrderCommand.setUserId(tokenUserId);
        CancelOrderResponse response = tradingApplicationService.cancelLimitOrder(cancelLimitOrderCommand);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

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
