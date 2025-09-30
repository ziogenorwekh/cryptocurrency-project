package shop.shportfolio.trading.application.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.trading.application.dto.context.TradeMatchingContext;
import shop.shportfolio.trading.application.dto.trade.PredicatedTradeKafkaResponse;
import shop.shportfolio.trading.application.orderbook.matching.FeeRateResolver;
import shop.shportfolio.trading.application.ports.output.repository.TradingOrderRepositoryPort;
import shop.shportfolio.trading.application.ports.output.repository.TradingTradeRecordRepositoryPort;
import shop.shportfolio.trading.domain.OrderDomainService;
import shop.shportfolio.trading.domain.TradeDomainService;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.MarketOrder;
import shop.shportfolio.trading.domain.entity.ReservationOrder;
import shop.shportfolio.trading.domain.entity.userbalance.UserBalance;
import shop.shportfolio.trading.domain.event.TradeCreatedEvent;
import shop.shportfolio.trading.domain.event.UserBalanceUpdatedEvent;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class PredicatedTradeTransactionHandler {

    private final UserBalanceHandler userBalanceHandler;
    private final TradingOrderRepositoryPort tradingOrderRepositoryPort;
    private final FeeRateResolver feeRateResolver;
    private final TradeDomainService tradeDomainService;
    private final TradingTradeRecordRepositoryPort tradingTradeRecordRepositoryPort;
    private final OrderDomainService orderDomainService;

    @Autowired
    public PredicatedTradeTransactionHandler(UserBalanceHandler userBalanceHandler,
                                             TradingOrderRepositoryPort tradingOrderRepositoryPort,
                                             FeeRateResolver feeRateResolver,
                                             TradeDomainService tradeDomainService,
                                             TradingTradeRecordRepositoryPort tradingTradeRecordRepositoryPort,
                                             OrderDomainService orderDomainService) {
        this.userBalanceHandler = userBalanceHandler;
        this.tradingOrderRepositoryPort = tradingOrderRepositoryPort;
        this.feeRateResolver = feeRateResolver;
        this.tradeDomainService = tradeDomainService;
        this.tradingTradeRecordRepositoryPort = tradingTradeRecordRepositoryPort;
        this.orderDomainService = orderDomainService;
    }

    @Transactional
    public Optional<TradeMatchingContext> processTradeEvent(PredicatedTradeKafkaResponse response, boolean isBuySide) {

        String orderId = isBuySide ? response.getBuyOrderId() : response.getSellOrderId();
        var orderType = isBuySide ? response.getBuyOrderType() : response.getSellOrderType();

        if (orderId == null || orderId.contains("anonymous")) {
            return Optional.empty();
        }

        return switch (orderType) {
            case LIMIT -> tradingOrderRepositoryPort.findLimitOrderByOrderId(orderId)
                    .map(order -> {
                        if (order.isFilled() || order.isCancel()) {
                            log.warn("Duplicate/Invalid trade event for already finalized limit order: {}", order.getId().getValue());
                            return null;
                        }
                        return executeLimitOrderTransactionInternal(order, response, isBuySide);
//                        var balanceEvent = userBalanceHandler.makeUserBalanceUpdatedEvent(order.getUserId());
                    }).flatMap(Optional::ofNullable);

            case MARKET -> tradingOrderRepositoryPort.findMarketOrderByOrderId(orderId)
                    .map(order -> {
                        if (order.isFilled() || order.isCancel()) {
                            log.warn("Duplicate/Invalid trade event for already finalized market order: {}", order.getId().getValue());
                            return null;
                        }
                        return executeMarketTradeTransactionInternal(order, response, isBuySide);
//                        var balanceEvent = userBalanceHandler.makeUserBalanceUpdatedEvent(order.getUserId());
                    }).flatMap(Optional::ofNullable);

            case RESERVATION -> tradingOrderRepositoryPort.findReservationOrderByOrderId(orderId)
                    .map(order -> {
                        if (order.isFilled() || order.isCancel()) {
                            log.warn("Duplicate/Invalid trade event for already finalized reservation order: {}", order.getId().getValue());
                            return null;
                        }
                        return executeReservationOrderTransactionInternal(order, response, isBuySide);
//                        UserBalanceUpdatedEvent userBalanceUpdatedEvent = userBalanceHandler
//                                .makeUserBalanceUpdatedEvent(order.getUserId());
//                        return new TradeMatchingContext(Optional.of(tradeEvent), Optional.of(userBalanceUpdatedEvent));
                    }).flatMap(Optional::ofNullable);

            default -> Optional.empty();
        };
    }

    // ---------------- 내부 구현 ----------------

    private TradeMatchingContext executeMarketTradeTransactionInternal(MarketOrder marketOrder,
                                                                    PredicatedTradeKafkaResponse response,
                                                                    boolean isBuySide) {
        FeeRate feeRate = feeRateResolver.resolve(marketOrder.getUserId(), marketOrder.getOrderSide());
        OrderPrice orderPrice = new OrderPrice(new BigDecimal(response.getOrderPrice()));
        Quantity quantity = new Quantity(new BigDecimal(response.getQuantity()));
        FeeAmount feeAmount = feeRate.calculateFeeAmount(orderPrice, quantity);

        TradeCreatedEvent tradeEvent = tradeDomainService.createTrade(
                new TradeId(UUID.randomUUID()),
                marketOrder.getMarketId(),
                marketOrder.getUserId(),
                marketOrder.getId(),
                orderPrice,
                quantity,
                isBuySide ? TransactionType.TRADE_BUY : TransactionType.TRADE_SELL,
                feeAmount,
                feeRate
        );

        BigDecimal totalAmount = orderPrice.getValue().multiply(quantity.getValue());
        if (marketOrder.isBuyOrder()) {
            orderDomainService.applyMarketOrder(marketOrder, quantity, orderPrice);
        } else {
            orderDomainService.applyOrder(marketOrder, quantity);
        }

        Optional<UserBalanceUpdatedEvent> userBalanceUpdatedEvent;
        if (isBuySide) {
            userBalanceUpdatedEvent = userBalanceHandler.deduct(marketOrder.getUserId(), marketOrder.getId(), totalAmount.add(feeAmount.getValue()));
        } else {
            userBalanceUpdatedEvent = userBalanceHandler.credit(marketOrder.getUserId(), marketOrder.getId(), totalAmount);
        }

        tradingTradeRecordRepositoryPort.saveTrade(tradeEvent.getDomainType());
        clearMinorLockedBalance(marketOrder);
        tradingOrderRepositoryPort.saveMarketOrder(marketOrder);

        log.info("[PredictedTrade] Trade processed (market): orderId={}, qty={}, price={}",
                marketOrder.getId().getValue(), quantity.getValue(), orderPrice.getValue());
        return new TradeMatchingContext(Optional.of(tradeEvent),userBalanceUpdatedEvent);
    }

    private TradeMatchingContext executeReservationOrderTransactionInternal(ReservationOrder reservationOrder,
                                                                         PredicatedTradeKafkaResponse response,
                                                                         boolean isBuySide) {
        FeeRate feeRate = feeRateResolver.resolve(reservationOrder.getUserId(), reservationOrder.getOrderSide());
        OrderPrice orderPrice = new OrderPrice(new BigDecimal(response.getOrderPrice()));
        Quantity quantity = new Quantity(new BigDecimal(response.getQuantity()));
        FeeAmount feeAmount = feeRate.calculateFeeAmount(orderPrice, quantity);

        TradeCreatedEvent tradeEvent = tradeDomainService.createTrade(
                new TradeId(UUID.randomUUID()),
                reservationOrder.getMarketId(),
                reservationOrder.getUserId(),
                reservationOrder.getId(),
                orderPrice,
                quantity,
                isBuySide ? TransactionType.TRADE_BUY : TransactionType.TRADE_SELL,
                feeAmount,
                feeRate
        );

        BigDecimal totalAmount = orderPrice.getValue().multiply(quantity.getValue());
        Optional<UserBalanceUpdatedEvent> userBalanceUpdatedEvent;
        if (isBuySide) {
            userBalanceUpdatedEvent = userBalanceHandler.deduct(reservationOrder.getUserId(), reservationOrder.getId(), totalAmount.add(feeAmount.getValue()));
        } else {
            userBalanceUpdatedEvent = userBalanceHandler.credit(reservationOrder.getUserId(), reservationOrder.getId(), totalAmount);
        }

        orderDomainService.applyOrder(reservationOrder, quantity);
        tradingOrderRepositoryPort.saveReservationOrder(reservationOrder);
        tradingTradeRecordRepositoryPort.saveTrade(tradeEvent.getDomainType());
        clearMinorLockedBalance(reservationOrder);

        log.info("[PredictedTrade] Trade processed (reservation): orderId={}, qty={}, price={}",
                reservationOrder.getId().getValue(), quantity.getValue(), orderPrice.getValue());
        return new TradeMatchingContext(Optional.of(tradeEvent),userBalanceUpdatedEvent);
    }

    private TradeMatchingContext executeLimitOrderTransactionInternal(LimitOrder limitOrder,
                                                                   PredicatedTradeKafkaResponse response,
                                                                   boolean isBuySide) {
        FeeRate feeRate = feeRateResolver.resolve(limitOrder.getUserId(), limitOrder.getOrderSide());
        OrderPrice orderPrice = new OrderPrice(new BigDecimal(response.getOrderPrice()));
        Quantity quantity = new Quantity(new BigDecimal(response.getQuantity()));
        FeeAmount feeAmount = feeRate.calculateFeeAmount(orderPrice, quantity);

        TradeCreatedEvent tradeEvent = tradeDomainService.createTrade(
                new TradeId(UUID.randomUUID()),
                limitOrder.getMarketId(),
                limitOrder.getUserId(),
                limitOrder.getId(),
                orderPrice,
                quantity,
                isBuySide ? TransactionType.TRADE_BUY : TransactionType.TRADE_SELL,
                feeAmount,
                feeRate
        );

        BigDecimal totalAmount = orderPrice.getValue().multiply(quantity.getValue());
        Optional<UserBalanceUpdatedEvent> userBalanceUpdatedEvent;
        if (isBuySide) {
            userBalanceUpdatedEvent = userBalanceHandler.deduct(limitOrder.getUserId(), limitOrder.getId(), totalAmount.add(feeAmount.getValue()));
        } else {
            userBalanceUpdatedEvent = userBalanceHandler.credit(limitOrder.getUserId(), limitOrder.getId(), totalAmount);
        }

        orderDomainService.applyOrder(limitOrder, quantity);
        tradingOrderRepositoryPort.saveLimitOrder(limitOrder);
        tradingTradeRecordRepositoryPort.saveTrade(tradeEvent.getDomainType());
        clearMinorLockedBalance(limitOrder);

        log.info("[PredictedTrade] Trade processed (limit): orderId={}, qty={}, price={}",
                limitOrder.getId().getValue(), quantity.getValue(), orderPrice.getValue());
        return new TradeMatchingContext(Optional.of(tradeEvent),userBalanceUpdatedEvent);
    }

    // ----------------- 기존 clearMinorLockedBalance 메서드들 -----------------

    private void clearMinorLockedBalance(LimitOrder limitOrder) {
        userBalanceHandler.findUserBalanceByUserId(limitOrder.getUserId()).getLockBalances().stream()
                .filter(lock -> lock.getId().equals(limitOrder.getId()))
                .findAny()
                .filter(lock -> limitOrder.isFilled())
                .ifPresent(lockBalance ->
                        userBalanceHandler.finalizeLockedAmount(userBalanceHandler.findUserBalanceByUserId(limitOrder.getUserId()), lockBalance));
    }

    private void clearMinorLockedBalance(ReservationOrder reservationOrder) {
        UserBalance userBalance = userBalanceHandler.findUserBalanceByUserId(reservationOrder.getUserId());
        userBalance.getLockBalances().stream()
                .filter(lockBalance -> lockBalance.getId().equals(reservationOrder.getId()))
                .findAny()
                .filter(lockBalance -> reservationOrder.isFilled() || reservationOrder.getRemainingQuantity().isZero())
                .ifPresent(lockBalance -> {
                    log.info("[ReservationOrder] Locked balance for remaining Money: {}", lockBalance.getLockedAmount().getValue());
                    userBalanceHandler.finalizeLockedAmount(userBalance, lockBalance);
                });
    }

    private void clearMinorLockedBalance(MarketOrder marketOrder) {
        UserBalance userBalance = userBalanceHandler.findUserBalanceByUserId(marketOrder.getUserId());
        userBalance.getLockBalances().stream()
                .filter(lockBalance -> lockBalance.getId().equals(marketOrder.getId()))
                .findAny()
                .filter(lockBalance -> marketOrder.isFilled()
                        || marketOrder.getRemainingPrice().isLessThan(new OrderPrice(BigDecimal.valueOf(10L))))
                .ifPresent(lockBalance -> {
                    log.info("[MarketOrder] Locked balance for remaining money: {}", lockBalance.getLockedAmount().getValue());
                    userBalanceHandler.finalizeLockedAmount(userBalance, lockBalance);
                    orderDomainService.filledOrder(marketOrder);
                });
    }
}
