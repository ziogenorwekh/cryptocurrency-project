package shop.shportfolio.trading.application.ports.input;

import shop.shportfolio.trading.application.command.create.CreateLimitOrderCommand;
import shop.shportfolio.trading.domain.entity.LimitOrder;

public interface TradingCreateOrderUseCase {


    LimitOrder createLimitOrder(CreateLimitOrderCommand command);
}
