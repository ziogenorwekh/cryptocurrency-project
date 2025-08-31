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
    public MatchedContext<ReservationOrder> match(MatchingOrderBook matchingOrderBook, ReservationOrder reservationOrder) {
        List<PredictedTradeCreatedEvent> trades = new ArrayList<>();

        // 전체 OrderBook 상태 로그
        log.info("[Reservation] OrderBook levels: buy={}, sell={}",
                matchingOrderBook.getBuyPriceLevels().size(),
                matchingOrderBook.getSellPriceLevels().size());

        // 새 주문 진입 로그
        log.info("[Reservation] New reservation order received: id={}, user={}, side={}, remainingQty={}, market={}",
                reservationOrder.getId().getValue(),
                reservationOrder.getUserId().getValue(),
                reservationOrder.getOrderSide().getValue(),
                reservationOrder.getRemainingQuantity().getValue(),
                reservationOrder.getMarketId().getValue());

        NavigableMap<TickPrice, MatchingPriceLevel> counterPriceLevels = reservationOrder.isBuyOrder()
                ? matchingOrderBook.getSellPriceLevels()
                : matchingOrderBook.getBuyPriceLevels();

        for (Map.Entry<TickPrice, MatchingPriceLevel> entry : counterPriceLevels.entrySet()) {
            TickPrice tickPrice = entry.getKey();
            MatchingPriceLevel matchingPriceLevel = entry.getValue();
            OrderPrice executionPrice = new OrderPrice(tickPrice.getValue());

            // 가격 레벨 진입 로그
            log.debug("[Reservation] Checking price level {} with restingOrders={}, takerRemaining={}",
                    tickPrice.getValue(),
                    matchingPriceLevel.getOrders().size(),
                    reservationOrder.getRemainingQuantity().getValue());

            if (!reservationOrder.isPriceMatch(executionPrice)) {
                log.info("[Reservation] Price {} does not match. Skipping.", executionPrice.getValue());
                continue;
            }

            while (reservationOrder.isUnfilled() && !matchingPriceLevel.isEmpty()) {
                Order restingOrder = matchingPriceLevel.peekOrder();

                // 체결 직전 상태 로그
                log.debug("[Reservation] Before trade: takerRemaining={}, restingRemaining={}",
                        reservationOrder.getRemainingQuantity().getValue(),
                        restingOrder.getRemainingQuantity().getValue());

                if (!reservationOrder.canExecute(restingOrder.getOrderPrice(), LocalDateTime.now(ZoneOffset.UTC))) {
                    log.info("[Reservation] Execution condition not met for restingOrder {}. Stopping.", restingOrder.getId().getValue());
                    break;
                }
                if (reservationOrder.isExpired(LocalDateTime.now(ZoneOffset.UTC))) {
                    log.info("[Reservation] Reservation order expired during matching.");
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

                // 체결 로그
                log.info("[Reservation] Trade executed: takerId={}, makerId={}, qty={}, price={}, takerRemaining(before)={}, makerRemaining(before)={}",
                        reservationOrder.getId().getValue(),
                        restingOrder.getId().getValue(),
                        execQty.getValue(),
                        executionPrice.getValue(),
                        reservationOrder.getRemainingQuantity().getValue(),
                        restingOrder.getRemainingQuantity().getValue());

                if (restingOrder.isFilled()) {
                    log.debug("[Reservation] Resting order {} filled and removed from PriceLevel", restingOrder.getId().getValue());
                    matchingPriceLevel.popOrder();
                    log.debug("[Reservation] Remaining orders in PriceLevel: {}", matchingPriceLevel.getOrders().size());
                }

                if (reservationOrder.isFilled()) {
                    log.debug("[Reservation] Reservation order fully filled.");
                    break;
                }
            }

            // tickPrice 레벨 소진 로그
            if (matchingPriceLevel.isEmpty()) {
                log.debug("[Reservation] PriceLevel empty after matching: tickPrice={}", tickPrice.getValue());
            }

            // tickPrice별 매칭 후 남은 상태 로그
            log.debug("[Reservation] After matching tick {}: remaining takerQty={}, remaining restingOrders={}",
                    tickPrice.getValue(),
                    reservationOrder.getRemainingQuantity().getValue(),
                    matchingPriceLevel.getOrders().size());

            if (reservationOrder.isFilled()) break;
        }

        // 최종 매칭 결과 로그
        log.info("[Reservation] Matching finished: takerId={}, totalTrades={}, finalRemaining={}",
                reservationOrder.getId().getValue(),
                trades.size(),
                reservationOrder.getRemainingQuantity().getValue());

        return new MatchedContext<>(trades, reservationOrder);
    }
}
