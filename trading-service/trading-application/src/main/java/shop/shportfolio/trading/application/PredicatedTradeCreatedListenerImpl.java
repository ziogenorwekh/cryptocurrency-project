package shop.shportfolio.trading.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.stylesheets.LinkStyle;
import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.trading.application.dto.trade.PredicatedTradeKafkaResponse;
import shop.shportfolio.trading.application.handler.UserBalanceHandler;
import shop.shportfolio.trading.application.orderbook.matching.FeeRateResolver;
import shop.shportfolio.trading.application.ports.input.kafka.PredicatedTradeCreatedListener;
import shop.shportfolio.trading.application.ports.output.kafka.TradeKafkaPublisher;
import shop.shportfolio.trading.application.ports.output.kafka.UserBalanceKafkaPublisher;
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
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class PredicatedTradeCreatedListenerImpl implements PredicatedTradeCreatedListener {

    private final UserBalanceHandler userBalanceHandler;
    private final TradeKafkaPublisher tradeKafkaPublisher;
    private final UserBalanceKafkaPublisher userBalanceKafkaPublisher;
    private final TradingOrderRepositoryPort tradingOrderRepositoryPort;
    private final FeeRateResolver feeRateResolver;
    private final TradeDomainService tradeDomainService;
    private final TradingTradeRecordRepositoryPort tradingTradeRecordRepositoryPort;
    private final OrderDomainService orderDomainService;

    @Autowired
    public PredicatedTradeCreatedListenerImpl(UserBalanceHandler userBalanceHandler,
                                              TradeKafkaPublisher tradeKafkaPublisher,
                                              UserBalanceKafkaPublisher userBalanceKafkaPublisher,
                                              TradingOrderRepositoryPort tradingOrderRepositoryPort,
                                              FeeRateResolver feeRateResolver,
                                              TradeDomainService tradeDomainService, TradingTradeRecordRepositoryPort tradingTradeRecordRepositoryPort, OrderDomainService orderDomainService) {
        this.userBalanceHandler = userBalanceHandler;
        this.tradeKafkaPublisher = tradeKafkaPublisher;
        this.userBalanceKafkaPublisher = userBalanceKafkaPublisher;
        this.tradingOrderRepositoryPort = tradingOrderRepositoryPort;
        this.feeRateResolver = feeRateResolver;
        this.tradeDomainService = tradeDomainService;
        this.tradingTradeRecordRepositoryPort = tradingTradeRecordRepositoryPort;
        this.orderDomainService = orderDomainService;
    }

    @Override
    public void updateLimitOrder(PredicatedTradeKafkaResponse response) {
        Optional.ofNullable(response)
                .flatMap(r -> tradingOrderRepositoryPort.findLimitOrderByOrderId(r.getBuyOrderId()))
                .ifPresent(buyOrder -> processPredictedTradeLimitOrder(buyOrder, response, true));

        Optional.ofNullable(response)
                .flatMap(r -> tradingOrderRepositoryPort.findLimitOrderByOrderId(r.getSellOrderId()))
                .ifPresent(sellOrder -> processPredictedTradeLimitOrder(sellOrder, response, false));
    }

    private void processPredictedTradeLimitOrder(LimitOrder limitOrder, PredicatedTradeKafkaResponse response,
                                                 boolean isBuySide) {
        List<UserBalanceUpdatedEvent> userBalanceUpdatedEventList = new ArrayList<>();
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
        if (isBuySide) {
            userBalanceHandler.deductTrade(limitOrder.getUserId(), limitOrder.getId(),
                    totalAmount.add(feeAmount.getValue())).ifPresent(userBalanceUpdatedEventList::add);
        } else {
            userBalanceHandler.creditTrade(limitOrder.getUserId(), limitOrder.getId(), totalAmount)
                    .ifPresent(userBalanceUpdatedEventList::add);
        }
        // 값 계산된 리밋 오더도 저장해야 하고,
        orderDomainService.applyOrder(limitOrder, quantity);
        tradingOrderRepositoryPort.saveLimitOrder(limitOrder);
        // 거래기록도 리포지토리에 저장해야 됌
        tradingTradeRecordRepositoryPort.saveTrade(tradeEvent.getDomainType());
        // 이거 다 지워버리는데 이러면 안되고..
        userBalanceUpdatedEventList.add(clearMinorLockedBalance(limitOrder));
        userBalanceUpdatedEventList.forEach(userBalanceKafkaPublisher::publish);
        tradeKafkaPublisher.publish(tradeEvent);
        log.info("[PredictedTrade] Trade processed: orderId={}, qty={}, price={}",
                limitOrder.getId().getValue(), quantity.getValue(), orderPrice.getValue());
    }

    @Override
    public void updateReservationOrder(PredicatedTradeKafkaResponse response) {
        Optional.ofNullable(response)
                .flatMap(r -> tradingOrderRepositoryPort.findReservationOrderByOrderId(r.getBuyOrderId()))
                .ifPresent(buyOrder -> processPredictedTradeReservationOrder(buyOrder, response, true));
        Optional.ofNullable(response)
                .flatMap(r -> tradingOrderRepositoryPort.findReservationOrderByOrderId(r.getSellOrderId()))
                .ifPresent(sellOrder -> processPredictedTradeReservationOrder(sellOrder, response, false));
    }

    private void processPredictedTradeReservationOrder(ReservationOrder reservationOrder,
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
        if (isBuySide) {
            userBalanceHandler.deduct(reservationOrder.getUserId(), reservationOrder.getId(), totalAmount.add(feeAmount.getValue()));
        } else {
            userBalanceHandler.credit(reservationOrder.getUserId(), reservationOrder.getId(), totalAmount);
        }

        UserBalanceUpdatedEvent userBalanceUpdatedEvent = clearMinorLockedBalance(reservationOrder);

        userBalanceKafkaPublisher.publish(userBalanceUpdatedEvent);
        tradeKafkaPublisher.publish(tradeEvent);

        log.info("[PredictedTrade] Trade processed: orderId={}, qty={}, price={}",
                reservationOrder.getId().getValue(), quantity.getValue(), orderPrice.getValue());
    }

    @Override
    public void updateMarketOrder(PredicatedTradeKafkaResponse response) {
        Optional.ofNullable(response)
                .flatMap(r -> tradingOrderRepositoryPort.findMarketOrderByOrderId(r.getBuyOrderId()))
                .ifPresent(buyOrder -> processPredictedTradeMarketOrder(buyOrder, response, true));

        Optional.ofNullable(response)
                .flatMap(r -> tradingOrderRepositoryPort.findMarketOrderByOrderId(r.getSellOrderId()))
                .ifPresent(sellOrder -> processPredictedTradeMarketOrder(sellOrder, response, false));
    }

    private void processPredictedTradeMarketOrder(MarketOrder marketOrder,
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
        if (isBuySide) {
            userBalanceHandler.deduct(marketOrder.getUserId(), marketOrder.getId(), totalAmount.add(feeAmount.getValue()));
        } else {
            userBalanceHandler.credit(marketOrder.getUserId(), marketOrder.getId(), totalAmount);
        }

        UserBalanceUpdatedEvent userBalanceUpdatedEvent = clearMinorLockedBalance(marketOrder);
        userBalanceKafkaPublisher.publish(userBalanceUpdatedEvent);
        tradeKafkaPublisher.publish(tradeEvent);

        log.info("[PredictedTrade] Trade processed: orderId={}, qty={}, price={}",
                marketOrder.getId().getValue(), quantity.getValue(), orderPrice.getValue());
    }

    private UserBalanceUpdatedEvent clearMinorLockedBalance(LimitOrder limitOrder) {
        return userBalanceHandler.findUserBalanceByUserId(limitOrder.getUserId()).getLockBalances().stream()
                .filter(lock -> lock.getId().equals(limitOrder.getId()))
                .findAny()
                .filter(lock -> limitOrder.isFilled())
                .map(lock -> userBalanceHandler.finalizeLockedAmount(
                        userBalanceHandler.findUserBalanceByUserId(limitOrder.getUserId()), lock))
                .orElseGet(() -> new UserBalanceUpdatedEvent(
                        userBalanceHandler.findUserBalanceByUserId(limitOrder.getUserId()),
                        MessageType.UPDATE,
                        ZonedDateTime.now(ZoneOffset.UTC))
                );
    }

    private UserBalanceUpdatedEvent clearMinorLockedBalance(ReservationOrder reservationOrder) {
        UserBalance userBalance = userBalanceHandler.findUserBalanceByUserId(reservationOrder.getUserId());
        return userBalance.getLockBalances().stream()
                .filter(lockBalance -> lockBalance.getId().equals(reservationOrder.getId()))
                .findAny()
                .filter(lockBalance -> reservationOrder.isFilled() || reservationOrder.getRemainingQuantity().isZero())
                .map(lockBalance -> {
                    log.info("[ReservationOrder] Locked balance for remaining Money: {}", lockBalance.getLockedAmount().getValue());
                    return userBalanceHandler.finalizeLockedAmount(userBalance, lockBalance);
                })
                .orElseGet(() -> {
                    log.info("[ReservationOrder] No locked balance to clear: orderId={}", reservationOrder.getId().getValue());
                    return new UserBalanceUpdatedEvent(userBalance, MessageType.UPDATE, ZonedDateTime.now(ZoneOffset.UTC));
                });
    }

    private UserBalanceUpdatedEvent clearMinorLockedBalance(MarketOrder marketOrder) {
        UserBalance userBalance = userBalanceHandler.findUserBalanceByUserId(marketOrder.getUserId());
        return userBalance.getLockBalances().stream()
                .filter(lockBalance -> lockBalance.getId().equals(marketOrder.getId()))
                .findAny()
                .filter(lockBalance -> marketOrder.getRemainingPrice().isPositive())
                .map(lockBalance -> {
                    log.info("[MarketOrder] Locked balance for remaining money: {}", lockBalance.getLockedAmount().getValue());
                    return userBalanceHandler.finalizeLockedAmount(userBalance, lockBalance);
                })
                .orElseGet(() -> {
                    log.info("[MarketOrder] No locked balance to clear for marketOrderId={}", marketOrder.getId().getValue());
                    return new UserBalanceUpdatedEvent(userBalance, MessageType.UPDATE, ZonedDateTime.now(ZoneOffset.UTC));
                });
    }

}
