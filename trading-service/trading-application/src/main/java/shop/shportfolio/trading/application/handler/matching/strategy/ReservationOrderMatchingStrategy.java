package shop.shportfolio.trading.application.handler.matching.strategy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.valueobject.MessageType;
import shop.shportfolio.trading.application.dto.context.TradeMatchingContext;
import shop.shportfolio.trading.application.handler.UserBalanceHandler;
import shop.shportfolio.trading.application.handler.matching.OrderExecutionChecker;
import shop.shportfolio.trading.application.handler.matching.OrderMatchProcessor;
import shop.shportfolio.trading.application.ports.output.redis.TradingOrderRedisPort;
import shop.shportfolio.trading.application.ports.output.repository.TradingOrderRepositoryPort;
import shop.shportfolio.trading.application.handler.matching.FeeRateResolver;
import shop.shportfolio.trading.application.support.RedisKeyPrefix;
import shop.shportfolio.trading.domain.entity.ReservationOrder;
import shop.shportfolio.trading.domain.entity.Order;
import shop.shportfolio.trading.domain.entity.orderbook.OrderBook;
import shop.shportfolio.trading.domain.entity.userbalance.UserBalance;
import shop.shportfolio.trading.domain.event.UserBalanceUpdatedEvent;
import shop.shportfolio.trading.domain.valueobject.OrderType;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collections;

@Slf4j
@Component
public class ReservationOrderMatchingStrategy implements OrderMatchingStrategy<ReservationOrder> {

    private final FeeRateResolver feeRateResolver;
    private final OrderExecutionChecker executionChecker;
    private final UserBalanceHandler userBalanceHandler;
    private final OrderMatchProcessor matchProcessor;

    private final TradingOrderRepositoryPort tradingOrderRepositoryPort;
    private final TradingOrderRedisPort tradingOrderRedisPort;

    public ReservationOrderMatchingStrategy(FeeRateResolver feeRateResolver, OrderExecutionChecker executionChecker,
                                            UserBalanceHandler userBalanceHandler, OrderMatchProcessor matchProcessor,
                                            TradingOrderRepositoryPort tradingOrderRepositoryPort,
                                            TradingOrderRedisPort tradingOrderRedisPort) {
        this.feeRateResolver = feeRateResolver;
        this.executionChecker = executionChecker;
        this.userBalanceHandler = userBalanceHandler;
        this.matchProcessor = matchProcessor;
        this.tradingOrderRepositoryPort = tradingOrderRepositoryPort;
        this.tradingOrderRedisPort = tradingOrderRedisPort;
    }

    @Override
    public boolean supports(Order order) {
        return OrderType.RESERVATION.equals(order.getOrderType());
    }

    @Override
    public TradeMatchingContext match(OrderBook orderBook, ReservationOrder reservationOrder) {
        final String orderId = reservationOrder.getId().getValue();

        if (executionChecker.isExpired(reservationOrder)) {
            log.info("[ReservationOrder] orderId={} expired before matching", orderId);
            UserBalanceUpdatedEvent event = clearMinorLockedBalance(reservationOrder);
            return new TradeMatchingContext(Collections.emptyList(), event);
        }

        var feeRate = feeRateResolver.resolve(reservationOrder.getUserId(), reservationOrder.getOrderSide());

        log.info("[ReservationOrder] Start matching: orderId={}, userId={}, RemainingQty={}",
                orderId, reservationOrder.getUserId().getValue(), reservationOrder.getRemainingQuantity().getValue());

        var trades = matchProcessor.processReservation(orderBook, reservationOrder, feeRate, executionChecker);

        log.info("[ReservationOrder] After matching: orderId={}, FilledQty={}, RemainingQty={}",
                orderId, reservationOrder.getQuantity().getValue(), reservationOrder.getRemainingQuantity().getValue());

        if (reservationOrder.isFilled()) {
            tradingOrderRedisPort.deleteReservationOrder(
                    RedisKeyPrefix.reservation(reservationOrder.getMarketId().getValue(), orderId)
            );
            tradingOrderRepositoryPort.saveReservationOrder(reservationOrder);
            log.info("[ReservationOrder] Fully filled and removed from Redis: orderId={}", orderId);
        } else if (!executionChecker.isExpired(reservationOrder)) {
            tradingOrderRedisPort.saveReservationOrder(
                    RedisKeyPrefix.reservation(reservationOrder.getMarketId().getValue(), orderId),
                    reservationOrder
            );
            log.info("[ReservationOrder] Partially/unfilled → saved to Redis/DB: orderId={}, RemainingQty={}",
                    orderId, reservationOrder.getRemainingQuantity().getValue());
        } else {
            log.info("[ReservationOrder] Expired after matching, not saved: orderId={}", orderId);
        }

        UserBalanceUpdatedEvent userBalanceUpdatedEvent = clearMinorLockedBalance(reservationOrder);

        log.info("[ReservationOrder] Matching complete: orderId={}, userId={}, TradesCount={}",
                orderId, reservationOrder.getUserId().getValue(), trades.size());

        return new TradeMatchingContext(trades, userBalanceUpdatedEvent);
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
}
