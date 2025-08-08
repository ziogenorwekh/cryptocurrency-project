package shop.shportfolio.trading.api.resources;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.shportfolio.trading.application.command.create.*;
import shop.shportfolio.trading.application.ports.input.TradingApplicationService;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api")
public class OrderCreateResources {


    private final TradingApplicationService tradingApplicationService;

    @Autowired
    public OrderCreateResources(TradingApplicationService tradingApplicationService) {
        this.tradingApplicationService = tradingApplicationService;
    }


    @RequestMapping(path = "/orders/limit", method = RequestMethod.POST)
    public ResponseEntity<CreateLimitOrderResponse> createLimitOrder(
            @RequestBody CreateLimitOrderCommand createLimitOrderCommand,
            @RequestHeader("X-header-User-Id") UUID tokenUserId) {
        createLimitOrderCommand.setUserId(tokenUserId);
        CreateLimitOrderResponse response = tradingApplicationService
                .createLimitOrder(createLimitOrderCommand);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @RequestMapping(path = "/orders/reservation",method = RequestMethod.POST)
    public ResponseEntity<CreateReservationResponse> createReservation(
            @RequestBody CreateReservationOrderCommand command,
            @RequestHeader("X-header-User-Id") UUID tokenUserId
            ) {
        command.setUserId(tokenUserId);
        CreateReservationResponse response = tradingApplicationService.createReservationOrder(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @RequestMapping(path = "/orders/market",method = RequestMethod.POST)
    public ResponseEntity<Void> createMarketOrder(
            @RequestBody CreateMarketOrderCommand command,
            @RequestHeader("X-header-User-Id") UUID tokenUserId
            ) {
        command.setUserId(tokenUserId);
        tradingApplicationService.createMarketOrder(command);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }



}
