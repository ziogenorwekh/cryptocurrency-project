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
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.Order;
import shop.shportfolio.trading.domain.entity.orderbook.OrderBook;
import shop.shportfolio.trading.domain.entity.userbalance.LockBalance;
import shop.shportfolio.trading.domain.entity.userbalance.UserBalance;
import shop.shportfolio.trading.domain.event.UserBalanceUpdatedEvent;
import shop.shportfolio.trading.domain.valueobject.OrderType;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.*;

@Slf4j
@Component
public class LimitOrderMatchingStrategy implements OrderMatchingStrategy<LimitOrder> {

    private final FeeRateResolver feeRateResolver;
    private final UserBalanceHandler userBalanceHandler;
    private final OrderExecutionChecker executionChecker;
    private final OrderMatchProcessor matchProcessor;
    private final TradingOrderRepositoryPort tradingOrderRepository;
    private final TradingOrderRedisPort tradingOrderRedisPort;

    public LimitOrderMatchingStrategy(
            FeeRateResolver feeRateResolver,
            UserBalanceHandler userBalanceHandler,
            OrderExecutionChecker executionChecker,
            OrderMatchProcessor matchProcessor,
            TradingOrderRepositoryPort tradingOrderRepository,
            TradingOrderRedisPort tradingOrderRedisPort
    ) {
        this.feeRateResolver = feeRateResolver;
        this.userBalanceHandler = userBalanceHandler;
        this.executionChecker = executionChecker;
        this.matchProcessor = matchProcessor;
        this.tradingOrderRepository = tradingOrderRepository;
        this.tradingOrderRedisPort = tradingOrderRedisPort;
    }

    @Override
    public boolean supports(Order order) {
        return OrderType.LIMIT.equals(order.getOrderType());
    }

    @Override
    public TradeMatchingContext match(OrderBook orderBook, LimitOrder limitOrder) {
        final String orderId = limitOrder.getId().getValue();

        var feeRate = feeRateResolver.resolve(limitOrder.getUserId(), limitOrder.getOrderSide());
        var userBalance = userBalanceHandler.findUserBalanceByUserId(limitOrder.getUserId());

        log.info("[{}] Start matching limit order: RemainingQty={}", orderId, limitOrder.getRemainingQuantity().getValue());

        var trades = matchProcessor.processLimitOrder(orderBook, limitOrder, feeRate, userBalance, executionChecker);

        if (limitOrder.isFilled()) {
            tradingOrderRedisPort.deleteLimitOrder(
                    RedisKeyPrefix.limit(limitOrder.getMarketId().getValue(), orderId)
            );
            tradingOrderRepository.saveLimitOrder(limitOrder);
            log.info("[{}] Limit order fully filled", orderId);
        } else {
            tradingOrderRepository.saveLimitOrder(limitOrder);
            tradingOrderRedisPort.saveLimitOrder(
                    RedisKeyPrefix.limit(limitOrder.getMarketId().getValue(), orderId),
                    limitOrder
            );
            log.info("[{}] Limit order partially/unfilled → saved", orderId);
        }
        UserBalanceUpdatedEvent userBalanceUpdatedEvent = clearMinorLockedBalance(userBalance, limitOrder);

        return new TradeMatchingContext(trades, userBalanceUpdatedEvent);
    }

    private UserBalanceUpdatedEvent clearMinorLockedBalance(UserBalance userBalance, LimitOrder limitOrder) {
        return userBalance.getLockBalances().stream()
                .filter(lockBalance -> lockBalance.getId().equals(limitOrder.getId()))
                .findAny()
                .filter(lockBalance -> !limitOrder.isOpen())
                .map(lockBalance -> {
                    log.info("locked balance for remaining Money: {}", lockBalance.getLockedAmount().getValue());
                    return userBalanceHandler.finalizeLockedAmount(userBalance, lockBalance);
                })
                .orElse(new UserBalanceUpdatedEvent(userBalance, MessageType.UPDATE, ZonedDateTime.now(ZoneOffset.UTC)));
    }
}
