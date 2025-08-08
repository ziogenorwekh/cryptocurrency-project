package shop.shportfolio.trading.api.resources;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.shportfolio.trading.application.ports.input.TradingApplicationService;

@Slf4j
@RestController
@RequestMapping("/api")
public class LimitOrderResources {


    private final TradingApplicationService tradingApplicationService;

    public LimitOrderResources(TradingApplicationService tradingApplicationService) {
        this.tradingApplicationService = tradingApplicationService;
    }


}
