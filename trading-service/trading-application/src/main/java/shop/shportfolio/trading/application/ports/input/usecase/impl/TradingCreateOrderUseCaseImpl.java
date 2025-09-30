package shop.shportfolio.trading.application.ports.input.usecase.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.CannotAcquireLockException;
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
    public LimitOrderCreatedEvent createLimitOrder(CreateLimitOrderCommand command) {
        UserId userId = new UserId(command.getUserId());
        OrderSide orderSide = OrderSide.of(command.getOrderSide());
        OrderPrice orderPrice = new OrderPrice(command.getOrderPrice());
        Quantity quantity = new Quantity(command.getQuantity());

        if (orderSide.isSell()) {
            userBalanceHandler.validateSellOrder(command.getUserId(), command.getMarketId(), command.getQuantity());
        }

        // Buy 주문 초기 잔고 검증 (락 없음: Lock A 회피)
        if (orderSide.isBuy()) {
            FeeAmount feeAmount = calculateFeeAmount(userId, orderSide, orderPrice, quantity);
            userBalanceHandler.validateLimitAndReservationOrder(userId, orderPrice, quantity, feeAmount);
        }

        // Order 생성 & 저장 (Lock B 획득/업데이트!)
        OrderCreationContext<LimitOrderCreatedEvent> context = tradingCreateHandler.createLimitOrder(command);
        LimitOrder order = context.getDomainEvent().getDomainType();
        execute(order);

        if (order.isBuyOrder()) {
            // 잔고 잠금 및 락 획득 (Lock A 획득!) -> B -> A 순서 강제
            FeeAmount feeAmount = calculateFeeAmount(order.getUserId(), order.getOrderSide(),
                    order.getOrderPrice(), order.getQuantity());

            userBalanceHandler.lockUserBalanceForOrder(
                    order.getUserId(),
                    order.getId(),
                    order.getOrderPrice(),
                    feeAmount,
                    order.getQuantity()
            );
        }
        return context.getDomainEvent();
    }



    @Override
    public MarketOrderCreatedEvent createMarketOrder(CreateMarketOrderCommand command) {
        UserId userId = new UserId(command.getUserId());
        OrderSide orderSide = OrderSide.of(command.getOrderSide());
        OrderPrice orderPrice = new OrderPrice(command.getOrderPrice());

        if (orderSide.isSell()) {
            userBalanceHandler.validateSellOrder(command.getUserId(),
                    command.getMarketId(), command.getQuantity());
        }

        if (orderSide.isBuy()) {
            FeeAmount feeAmount = calculateFeeAmount(userId, orderSide, orderPrice);
            userBalanceHandler.validateMarketOrder(userId, orderPrice, feeAmount);
        }

        OrderCreationContext<MarketOrderCreatedEvent> context = tradingCreateHandler.createMarketOrder(command);
        MarketOrder order = context.getDomainEvent().getDomainType();
        execute(order); // Order 유효성 검증

        if (order.isBuyOrder()) {
            FeeAmount feeAmount = calculateFeeAmount(userId, orderSide, orderPrice);
            userBalanceHandler.lockUserBalanceForOrder(
                    order.getUserId(),
                    order.getId(),
                    orderPrice,
                    feeAmount,
                    null
            );
        }
        return context.getDomainEvent();
    }

    @Override
    public ReservationOrderCreatedEvent createReservationOrder(CreateReservationOrderCommand command) {
        UserId userId = new UserId(command.getUserId());
        OrderSide orderSide = OrderSide.of(command.getOrderSide());
        OrderPrice targetPrice = new OrderPrice(command.getTargetPrice());
        Quantity quantity = new Quantity(command.getQuantity());

        if (orderSide.isSell()) {
            userBalanceHandler.validateSellOrder(command.getUserId(), command.getMarketId(), command.getQuantity());
        }

        // Buy 주문 초기 잔고 검증 (락 없음: Lock A 회피)
        if (orderSide.isBuy()) {
            FeeAmount feeAmount = calculateFeeAmount(userId, orderSide, targetPrice, quantity);
            userBalanceHandler.validateLimitAndReservationOrder(userId, targetPrice, quantity, feeAmount);
        }

        // Order 생성 & 저장 (Lock B 획득/업데이트!)
        OrderCreationContext<ReservationOrderCreatedEvent> context = tradingCreateHandler.createReservationOrder(command);
        ReservationOrder order = context.getDomainEvent().getDomainType();
        execute(order);

        if (order.isBuyOrder()) {
            FeeAmount feeAmount = calculateFeeAmount(order.getUserId(), order.getOrderSide(),
                    order.getTriggerCondition().getTargetPrice(), order.getQuantity());

            userBalanceHandler.lockUserBalanceForOrder(
                    order.getUserId(),
                    order.getId(),
                    order.getTriggerCondition().getTargetPrice(),
                    feeAmount,
                    order.getQuantity()
            );
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
