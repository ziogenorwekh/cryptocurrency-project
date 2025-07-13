package shop.shportfolio.trading.application.facade;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.trading.application.command.create.CreateLimitOrderCommand;
import shop.shportfolio.trading.application.command.create.CreateMarketOrderCommand;
import shop.shportfolio.trading.application.command.create.CreateReservationOrderCommand;
import shop.shportfolio.trading.application.handler.create.TradingCreateHandler;
import shop.shportfolio.trading.application.handler.matching.strategy.OrderMatchingStrategy;
import shop.shportfolio.trading.application.ports.input.OrderValidator;
import shop.shportfolio.trading.application.ports.input.TradingCreateOrderUseCase;
import shop.shportfolio.trading.domain.entity.*;

import java.util.List;

@Slf4j
@Component
public class TradingCreateOrderFacade implements TradingCreateOrderUseCase {


    private final TradingCreateHandler tradingCreateHandler;
    private final List<OrderValidator<? extends Order>> orderValidators;
    @Autowired
    public TradingCreateOrderFacade(TradingCreateHandler tradingCreateHandler,
                                    List<OrderValidator<? extends Order>> orderValidators){
        this.tradingCreateHandler = tradingCreateHandler;
        this.orderValidators = orderValidators;
    }

    @Override
    public LimitOrder createLimitOrder(CreateLimitOrderCommand command) {
        LimitOrder limitOrder = tradingCreateHandler.createLimitOrder(command);
        execute(limitOrder);
        return limitOrder;
    }

    @Override
    public MarketOrder createMarketOrder(CreateMarketOrderCommand command) {
        MarketOrder marketOrder = tradingCreateHandler.createMarketOrder(command);
        execute(marketOrder);
        return marketOrder;
    }

    @Override
    public ReservationOrder createReservationOrder(CreateReservationOrderCommand command) {
        ReservationOrder reservationOrder = tradingCreateHandler.createReservationOrder(command);
        execute(reservationOrder);
        return reservationOrder;
    }


    @SuppressWarnings("unchecked")
    private <T extends Order> OrderValidator<T> findStrategy(T order) {
        return (OrderValidator<T>) orderValidators.stream()
                .filter(s -> s.supports(order))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("No strategy for order type: " + order.getOrderType()));
    }

    private <T extends Order> void execute(T order) {
        OrderValidator<T> strategy = findStrategy(order);
        boolean valid;
        if (order.isBuyOrder()) {
            valid = strategy.validateBuyOrder(order);
        } else {
            valid = strategy.validateSellOrder(order);
        }
        if (!valid) {
            // 여기 수정해야 됌
            throw new IllegalArgumentException(
                    String.format("Invalid %s order: %s", order.getOrderType(), order));
        }
    }
}
