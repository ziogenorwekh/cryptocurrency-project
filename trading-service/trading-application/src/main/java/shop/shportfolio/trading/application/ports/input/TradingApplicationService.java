package shop.shportfolio.trading.application.ports.input;

import jakarta.validation.Valid;
import shop.shportfolio.trading.application.command.create.CreateLimitOrderCommand;
import shop.shportfolio.trading.application.command.create.CreateLimitOrderResponse;
import shop.shportfolio.trading.application.command.create.CreateMarketOrderCommand;

public interface TradingApplicationService {

    CreateLimitOrderResponse createLimitOrder(@Valid CreateLimitOrderCommand createLimitOrderCommand);

    void createMarketOrder(@Valid CreateMarketOrderCommand createMarketOrderCommand);
}
