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
import shop.shportfolio.trading.application.ports.output.kafka.TradeKafkaPublisher;
import shop.shportfolio.trading.application.ports.output.kafka.UserBalanceKafkaPublisher;
import shop.shportfolio.trading.application.ports.output.repository.TradingOrderRepositoryPort;
import shop.shportfolio.trading.application.ports.output.repository.TradingTradeRecordRepositoryPort;
import shop.shportfolio.trading.domain.OrderDomainService;
import shop.shportfolio.trading.domain.TradeDomainService;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.MarketOrder;
import shop.shportfolio.trading.domain.entity.Order;
import shop.shportfolio.trading.domain.entity.ReservationOrder;
import shop.shportfolio.trading.domain.entity.userbalance.UserBalance;
import shop.shportfolio.trading.domain.event.TradeCreatedEvent;

import java.math.BigDecimal;
import java.util.UUID;

@Slf4j
@Component
public class PredicatedTradeListenerImpl implements PredicatedTradeListener {

    private final UserBalanceHandler userBalanceHandler;
    private final TradeKafkaPublisher tradeKafkaPublisher;
    private final UserBalanceKafkaPublisher userBalanceKafkaPublisher;
    private final TradingOrderRepositoryPort tradingOrderRepositoryPort;
    private final FeeRateResolver feeRateResolver;
    private final TradeDomainService tradeDomainService;
    private final TradingTradeRecordRepositoryPort tradingTradeRecordRepositoryPort;
    private final OrderDomainService orderDomainService;

    @Autowired
    public PredicatedTradeListenerImpl(UserBalanceHandler userBalanceHandler,
                                       TradeKafkaPublisher tradeKafkaPublisher,
                                       UserBalanceKafkaPublisher userBalanceKafkaPublisher,
                                       TradingOrderRepositoryPort tradingOrderRepositoryPort,
                                       FeeRateResolver feeRateResolver,
                                       TradeDomainService tradeDomainService,
                                       TradingTradeRecordRepositoryPort tradingTradeRecordRepositoryPort,
                                       OrderDomainService orderDomainService) {
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
    @Transactional
    public void process(PredicatedTradeKafkaResponse response) {
        if (!response.getBuyOrderId().contains("anonymous")) {
            switch (response.getBuyOrderType()) {
                case LIMIT -> {
                    tradingOrderRepositoryPort.findLimitOrderByOrderId(response.getBuyOrderId()).ifPresent(
                            limitOrder -> {
                                processLimitOrder(limitOrder, response, true);
                            });
                }
                case MARKET -> {
                    tradingOrderRepositoryPort.findMarketOrderByOrderId(response.getBuyOrderId()).ifPresent(
                            marketOrder -> processMarketOrder(marketOrder, response, true));
                }
                case RESERVATION -> {
                    tradingOrderRepositoryPort.findReservationOrderByOrderId(response.getBuyOrderId()).ifPresent(
                            reservationOrder -> {
                                processReservationOrder(reservationOrder, response, true);
                            }
                    );
                }
            }
        }
        if (!response.getSellOrderId().contains("anonymous")) {
            switch (response.getSellOrderType()) {
                case LIMIT -> {
                    tradingOrderRepositoryPort.findLimitOrderByOrderId(response.getSellOrderId()).ifPresent(
                            limitOrder -> {
                                processLimitOrder(limitOrder, response, false);
                            });
                }
                case MARKET -> {
                    tradingOrderRepositoryPort.findMarketOrderByOrderId(response.getSellOrderId()).ifPresent(
                            marketOrder -> processMarketOrder(marketOrder, response, false));
                }
                case RESERVATION -> {
                    tradingOrderRepositoryPort.findReservationOrderByOrderId(response.getSellOrderId()).ifPresent(
                            reservationOrder -> {
                                processReservationOrder(reservationOrder, response, false);
                            }
                    );
                }
            }
        }
    }

    private void processMarketOrder(MarketOrder marketOrder, PredicatedTradeKafkaResponse response, boolean isBuySide) {
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
        orderDomainService.applyMarketOrder(marketOrder, quantity, orderPrice);
        tradingOrderRepositoryPort.saveMarketOrder(marketOrder);
        if (isBuySide) {
            userBalanceHandler.deduct(marketOrder.getUserId(), marketOrder.getId(),
                    totalAmount.add(feeAmount.getValue()));
        } else {
            userBalanceHandler.credit(marketOrder.getUserId(), marketOrder.getId(), totalAmount);
        }

        tradingTradeRecordRepositoryPort.saveTrade(tradeEvent.getDomainType());
        clearMinorLockedBalance(marketOrder);
        userBalanceKafkaPublisher.publish(userBalanceHandler.makeUserBalanceUpdatedEvent(marketOrder.getUserId()));
        tradeKafkaPublisher.publish(tradeEvent);
        log.info("[PredictedTrade] Trade processed: orderId={}, qty={}, price={}",
                marketOrder.getId().getValue(), quantity.getValue(), orderPrice.getValue());
    }

    private void processReservationOrder(ReservationOrder reservationOrder,
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
        userBalanceKafkaPublisher.publish(userBalanceHandler.makeUserBalanceUpdatedEvent(reservationOrder.getUserId()));
        tradeKafkaPublisher.publish(tradeEvent);
        log.info("[PredictedTrade] Trade processed: orderId={}, qty={}, price={}",
                reservationOrder.getId().getValue(), quantity.getValue(), orderPrice.getValue());
    }


    private void processLimitOrder(LimitOrder limitOrder, PredicatedTradeKafkaResponse response
            , boolean isBuySide) {
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
        userBalanceKafkaPublisher.publish(userBalanceHandler.makeUserBalanceUpdatedEvent(limitOrder.getUserId()));
        tradeKafkaPublisher.publish(tradeEvent);
        log.info("[PredictedTrade] Trade processed: orderId={}, qty={}, price={}",
                limitOrder.getId().getValue(), quantity.getValue(), orderPrice.getValue());
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
                        marketOrder.getRemainingPrice().isLessThan(new OrderPrice(BigDecimal.ONE)))
                .ifPresent(lockBalance -> {
                    log.info("[MarketOrder] Locked balance for remaining money: {}",
                            lockBalance.getLockedAmount().getValue());
                    userBalanceHandler.finalizeLockedAmount(userBalance, lockBalance);
                });
    }

}
