package shop.shportfolio.trading.application.ports.input.usecase.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.trading.application.command.create.*;
import shop.shportfolio.trading.application.dto.context.OrderCreationContext;
import shop.shportfolio.trading.application.handler.*;
import shop.shportfolio.trading.application.handler.create.TradingCreateHandler;
import shop.shportfolio.trading.application.policy.FeePolicy;
import shop.shportfolio.trading.application.ports.input.usecase.TradingCreateOrderUseCase;
import shop.shportfolio.trading.application.validator.OrderValidator;
import shop.shportfolio.trading.domain.entity.*;
import shop.shportfolio.trading.domain.entity.userbalance.UserBalance;
import shop.shportfolio.common.domain.valueobject.Money;
import shop.shportfolio.trading.domain.event.LimitOrderCreatedEvent;
import shop.shportfolio.trading.domain.event.MarketOrderCreatedEvent;
import shop.shportfolio.trading.domain.event.ReservationOrderCreatedEvent;
import shop.shportfolio.trading.domain.valueobject.OrderSide;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Component
public class TradingCreateOrderUseCaseImpl implements TradingCreateOrderUseCase {

    private final TradingCreateHandler tradingCreateHandler;
    private final List<OrderValidator<? extends Order>> orderValidators;
    private final UserBalanceHandler userBalanceHandler;
    private final CouponInfoHandler couponInfoHandler;
    private final FeePolicy feePolicy;

    @Autowired
    public TradingCreateOrderUseCaseImpl(TradingCreateHandler tradingCreateHandler,
                                         List<OrderValidator<? extends Order>> orderValidators,
                                         UserBalanceHandler userBalanceHandler,
                                         CouponInfoHandler couponInfoHandler,
                                         FeePolicy feePolicy) {
        this.tradingCreateHandler = tradingCreateHandler;
        this.orderValidators = orderValidators;
        this.userBalanceHandler = userBalanceHandler;
        this.couponInfoHandler = couponInfoHandler;
        this.feePolicy = feePolicy;
    }

    @Override
    @Transactional
    public LimitOrderCreatedEvent createLimitOrder(CreateLimitOrderCommand command) {
        if ("SELL".equals(command.getOrderSide())) {
            userBalanceHandler.validateSellOrder(command.getUserId(), command.getMarketId(), command.getQuantity());
        }
        OrderCreationContext<LimitOrderCreatedEvent> context = tradingCreateHandler.createLimitOrder(command);
        LimitOrder order = context.getDomainEvent().getDomainType();
        execute(order);

        if (order.isBuyOrder()) {
            FeeAmount feeAmount = calculateFeeAmount(order.getUserId(), order.getOrderSide(),
                    order.getOrderPrice(), order.getQuantity());
            UserBalance userBalance = userBalanceHandler.validateLimitAndReservationOrder(
                    order.getUserId(), order.getOrderPrice(), order.getQuantity(), feeAmount);

            Money totalAmount = calculateTotalAmount(order.getOrderPrice(), order.getQuantity(), feeAmount);
            userBalanceHandler.saveUserBalanceForLockBalance(userBalance, order.getId(), totalAmount);
        }

//        limitOrderCreatedPublisher.publish(context.getDomainEvent());
        return context.getDomainEvent();
    }

    @Override
    @Transactional
    public MarketOrderCreatedEvent createMarketOrder(CreateMarketOrderCommand command) {
        if ("SELL".equals(command.getOrderSide())) {
            userBalanceHandler.validateSellOrder(command.getUserId(), command.getMarketId(), command.getQuantity());
        }
        OrderCreationContext<MarketOrderCreatedEvent> context = tradingCreateHandler.createMarketOrder(command);
        MarketOrder order = context.getDomainEvent().getDomainType();
        execute(order);

        if (order.isBuyOrder()) {
            FeeAmount feeAmount = calculateFeeAmount(order.getUserId(), order.getOrderSide(), order.getOrderPrice());
            UserBalance userBalance = userBalanceHandler.validateMarketOrder(
                    order.getUserId(), order.getOrderPrice(), feeAmount);
            Money totalAmount = calculateTotalAmount(order.getOrderPrice(), null, feeAmount);
            userBalanceHandler.saveUserBalanceForLockBalance(userBalance, order.getId(), totalAmount);
        }
//        marketOrderCreatedPublisher.publish(context.getDomainEvent());
        return context.getDomainEvent();
    }

    @Override
    @Transactional
    public ReservationOrderCreatedEvent createReservationOrder(CreateReservationOrderCommand command) {
        if ("SELL".equals(command.getOrderSide())) {
            userBalanceHandler.validateSellOrder(command.getUserId(), command.getMarketId(), command.getQuantity());
        }

        OrderCreationContext<ReservationOrderCreatedEvent> context = tradingCreateHandler.createReservationOrder(command);
        ReservationOrder order = context.getDomainEvent().getDomainType();
        execute(order);

        if (order.isBuyOrder()) {
            FeeAmount feeAmount = calculateFeeAmount(order.getUserId(), order.getOrderSide(),
                    order.getTriggerCondition().getTargetPrice(), order.getQuantity());
            UserBalance userBalance = userBalanceHandler.validateLimitAndReservationOrder(
                    order.getUserId(), order.getTriggerCondition().getTargetPrice(),
                    order.getQuantity(), feeAmount);
            Money totalAmount = calculateTotalAmount(order.getTriggerCondition().getTargetPrice(),
                    order.getQuantity(), feeAmount);
            userBalanceHandler.saveUserBalanceForLockBalance(userBalance, order.getId(), totalAmount);
        }
//        reservationOrderCreatedPublisher.publish(context.getDomainEvent());
        return context.getDomainEvent();
    }

    @SuppressWarnings("unchecked")
    private <T extends Order> OrderValidator<T> findStrategy(T order) {
        return (OrderValidator<T>) orderValidators.stream()
                .filter(v -> v.supports(order))
                .findFirst()
                .orElseThrow(() -> new UnsupportedOperationException(
                        "No validator found for order type: " + order.getOrderType()));
    }

    private <T extends Order> void execute(T order) {
        OrderValidator<T> validator = findStrategy(order);
        if (order.isBuyOrder()) {
            validator.validateBuyOrder(order);
        } else {
            validator.validateSellOrder(order);
        }
    }

    private FeeAmount calculateFeeAmount(UserId userId, OrderSide side, OrderPrice price) {
        FeeRate feeRate = determineEffectiveFeeRate(userId, side);
        return feeRate.calculateFeeTotalAmount(price);
    }

    private FeeAmount calculateFeeAmount(UserId userId, OrderSide side, OrderPrice price, Quantity quantity) {
        FeeRate feeRate = determineEffectiveFeeRate(userId, side);
        return feeRate.calculateFeeAmount(price, quantity);
    }

    private FeeRate determineEffectiveFeeRate(UserId userId, OrderSide side) {
        FeeRate baseFeeRate = feePolicy.calculateDefualtFeeRate(side);

        return couponInfoHandler.trackCouponInfo(userId)
                .filter(c -> !c.getUsageExpiryDate().isExpired())
                .map(c -> baseFeeRate.applyDiscount(c.getFeeDiscount().getRatio()
                        .divide(BigDecimal.valueOf(100))))
                .orElse(baseFeeRate);
    }

    private Money calculateTotalAmount(OrderPrice price, Quantity quantity, FeeAmount feeAmount) {
        BigDecimal priceAmount = price.getValue();
        BigDecimal qty = quantity != null ? quantity.getValue() : BigDecimal.ONE;
        BigDecimal total = priceAmount.multiply(qty).add(feeAmount.getValue());
        return Money.of(total);
    }
}
