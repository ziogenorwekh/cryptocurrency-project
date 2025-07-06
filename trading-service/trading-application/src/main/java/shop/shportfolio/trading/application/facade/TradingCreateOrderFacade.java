package shop.shportfolio.trading.application.facade;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.trading.application.command.create.CreateLimitOrderCommand;
import shop.shportfolio.trading.application.command.create.CreateMarketOrderCommand;
import shop.shportfolio.trading.application.handler.create.TradingCreateHandler;
import shop.shportfolio.trading.application.ports.input.TradingCreateOrderUseCase;
import shop.shportfolio.trading.domain.entity.*;

@Slf4j
@Component
public class TradingCreateOrderFacade implements TradingCreateOrderUseCase {


    private final TradingCreateHandler tradingCreateHandler;
    @Autowired
    public TradingCreateOrderFacade(TradingCreateHandler tradingCreateHandler){
        this.tradingCreateHandler = tradingCreateHandler;
    }

    @Override
    public LimitOrder createLimitOrder(CreateLimitOrderCommand command) {
        return tradingCreateHandler.createLimitOrder(command);
    }

    @Override
    public MarketOrder createMarketOrder(CreateMarketOrderCommand command) {
        return tradingCreateHandler.createMarketOrder(command);
    }
}
