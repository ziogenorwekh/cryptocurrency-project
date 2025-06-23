package shop.shportfolio.trading.application.ports.input;

import shop.shportfolio.trading.application.command.create.CreateLimitOrderCommand;
import shop.shportfolio.trading.application.command.create.CreateMarketOrderCommand;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.MarketOrder;

import java.math.BigDecimal;

public interface TradingCreateOrderUseCase {


    LimitOrder createLimitOrder(CreateLimitOrderCommand command);

    MarketOrder createMarketOrder(CreateMarketOrderCommand command, BigDecimal nowPrice);
}
