package shop.shportfolio.trading.application.handler.matching.strategy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import shop.shportfolio.trading.application.handler.UserBalanceHandler;
import shop.shportfolio.trading.application.handler.matching.OrderExecutionChecker;
import shop.shportfolio.trading.application.handler.matching.OrderMatchProcessor;
import shop.shportfolio.trading.application.ports.output.redis.TradingOrderRedisPort;
import shop.shportfolio.trading.application.ports.output.repository.TradingOrderRepositoryPort;
import shop.shportfolio.trading.application.support.FeeRateResolver;
import shop.shportfolio.trading.application.support.RedisKeyPrefix;
import shop.shportfolio.trading.domain.entity.Order;
import shop.shportfolio.trading.domain.entity.ReservationOrder;
import shop.shportfolio.trading.domain.entity.orderbook.OrderBook;
import shop.shportfolio.trading.domain.event.TradingRecordedEvent;
import shop.shportfolio.trading.domain.valueobject.OrderType;

import java.util.Collections;
import java.util.List;

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
    public List<TradingRecordedEvent> match(OrderBook orderBook, ReservationOrder reservationOrder) {
        final String orderId = reservationOrder.getId().getValue();

        if (executionChecker.isExpired(reservationOrder)) {
            log.info("[{}] Reservation order expired before matching", orderId);
            return Collections.emptyList();
        }

        var feeRate = feeRateResolver.resolve(reservationOrder.getUserId(), reservationOrder.getOrderSide());
        var userBalance = userBalanceHandler.loadOrThrow(reservationOrder.getUserId());

        log.info("[{}] Start matching reservation order: RemainingQty={}", orderId,
                reservationOrder.getRemainingQuantity().getValue());

        var trades = matchProcessor.processReservation(orderBook, reservationOrder, feeRate, userBalance, executionChecker);

        if (reservationOrder.isFilled()) {
            tradingOrderRepositoryPort.saveReservationOrder(reservationOrder);
            log.info("[{}] Reservation order fully filled", orderId);
        } else if (!executionChecker.isExpired(reservationOrder)) {
            tradingOrderRedisPort.saveReservationOrder(
                    RedisKeyPrefix.reservation(
                            reservationOrder.getMarketId().getValue(),
                            orderId),
                    reservationOrder);
            log.info("[{}] Reservation order partially/unfilled → saved", orderId);
        } else {
            log.info("[{}] Reservation order expired after matching, not saved", orderId);
        }

        userBalanceHandler.saveUserBalance(userBalance);

        return trades;
    }
}
