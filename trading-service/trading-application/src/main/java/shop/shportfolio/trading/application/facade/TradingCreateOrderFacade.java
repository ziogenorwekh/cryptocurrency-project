package shop.shportfolio.trading.application.facade;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.trading.application.command.create.CreateLimitOrderCommand;
import shop.shportfolio.trading.application.command.create.CreateMarketOrderCommand;
import shop.shportfolio.trading.application.command.create.CreateReservationOrderCommand;
import shop.shportfolio.trading.application.dto.context.OrderCreationContext;
import shop.shportfolio.trading.application.handler.create.TradingCreateHandler;
import shop.shportfolio.trading.application.ports.input.OrderValidator;
import shop.shportfolio.trading.application.ports.input.TradingCreateOrderUseCase;
import shop.shportfolio.trading.domain.entity.*;
import shop.shportfolio.trading.domain.entity.orderbook.MarketItem;

import java.util.List;

@Slf4j
@Component
public class TradingCreateOrderFacade implements TradingCreateOrderUseCase {


    private final TradingCreateHandler tradingCreateHandler;
    private final List<OrderValidator<? extends Order>> orderValidators;

    @Autowired
    public TradingCreateOrderFacade(TradingCreateHandler tradingCreateHandler,
                                    List<OrderValidator<? extends Order>> orderValidators) {
        this.tradingCreateHandler = tradingCreateHandler;
        this.orderValidators = orderValidators;
    }

    @Override
    public LimitOrder createLimitOrder(CreateLimitOrderCommand command) {
        OrderCreationContext<LimitOrder> orderOrderCreationContext = tradingCreateHandler.createLimitOrder(command);
        execute(orderOrderCreationContext.getOrder(), orderOrderCreationContext.getMarketItem());
        return orderOrderCreationContext.getOrder();
    }

    @Override
    public MarketOrder createMarketOrder(CreateMarketOrderCommand command) {
        OrderCreationContext<MarketOrder> orderOrderCreationContext = tradingCreateHandler.createMarketOrder(command);
        execute(orderOrderCreationContext.getOrder(), orderOrderCreationContext.getMarketItem());
        return orderOrderCreationContext.getOrder();
    }

    @Override
    public ReservationOrder createReservationOrder(CreateReservationOrderCommand command) {
        OrderCreationContext<ReservationOrder> orderOrderCreationContext = tradingCreateHandler.createReservationOrder(command);
        execute(orderOrderCreationContext.getOrder(), orderOrderCreationContext.getMarketItem());
        return orderOrderCreationContext.getOrder();
    }


    @SuppressWarnings("unchecked")
    private <T extends Order> OrderValidator<T> findStrategy(T order) {
        return (OrderValidator<T>) orderValidators.stream()
                .filter(s -> s.supports(order))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("No strategy for order type: " + order.getOrderType()));
    }

    private <T extends Order> void execute(T order, MarketItem marketItem) {
        OrderValidator<T> strategy = findStrategy(order);
        boolean valid;
        if (order.isBuyOrder()) {
            strategy.validateBuyOrder(order, marketItem);
        } else {
            strategy.validateSellOrder(order, marketItem);
        }

    }
}
