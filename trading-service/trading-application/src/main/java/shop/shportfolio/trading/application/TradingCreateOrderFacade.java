package shop.shportfolio.trading.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.trading.application.command.create.CreateLimitOrderCommand;
import shop.shportfolio.trading.application.command.create.CreateMarketOrderCommand;
import shop.shportfolio.trading.application.handler.TradingCreateHandler;
import shop.shportfolio.trading.application.ports.input.TradingCreateOrderUseCase;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.MarketOrder;

import java.math.BigDecimal;

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

    @Override
    public MarketOrder createMarketOrder(CreateMarketOrderCommand command, BigDecimal nowPrice) {
        return tradingCreateHandler.createMarketOrder(command, nowPrice);
    }
}
