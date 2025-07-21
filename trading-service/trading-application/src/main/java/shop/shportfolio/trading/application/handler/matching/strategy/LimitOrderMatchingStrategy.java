package shop.shportfolio.trading.application.handler.matching.strategy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.valueobject.*;
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
import shop.shportfolio.trading.domain.entity.orderbook.PriceLevel;
import shop.shportfolio.trading.domain.entity.userbalance.UserBalance;
import shop.shportfolio.trading.domain.event.TradingRecordedEvent;
import shop.shportfolio.trading.domain.valueobject.OrderType;
import shop.shportfolio.trading.domain.valueobject.TickPrice;

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
    public List<TradingRecordedEvent> match(OrderBook orderBook, LimitOrder limitOrder) {
        List<TradingRecordedEvent> trades = new ArrayList<>();

        UserBalance userBalance = userBalanceHandler.findUserBalanceByUserId(limitOrder.getUserId());
        TickPrice tickPrice = TickPrice.of(
                limitOrder.getOrderPrice().getValue(),
                orderBook.getMarketItemTick().getValue()
        );

        if (!executionChecker.canMatchPrice(limitOrder, tickPrice)) {
            return trades;
        }

        PriceLevel priceLevel = getCounterPriceLevel(orderBook, limitOrder, tickPrice);
        if (priceLevel == null || priceLevel.isEmpty()) {
            return trades;
        }

        FeeRate feeRate = feeRateResolver.resolve(limitOrder.getUserId(), limitOrder.getOrderSide());

        trades.addAll(matchProcessor.processLimitOrder(limitOrder, priceLevel, tickPrice, feeRate, userBalance));

        cleanupEmptyPriceLevel(getCounterPriceLevels(orderBook, limitOrder), tickPrice);

        handleRemainingOrder(orderBook, limitOrder, tickPrice);

        userBalanceHandler.saveUserBalance(userBalance);

        return trades;
    }

    private NavigableMap<TickPrice, PriceLevel> getCounterPriceLevels(OrderBook orderBook, LimitOrder order) {
        return order.isBuyOrder() ? orderBook.getSellPriceLevels() : orderBook.getBuyPriceLevels();
    }

    private PriceLevel getCounterPriceLevel(OrderBook orderBook, LimitOrder order, TickPrice tickPrice) {
        return getCounterPriceLevels(orderBook, order).get(tickPrice);
    }

    private void cleanupEmptyPriceLevel(NavigableMap<TickPrice, PriceLevel> levels, TickPrice price) {
        PriceLevel pl = levels.get(price);
        if (pl == null || pl.isEmpty()) {
            levels.remove(price);
        }
    }

    private void handleRemainingOrder(OrderBook orderBook, LimitOrder limitOrder, TickPrice tickPrice) {
        if (limitOrder.isUnfilled()) {
            persistRemaining(limitOrder, orderBook, tickPrice);
        } else {
            deleteFilled(limitOrder);
        }
    }

    private void persistRemaining(LimitOrder limitOrder, OrderBook orderBook, TickPrice tickPrice) {
        tradingOrderRepository.saveLimitOrder(limitOrder);
        tradingOrderRedisPort.saveLimitOrder(
                RedisKeyPrefix.limit(limitOrder.getMarketId().getValue(), limitOrder.getId().getValue()),
                limitOrder
        );

        NavigableMap<TickPrice, PriceLevel> ownLevels =
                limitOrder.isBuyOrder() ? orderBook.getBuyPriceLevels() : orderBook.getSellPriceLevels();

        PriceLevel level = ownLevels.computeIfAbsent(tickPrice, k -> new PriceLevel(tickPrice));
        level.addOrder(limitOrder);

        log.info("Limit order {} partially/unfilled → added to orderbook at price {}",
                limitOrder.getId().getValue(), tickPrice.getValue());
    }

    private void deleteFilled(LimitOrder limitOrder) {
        tradingOrderRedisPort.deleteLimitOrder(
                RedisKeyPrefix.limit(limitOrder.getMarketId().getValue(), limitOrder.getId().getValue())
        );
        tradingOrderRepository.saveLimitOrder(limitOrder);
        log.info("Limit order {} fully filled", limitOrder.getId().getValue());
    }
}
