package shop.shportfolio.matching.application.handler.matching.strategy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.valueobject.FeeAmount;
import shop.shportfolio.common.domain.valueobject.OrderPrice;
import shop.shportfolio.common.domain.valueobject.Quantity;
import shop.shportfolio.common.domain.valueobject.TransactionType;
import shop.shportfolio.matching.application.dto.order.MatchedContext;
import shop.shportfolio.matching.domain.MatchingDomainService;
import shop.shportfolio.matching.domain.event.PredictedTradeCreatedEvent;
import shop.shportfolio.trading.domain.entity.Order;
import shop.shportfolio.trading.domain.entity.ReservationOrder;
import shop.shportfolio.trading.domain.entity.orderbook.OrderBook;
import shop.shportfolio.trading.domain.entity.orderbook.PriceLevel;
import shop.shportfolio.trading.domain.event.TradeCreatedEvent;
import shop.shportfolio.trading.domain.valueobject.OrderType;
import shop.shportfolio.trading.domain.valueobject.TickPrice;
import shop.shportfolio.trading.domain.valueobject.TradeId;

import java.math.BigDecimal;
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
    public MatchedContext<ReservationOrder> match(OrderBook orderBook, ReservationOrder reservationOrder) {
        List<PredictedTradeCreatedEvent> trades = new ArrayList<>();
        NavigableMap<TickPrice, PriceLevel> counterPriceLevels = reservationOrder.isBuyOrder()
                ? orderBook.getSellPriceLevels()
                : orderBook.getBuyPriceLevels();
        log.info("[Reservation] Start matching reservation order {}: RemainingQty={}",
                reservationOrder.getId().getValue(), reservationOrder.getRemainingQuantity().getValue());

        for (Map.Entry<TickPrice, PriceLevel> entry : counterPriceLevels.entrySet()) {
            TickPrice tickPrice = entry.getKey();
            PriceLevel priceLevel = entry.getValue();
            OrderPrice executionPrice = new OrderPrice(tickPrice.getValue());

            log.info("[Reservation] Checking price level {} for execution", tickPrice.getValue());

            if (!reservationOrder.isPriceMatch(executionPrice)) {
                log.info("[Reservation] Price {} does not match. Skipping.", executionPrice.getValue());
                continue;
            }

            while (reservationOrder.isUnfilled() && !priceLevel.isEmpty()) {
                Order restingOrder = priceLevel.peekOrder();
                log.info("[Reservation] Evaluating restingOrder {} qty={} price={}",
                        restingOrder.getId().getValue(),
                        restingOrder.getRemainingQuantity().getValue(),
                        restingOrder.getOrderPrice().getValue());

                if (!reservationOrder.canExecute(restingOrder.getOrderPrice(), LocalDateTime.now(ZoneOffset.UTC))) {
                    log.info("[Reservation] Execution condition not met. Stopping.");
                    break;
                }
                if (reservationOrder.isExpired(LocalDateTime.now(ZoneOffset.UTC))) {
                    log.info("[Reservation] Reservation expired during matching.");
                    break;
                }

                Quantity execQty = reservationOrder.applyTrade(restingOrder.getRemainingQuantity());
                restingOrder.applyTrade(execQty);

                PredictedTradeCreatedEvent createdEvent = matchingDomainService.createPredictedTrade(
                        reservationOrder.getMarketId(),
                        reservationOrder.getUserId(),
                        reservationOrder.getId(),
                        restingOrder.getId(),
                        executionPrice,
                        execQty,
                        reservationOrder.isBuyOrder() ? TransactionType.TRADE_BUY : TransactionType.TRADE_SELL
                );

                trades.add(createdEvent);
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

        return new MatchedContext<>(trades, reservationOrder);
    }
}
