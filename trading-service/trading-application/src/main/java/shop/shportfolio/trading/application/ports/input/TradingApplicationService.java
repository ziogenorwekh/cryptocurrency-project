package shop.shportfolio.trading.application.ports.input;

import jakarta.validation.Valid;
import shop.shportfolio.trading.application.command.create.CreateLimitOrderCommand;
import shop.shportfolio.trading.application.command.create.CreateLimitOrderResponse;
import shop.shportfolio.trading.application.command.create.CreateMarketOrderCommand;
import shop.shportfolio.trading.application.command.create.CreateMarketOrderResponse;

import java.math.BigDecimal;

public interface TradingApplicationService {


    CreateLimitOrderResponse createLimitOrder(@Valid CreateLimitOrderCommand createLimitOrderCommand);

    CreateMarketOrderResponse createMarketOrder(@Valid CreateMarketOrderCommand createMarketOrderCommand, BigDecimal nowPrice);
}
