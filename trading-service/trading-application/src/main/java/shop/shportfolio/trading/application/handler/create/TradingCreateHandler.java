package shop.shportfolio.trading.application.handler.create;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.trading.application.command.create.CreateLimitOrderCommand;
import shop.shportfolio.trading.application.command.create.CreateMarketOrderCommand;
import shop.shportfolio.trading.application.ports.output.repository.TradingRepositoryAdapter;
import shop.shportfolio.trading.domain.TradingDomainService;
import shop.shportfolio.trading.domain.entity.*;
import shop.shportfolio.trading.domain.event.TradingRecordedEvent;
import shop.shportfolio.trading.domain.valueobject.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.BiFunction;

@Slf4j
@Component
public class TradingCreateHandler {

    private final TradingRepositoryAdapter tradingRepositoryAdapter;
    private final TradingDomainService tradingDomainService;


    @Autowired
    public TradingCreateHandler(TradingRepositoryAdapter tradingRepositoryAdapter,
                                TradingDomainService tradingDomainService) {
        this.tradingRepositoryAdapter = tradingRepositoryAdapter;
        this.tradingDomainService = tradingDomainService;
    }

    public LimitOrder createLimitOrder(CreateLimitOrderCommand command) {
        LimitOrder limitOrder = tradingDomainService.createLimitOrder(new UserId(command.getUserId()), new MarketId(command.getMarketId()),
                OrderSide.of(command.getOrderSide()), new Quantity(command.getQuantity()), new OrderPrice(command.getPrice())
                , OrderType.valueOf(command.getOrderType()));
        return tradingRepositoryAdapter.saveLimitOrder(limitOrder);
    }

    public MarketOrder createMarketOrder(CreateMarketOrderCommand command) {
        MarketOrder marketOrder = tradingDomainService.createMarketOrder(new UserId(command.getUserId()),
                new MarketId(command.getMarketId()),
                OrderSide.of(command.getOrderSide()), new Quantity(command.getQuantity()),
                OrderType.valueOf(command.getOrderType()));
        return tradingRepositoryAdapter.saveMarketOrder(marketOrder);
    }

}
