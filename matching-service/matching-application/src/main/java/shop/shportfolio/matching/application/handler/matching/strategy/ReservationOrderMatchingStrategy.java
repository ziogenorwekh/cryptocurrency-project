package shop.shportfolio.matching.application.handler.matching.strategy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.valueobject.OrderPrice;
import shop.shportfolio.common.domain.valueobject.Quantity;
import shop.shportfolio.common.domain.valueobject.TransactionType;
import shop.shportfolio.matching.application.dto.order.MatchedContext;
import shop.shportfolio.matching.domain.MatchingDomainService;
import shop.shportfolio.matching.domain.entity.MatchingOrderBook;
import shop.shportfolio.matching.domain.entity.MatchingPriceLevel;
import shop.shportfolio.matching.domain.event.PredictedTradeCreatedEvent;
import shop.shportfolio.trading.domain.entity.Order;
import shop.shportfolio.trading.domain.entity.ReservationOrder;
import shop.shportfolio.trading.domain.valueobject.OrderType;
import shop.shportfolio.trading.domain.valueobject.TickPrice;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

@Slf4j
@Component
public class ReservationOrderMatchingStrategy implements OrderMatchingStrategy<ReservationOrder> {

    private final MatchingDomainService matchingDomainService;

    @Autowired
    public ReservationOrderMatchingStrategy(MatchingDomainService matchingDomainService) {
        this.matchingDomainService = matchingDomainService;
    }

    @Override
    public boolean supports(Order order) {
        return OrderType.RESERVATION.equals(order.getOrderType());
    }

    @Override
    public MatchedContext<ReservationOrder> match(MatchingOrderBook matchingOrderBook,
                                                  ReservationOrder reservationOrder) {
        List<PredictedTradeCreatedEvent> trades = new ArrayList<>();

//        log.info("[Reservation] OrderBook levels: buy={}, sell={}",
//                matchingOrderBook.getBuyPriceLevels().size(),
//                matchingOrderBook.getSellPriceLevels().size());
//
//        log.info("[Reservation] New reservation order received: id={}, user={}, side={}, remainingQty={}, market={}",
//                reservationOrder.getId().getValue(),
//                reservationOrder.getUserId().getValue(),
//                reservationOrder.getOrderSide().getValue(),
//                reservationOrder.getRemainingQuantity().getValue(),
//                reservationOrder.getMarketId().getValue());

        NavigableMap<TickPrice, MatchingPriceLevel> counterPriceLevels = reservationOrder.isBuyOrder()
                ? matchingOrderBook.getSellPriceLevels()
                : matchingOrderBook.getBuyPriceLevels();

        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);

        for (Map.Entry<TickPrice, MatchingPriceLevel> entry : counterPriceLevels.entrySet()) {
            TickPrice tickPrice = entry.getKey();
            MatchingPriceLevel matchingPriceLevel = entry.getValue();
            OrderPrice executionPrice = new OrderPrice(tickPrice.getValue());

            log.debug("[Reservation] Checking price level {} with restingOrders={}, takerRemaining={}",
                    tickPrice.getValue(),
                    matchingPriceLevel.getOrders().size(),
                    reservationOrder.getRemainingQuantity().getValue());

            if (!reservationOrder.isPriceMatch(executionPrice)) {
//                log.info("[Reservation] Price {} does not match. Skipping.", executionPrice.getValue());
                continue;
            }

            while (reservationOrder.isUnfilled() && !matchingPriceLevel.isEmpty()) {
                now = LocalDateTime.now(ZoneOffset.UTC);

                Order restingOrder = matchingPriceLevel.peekOrder();

                log.debug("[Reservation] Before trade: takerRemaining={}, restingRemaining={}",
                        reservationOrder.getRemainingQuantity().getValue(),
                        restingOrder.getRemainingQuantity().getValue());

                if (!reservationOrder.canExecute(restingOrder.getOrderPrice(), now)) {
                    log.info("[Reservation] Execution condition not met for restingOrder {}. Stopping.",
                            restingOrder.getId().getValue());
                    break;
                }

                if (reservationOrder.isExpired(now)) {
                    log.info("[Reservation] Reservation order expired during matching.");
                    break;
                }

                Quantity execQty = reservationOrder.applyTrade(restingOrder.getRemainingQuantity());

                if (execQty.isZero()) {
                    log.warn("[Reservation] Zero execution quantity detected. Breaking to avoid infinite loop.");
                    break;
                }

                restingOrder.applyTrade(execQty);

                PredictedTradeCreatedEvent createdEvent = matchingDomainService.createPredictedTrade(
                        reservationOrder.getMarketId(),
                        reservationOrder.getUserId(),
                        reservationOrder,
                        restingOrder,
                        executionPrice,
                        execQty,
                        reservationOrder.isBuyOrder() ? TransactionType.TRADE_BUY : TransactionType.TRADE_SELL
                );

                trades.add(createdEvent);

                log.info("[Reservation] Trade executed: takerId={}, makerId={}, qty={}, price={}, takerRemaining(before)={}, makerRemaining(before)={}",
                        reservationOrder.getId().getValue(),
                        restingOrder.getId().getValue(),
                        execQty.getValue(),
                        executionPrice.getValue(),
                        reservationOrder.getRemainingQuantity().getValue(),
                        restingOrder.getRemainingQuantity().getValue());

                if (restingOrder.isFilled()) {
                    matchingPriceLevel.popOrder();
                    log.debug("[Reservation] Resting order {} filled and removed from PriceLevel. Remaining orders={}",
                            restingOrder.getId().getValue(),
                            matchingPriceLevel.getOrders().size());
                }

                if (reservationOrder.isFilled()) {
                    log.debug("[Reservation] Reservation order fully filled.");
                    break;
                }
            }

            if (matchingPriceLevel.isEmpty()) {
                log.debug("[Reservation] PriceLevel empty after matching: tickPrice={}", tickPrice.getValue());
            }

            log.debug("[Reservation] After matching tick {}: remaining takerQty={}, remaining restingOrders={}",
                    tickPrice.getValue(),
                    reservationOrder.getRemainingQuantity().getValue(),
                    matchingPriceLevel.getOrders().size());

            if (reservationOrder.isFilled()) break;
        }

//        log.info("[Reservation] Matching finished: takerId={}, totalTrades={}, finalRemaining={}",
//                reservationOrder.getId().getValue(),
//                trades.size(),
//                reservationOrder.getRemainingQuantity().getValue());

        return new MatchedContext<>(trades, reservationOrder);
    }

}
