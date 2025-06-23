package shop.shportfolio.trading.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.trading.application.command.create.CreateLimitOrderCommand;
import shop.shportfolio.trading.application.handler.TradingCreateHandler;
import shop.shportfolio.trading.application.ports.input.TradingCreateOrderUseCase;
import shop.shportfolio.trading.domain.entity.LimitOrder;

@Component
public class TradingCreateOrderFacade implements TradingCreateOrderUseCase {


    private final TradingCreateHandler tradingCreateHandler;

    @Autowired
    public TradingCreateOrderFacade(TradingCreateHandler tradingCreateHandler) {
        this.tradingCreateHandler = tradingCreateHandler;
    }

    @Override
    public LimitOrder createLimitOrder(CreateLimitOrderCommand command) {
        return tradingCreateHandler.createLimitOrder(command);
    }
}
