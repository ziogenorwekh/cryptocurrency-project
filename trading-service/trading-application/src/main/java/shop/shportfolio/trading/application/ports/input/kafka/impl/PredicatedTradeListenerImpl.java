package shop.shportfolio.trading.application.ports.input.kafka.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.trading.application.dto.trade.PredicatedTradeKafkaResponse;
import shop.shportfolio.trading.application.handler.UserBalanceHandler;
import shop.shportfolio.trading.application.orderbook.matching.FeeRateResolver;
import shop.shportfolio.trading.application.ports.input.kafka.PredicatedTradeListener;
import shop.shportfolio.trading.application.ports.output.kafka.TradePublisher;
import shop.shportfolio.trading.application.ports.output.kafka.UserBalancePublisher;
import shop.shportfolio.trading.application.ports.output.repository.TradingOrderRepositoryPort;
import shop.shportfolio.trading.application.ports.output.repository.TradingTradeRecordRepositoryPort;
import shop.shportfolio.trading.domain.OrderDomainService;
import shop.shportfolio.trading.domain.TradeDomainService;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.MarketOrder;
import shop.shportfolio.trading.domain.entity.ReservationOrder;
import shop.shportfolio.trading.domain.entity.userbalance.UserBalance;
import shop.shportfolio.trading.domain.event.TradeCreatedEvent;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Component
public class PredicatedTradeListenerImpl implements PredicatedTradeListener {

    private final UserBalanceHandler userBalanceHandler;
    private final TradePublisher tradePublisher;
    private final UserBalancePublisher userBalancePublisher;
    private final TradingOrderRepositoryPort tradingOrderRepositoryPort;
    private final FeeRateResolver feeRateResolver;
    private final TradeDomainService tradeDomainService;
    private final TradingTradeRecordRepositoryPort tradingTradeRecordRepositoryPort;
    private final OrderDomainService orderDomainService;

    @Autowired
    public PredicatedTradeListenerImpl(UserBalanceHandler userBalanceHandler,
                                       TradePublisher tradePublisher,
                                       UserBalancePublisher userBalancePublisher,
                                       TradingOrderRepositoryPort tradingOrderRepositoryPort,
                                       FeeRateResolver feeRateResolver,
                                       TradeDomainService tradeDomainService,
                                       TradingTradeRecordRepositoryPort tradingTradeRecordRepositoryPort,
                                       OrderDomainService orderDomainService) {
        this.userBalanceHandler = userBalanceHandler;
        this.tradePublisher = tradePublisher;
        this.userBalancePublisher = userBalancePublisher;
        this.tradingOrderRepositoryPort = tradingOrderRepositoryPort;
        this.feeRateResolver = feeRateResolver;
        this.tradeDomainService = tradeDomainService;
        this.tradingTradeRecordRepositoryPort = tradingTradeRecordRepositoryPort;
        this.orderDomainService = orderDomainService;
    }

    @Override
    public void process(PredicatedTradeKafkaResponse response) {
        if (!response.getBuyOrderId().contains("anonymous")) {
            // ... (Buy Side Switch 로직은 그대로 유지)
            switch (response.getBuyOrderType()) {
                case LIMIT -> {
                    tradingOrderRepositoryPort.findLimitOrderByOrderId(response.getBuyOrderId()).ifPresent(
                            limitOrder -> processLimitOrderTransaction(limitOrder, response, true));
                }
                case MARKET -> {
                    tradingOrderRepositoryPort.findMarketOrderByOrderId(response.getBuyOrderId()).ifPresent(
                            marketOrder -> processMarketOrderTransaction(marketOrder, response, true));
                }
                case RESERVATION -> {
                    tradingOrderRepositoryPort.findReservationOrderByOrderId(response.getBuyOrderId()).ifPresent(
                            reservationOrder -> processReservationOrderTransaction(reservationOrder, response, true));
                }
            }
        }
        if (!response.getSellOrderId().contains("anonymous")) {
            // ... (Sell Side Switch 로직은 그대로 유지)
            switch (response.getSellOrderType()) {
                case LIMIT -> {
                    tradingOrderRepositoryPort.findLimitOrderByOrderId(response.getSellOrderId()).ifPresent(
                            limitOrder -> processLimitOrderTransaction(limitOrder, response, false));
                }
                case MARKET -> {
                    tradingOrderRepositoryPort.findMarketOrderByOrderId(response.getSellOrderId()).ifPresent(
                            marketOrder -> processMarketOrderTransaction(marketOrder, response, false));
                }
                case RESERVATION -> {
                    tradingOrderRepositoryPort.findReservationOrderByOrderId(response.getSellOrderId()).ifPresent(
                            reservationOrder -> processReservationOrderTransaction(reservationOrder, response, false));
                }
            }
        }
    }

    @Transactional
    protected void processMarketOrderTransaction(MarketOrder marketOrder, PredicatedTradeKafkaResponse response, boolean isBuySide) {
        // 기존 processMarketOrder 로직에서 Kafka 발행 부분(3개)만 제외하고 그대로 유지
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
        if (isBuySide) {
            userBalanceHandler.deduct(marketOrder.getUserId(), marketOrder.getId(),
                    totalAmount.add(feeAmount.getValue()));
        } else {
            userBalanceHandler.credit(marketOrder.getUserId(), marketOrder.getId(), totalAmount);
        }

        tradingTradeRecordRepositoryPort.saveTrade(tradeEvent.getDomainType());
        clearMinorLockedBalance(marketOrder);
        tradingOrderRepositoryPort.saveMarketOrder(marketOrder);

        publishTradeUpdates(marketOrder.getUserId(), tradeEvent);

        log.info("[PredictedTrade] Trade processed: orderId={}, qty={}, price={}",
                marketOrder.getId().getValue(), quantity.getValue(), orderPrice.getValue());
    }

    @Transactional
    protected void processReservationOrderTransaction(ReservationOrder reservationOrder,
                                                    PredicatedTradeKafkaResponse response, boolean isBuySide) {
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
            userBalanceHandler.deduct(reservationOrder.getUserId(), reservationOrder.getId(),
                    totalAmount.add(feeAmount.getValue()));
        } else {
            userBalanceHandler.credit(reservationOrder.getUserId(), reservationOrder.getId(), totalAmount);
        }
        orderDomainService.applyOrder(reservationOrder, quantity);
        tradingOrderRepositoryPort.saveReservationOrder(reservationOrder);
        tradingTradeRecordRepositoryPort.saveTrade(tradeEvent.getDomainType());
        clearMinorLockedBalance(reservationOrder);

        publishTradeUpdates(reservationOrder.getUserId(), tradeEvent);

        log.info("[PredictedTrade] Trade processed: orderId={}, qty={}, price={}",
                reservationOrder.getId().getValue(), quantity.getValue(), orderPrice.getValue());
    }

    @Transactional
    protected void processLimitOrderTransaction(LimitOrder limitOrder, PredicatedTradeKafkaResponse response
            , boolean isBuySide) {
        // ... (기존 processLimitOrder 로직 복사)
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
            userBalanceHandler.deduct(limitOrder.getUserId(), limitOrder.getId(),
                    totalAmount.add(feeAmount.getValue()));
        } else {
            userBalanceHandler.credit(limitOrder.getUserId(), limitOrder.getId(), totalAmount);
        }
        orderDomainService.applyOrder(limitOrder, quantity);
        tradingOrderRepositoryPort.saveLimitOrder(limitOrder);
        tradingTradeRecordRepositoryPort.saveTrade(tradeEvent.getDomainType());
        clearMinorLockedBalance(limitOrder);

        publishTradeUpdates(limitOrder.getUserId(), tradeEvent);

        log.info("[PredictedTrade] Trade processed: orderId={}, qty={}, price={}",
                limitOrder.getId().getValue(), quantity.getValue(), orderPrice.getValue());
    }

    private void publishTradeUpdates(UserId userId, TradeCreatedEvent tradeEvent) {
        userBalancePublisher.publish(userBalanceHandler.makeUserBalanceUpdatedEvent(userId));
        tradePublisher.publish(tradeEvent);
    }

    private void clearMinorLockedBalance(LimitOrder limitOrder) {
        userBalanceHandler.findUserBalanceByUserId(limitOrder.getUserId()).getLockBalances().stream()
                .filter(lock -> lock.getId().equals(limitOrder.getId()))
                .findAny()
                .filter(lock -> limitOrder.isFilled())
                .stream().forEach(lockBalance -> userBalanceHandler.finalizeLockedAmount(
                        userBalanceHandler.findUserBalanceByUserId(limitOrder.getUserId()), lockBalance));
    }

    private void clearMinorLockedBalance(ReservationOrder reservationOrder) {
        UserBalance userBalance = userBalanceHandler.findUserBalanceByUserId(reservationOrder.getUserId());
        userBalance.getLockBalances().stream()
                .filter(lockBalance -> lockBalance.getId().equals(reservationOrder.getId()))
                .findAny()
                .filter(lockBalance -> reservationOrder.isFilled() ||
                        reservationOrder.getRemainingQuantity().isZero())
                .ifPresent(lockBalance -> {
                    log.info("[ReservationOrder] Locked balance for remaining Money: {}",
                            lockBalance.getLockedAmount().getValue());
                    userBalanceHandler.finalizeLockedAmount(userBalance, lockBalance);
                });
    }

    private void clearMinorLockedBalance(MarketOrder marketOrder) {
        UserBalance userBalance = userBalanceHandler.findUserBalanceByUserId(marketOrder.getUserId());
        userBalance.getLockBalances().stream()
                .filter(lockBalance -> lockBalance.getId().equals(marketOrder.getId()))
                .findAny()
                .filter(lockBalance -> marketOrder.isFilled() ||
                        marketOrder.getRemainingPrice().isLessThan(new OrderPrice(BigDecimal.valueOf(10L))))
                .ifPresent(lockBalance -> {
                    log.info("[MarketOrder] Locked balance for remaining money: {}",
                            lockBalance.getLockedAmount().getValue());
                    userBalanceHandler.finalizeLockedAmount(userBalance, lockBalance);
                    orderDomainService.filledOrder(marketOrder);
                });
    }
}