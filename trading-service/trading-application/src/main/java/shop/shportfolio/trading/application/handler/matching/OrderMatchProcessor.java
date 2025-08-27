package shop.shportfolio.trading.application.handler.matching;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.valueobject.FeeAmount;
import shop.shportfolio.common.domain.valueobject.FeeRate;
import shop.shportfolio.common.domain.valueobject.OrderPrice;
import shop.shportfolio.common.domain.valueobject.Quantity;
import shop.shportfolio.common.domain.valueobject.TransactionType;
import shop.shportfolio.trading.application.handler.UserBalanceHandler;
import shop.shportfolio.trading.application.ports.output.repository.TradingTradeRecordRepositoryPort;
import shop.shportfolio.trading.domain.OrderDomainService;
import shop.shportfolio.trading.domain.TradeDomainService;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.MarketOrder;
import shop.shportfolio.trading.domain.entity.Order;
import shop.shportfolio.trading.domain.entity.ReservationOrder;
import shop.shportfolio.trading.domain.entity.orderbook.OrderBook;
import shop.shportfolio.trading.domain.entity.orderbook.PriceLevel;
import shop.shportfolio.trading.domain.entity.userbalance.UserBalance;
import shop.shportfolio.trading.domain.event.TradeCreatedEvent;
import shop.shportfolio.trading.domain.valueobject.TickPrice;
import shop.shportfolio.trading.domain.valueobject.TradeId;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
public class OrderMatchProcessor {

    private final OrderDomainService orderDomainService;
    private final TradeDomainService tradeDomainService;
    private final TradingTradeRecordRepositoryPort tradeRepository;
    private final UserBalanceHandler userBalanceHandler;

    @Autowired
    public OrderMatchProcessor(OrderDomainService orderDomainService,
                               TradeDomainService tradeDomainService,
                               TradingTradeRecordRepositoryPort tradeRepository,
                               UserBalanceHandler userBalanceHandler) {
        this.orderDomainService = orderDomainService;
        this.tradeDomainService = tradeDomainService;
        this.tradeRepository = tradeRepository;
        this.userBalanceHandler = userBalanceHandler;
    }

    public  List<TradeCreatedEvent> processReservation(
            OrderBook orderBook,
            ReservationOrder reservationOrder,
            FeeRate feeRate,
            OrderExecutionChecker executionChecker
    ) {
        List<TradeCreatedEvent> trades = new ArrayList<>();

        var counterPriceLevels = reservationOrder.isBuyOrder()
                ? orderBook.getSellPriceLevels()
                : orderBook.getBuyPriceLevels();
        log.info("[Reservation] Start matching reservation order {}: RemainingQty={}",
                reservationOrder.getId().getValue(), reservationOrder.getRemainingQuantity().getValue());

        for (Map.Entry<TickPrice, PriceLevel> entry : counterPriceLevels.entrySet()) {
            TickPrice tickPrice = entry.getKey();
            PriceLevel priceLevel = entry.getValue();
            OrderPrice executionPrice = new OrderPrice(tickPrice.getValue());

            log.info("[Reservation] Checking price level {} for execution", tickPrice.getValue());

            if (!orderDomainService.isPriceMatch(reservationOrder, executionPrice)) {
                log.info("[Reservation] Price {} does not match. Skipping.", executionPrice.getValue());
                continue;
            }

            while (reservationOrder.isUnfilled() && !priceLevel.isEmpty()) {
                Order restingOrder = priceLevel.peekOrder();
                log.info("[Reservation] Evaluating restingOrder {} qty={} price={}",
                        restingOrder.getId().getValue(),
                        restingOrder.getRemainingQuantity().getValue(),
                        restingOrder.getOrderPrice().getValue());

                if (!executionChecker.isExecutable(reservationOrder, restingOrder.getOrderPrice())) {
                    log.info("[Reservation] Execution condition not met. Stopping.");
                    break;
                }
                if (executionChecker.isExpired(reservationOrder)) {
                    log.info("[Reservation] Reservation expired during matching.");
                    break;
                }

                Quantity execQty = orderDomainService.applyOrder(reservationOrder, restingOrder.getRemainingQuantity());
                orderDomainService.applyOrder(restingOrder, execQty);

                FeeAmount feeAmount = feeRate.calculateFeeAmount(executionPrice, execQty);

                TradeCreatedEvent tradeEvent = tradeDomainService.createTrade(
                        new TradeId(UUID.randomUUID()),
                        reservationOrder.getMarketId(),
                        reservationOrder.getUserId(),
                        reservationOrder.getId(),
                        executionPrice,
                        execQty,
                        reservationOrder.isBuyOrder() ? TransactionType.TRADE_BUY : TransactionType.TRADE_SELL,
                        feeAmount,
                        feeRate
                );

                tradeRepository.saveTrade(tradeEvent.getDomainType());
                log.info("[Reservation] Trade recorded: {}", tradeEvent.getDomainType());

                BigDecimal sellAmount = executionPrice.getValue().multiply(execQty.getValue());
                BigDecimal buyAmount = sellAmount.add(feeAmount.getValue());

                if (reservationOrder.isBuyOrder()) {
                    userBalanceHandler.deduct(reservationOrder.getUserId(), reservationOrder.getId(), buyAmount);
                    userBalanceHandler.credit(restingOrder.getUserId(), restingOrder.getId(), sellAmount);
                } else {
                    userBalanceHandler.credit(reservationOrder.getUserId(), reservationOrder.getId(), sellAmount);
                    userBalanceHandler.deduct(restingOrder.getUserId(), restingOrder.getId(), buyAmount);
                }

                trades.add(tradeEvent);
                log.info("[Reservation] Executed trade: qty={}, price={}",
                        execQty.getValue(), executionPrice.getValue());

                if (restingOrder.isFilled()) {
                    log.info("[Reservation] Resting order {} filled. Removing from PriceLevel", restingOrder.getId().getValue());
                    priceLevel.popOrder();
                }

                if (reservationOrder.isFilled()) {
                    log.info("[Reservation] Reservation order fully filled.");
                    break;
                }
            }

            if (reservationOrder.isFilled()) break;
        }

        return trades;
    }

    public  List<TradeCreatedEvent> processLimitOrder(
            OrderBook orderBook,
            LimitOrder limitOrder,
            FeeRate feeRate,
            OrderExecutionChecker executionChecker
    ) {
        List<TradeCreatedEvent> trades = new ArrayList<>();
        var counterPriceLevels = limitOrder.isBuyOrder()
                ? orderBook.getSellPriceLevels()
                : orderBook.getBuyPriceLevels();

        for (Map.Entry<TickPrice, PriceLevel> entry : counterPriceLevels.entrySet()) {
            TickPrice tickPrice = entry.getKey();
            PriceLevel priceLevel = entry.getValue();
            OrderPrice executionPrice = new OrderPrice(tickPrice.getValue());

            log.info("[LimitOrder] Checking price level {} for matching", tickPrice.getValue());
            if (!executionChecker.canMatchPrice(limitOrder, tickPrice)) break;

            while (limitOrder.isUnfilled() && !priceLevel.isEmpty()) {
                Order restingOrder = priceLevel.peekOrder();
                Quantity execQty = orderDomainService.applyOrder(limitOrder, restingOrder.getRemainingQuantity());
                orderDomainService.applyOrder(restingOrder, execQty);

                FeeAmount feeAmount = feeRate.calculateFeeAmount(executionPrice, execQty);

                TradeCreatedEvent tradeEvent = tradeDomainService.createTrade(
                        new TradeId(UUID.randomUUID()),
                        limitOrder.getMarketId(),
                        limitOrder.getUserId(),
                        limitOrder.getId(),
                        executionPrice,
                        execQty,
                        limitOrder.isBuyOrder() ? TransactionType.TRADE_BUY : TransactionType.TRADE_SELL,
                        feeAmount,
                        feeRate
                );

                tradeRepository.saveTrade(tradeEvent.getDomainType());
                log.info("[LimitOrder] Trade recorded: {}", tradeEvent.getDomainType());

                BigDecimal sellAmount = executionPrice.getValue().multiply(execQty.getValue());
                BigDecimal buyAmount = sellAmount.add(feeAmount.getValue());

                if (limitOrder.isBuyOrder()) {
                    userBalanceHandler.deduct(limitOrder.getUserId(), limitOrder.getId(), buyAmount);
                    userBalanceHandler.credit(restingOrder.getUserId(), restingOrder.getId(), sellAmount);
                } else {
                    userBalanceHandler.credit(limitOrder.getUserId(), limitOrder.getId(), sellAmount);
                    userBalanceHandler.deduct(restingOrder.getUserId(), restingOrder.getId(), buyAmount);
                }

                trades.add(tradeEvent);
                log.info("[LimitOrder] Executed trade: qty={}, price={}", execQty.getValue(), executionPrice.getValue());

                if (restingOrder.isFilled()) {
                    log.info("[LimitOrder] Resting order {} filled. Removing from PriceLevel", restingOrder.getId().getValue());
                    priceLevel.popOrder();
                }
            }

            if (limitOrder.isFilled()) break;
        }

        return trades;
    }

    public List<TradeCreatedEvent> processMarketOrder(
            MarketOrder marketOrder,
            PriceLevel priceLevel,
            FeeRate feeRate
    ) {
        List<TradeCreatedEvent> trades = new ArrayList<>();

        while (marketOrder.isUnfilled() && !priceLevel.isEmpty()) {
            Order restingOrder = priceLevel.peekOrder();
            log.info("[MarketOrder] Peeked resting order: id={}, price={}, remainingQty={}",
                    restingOrder.getId().getValue(),
                    restingOrder.getOrderPrice().getValue(),
                    restingOrder.getRemainingQuantity().getValue());

            BigDecimal maxQtyByPrice = marketOrder.getRemainingPrice().getValue()
                    .divide(restingOrder.getOrderPrice().getValue(), 8, BigDecimal.ROUND_DOWN);
            Quantity execQty = Quantity.of(maxQtyByPrice.min(restingOrder.getRemainingQuantity().getValue()));

            if (execQty.isZero()) break;

            orderDomainService.applyOrder(restingOrder, execQty);
            orderDomainService.applyMarketOrder(marketOrder, execQty, restingOrder.getOrderPrice());

            OrderPrice executionPrice = restingOrder.getOrderPrice();
            FeeAmount feeAmount = feeRate.calculateFeeAmount(executionPrice, execQty);

            TradeCreatedEvent tradeEvent = tradeDomainService.createTrade(
                    new TradeId(UUID.randomUUID()),
                    marketOrder.getMarketId(),
                    marketOrder.getUserId(),
                    marketOrder.getId(),
                    executionPrice,
                    execQty,
                    marketOrder.isBuyOrder() ? TransactionType.TRADE_BUY : TransactionType.TRADE_SELL,
                    feeAmount,
                    feeRate
            );

            tradeRepository.saveTrade(tradeEvent.getDomainType());
            log.info("[MarketOrder] Trade recorded: {}", tradeEvent.getDomainType());

            BigDecimal sellAmount = executionPrice.getValue().multiply(execQty.getValue());
            BigDecimal buyAmount = sellAmount.add(feeAmount.getValue());

            if (marketOrder.isBuyOrder()) {
                userBalanceHandler.deduct(marketOrder.getUserId(), marketOrder.getId(), buyAmount);
                userBalanceHandler.credit(restingOrder.getUserId(), restingOrder.getId(), sellAmount);
            } else {
                userBalanceHandler.credit(marketOrder.getUserId(), marketOrder.getId(), sellAmount);
                userBalanceHandler.deduct(restingOrder.getUserId(), restingOrder.getId(), buyAmount);
            }

            trades.add(tradeEvent);
            log.info("[MarketOrder] Executed trade: qty={}, price={}", execQty.getValue(), executionPrice.getValue());

            if (restingOrder.isFilled()) {
                log.info("[MarketOrder] Resting order {} filled. Removing from PriceLevel", restingOrder.getId().getValue());
                priceLevel.popOrder();
                log.info("[MarketOrder] Remaining orders in PriceLevel: {}", priceLevel.getOrders().size());
            }

            if (marketOrder.getRemainingPrice().isZero()) {
                log.info("[MarketOrder] Market order {} fully matched", marketOrder.getId().getValue());
                break;
            }
        }

        return trades;
    }
}
