package shop.shportfolio.trading.application.handler.matching.strategy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.trading.application.dto.context.TradeMatchingContext;
import shop.shportfolio.trading.application.handler.UserBalanceHandler;
import shop.shportfolio.trading.application.handler.matching.OrderMatchProcessor;
import shop.shportfolio.trading.application.ports.output.redis.TradingOrderRedisPort;
import shop.shportfolio.trading.application.ports.output.repository.TradingOrderRepositoryPort;
import shop.shportfolio.trading.application.handler.matching.FeeRateResolver;
import shop.shportfolio.trading.application.support.RedisKeyPrefix;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.MarketOrder;
import shop.shportfolio.trading.domain.entity.Order;
import shop.shportfolio.trading.domain.entity.orderbook.OrderBook;
import shop.shportfolio.trading.domain.entity.orderbook.PriceLevel;
import shop.shportfolio.trading.domain.entity.userbalance.LockBalance;
import shop.shportfolio.trading.domain.entity.userbalance.UserBalance;
import shop.shportfolio.trading.domain.event.TradeCreatedEvent;
import shop.shportfolio.trading.domain.event.UserBalanceUpdatedEvent;
import shop.shportfolio.trading.domain.valueobject.OrderType;
import shop.shportfolio.trading.domain.valueobject.TickPrice;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.*;

@Slf4j
@Component
public class MarketOrderMatchingStrategy implements OrderMatchingStrategy<MarketOrder> {

    private final FeeRateResolver feeRateResolver;
    private final UserBalanceHandler userBalanceHandler;
    private final OrderMatchProcessor matchProcessor;
    private final TradingOrderRepositoryPort tradingOrderRepository;
    private final TradingOrderRedisPort tradingOrderRedisPort;
    public MarketOrderMatchingStrategy(
            FeeRateResolver feeRateResolver,
            UserBalanceHandler userBalanceHandler,
            OrderMatchProcessor matchProcessor,
            TradingOrderRepositoryPort tradingOrderRepository,
            TradingOrderRedisPort tradingOrderRedisPort
    ) {
        this.feeRateResolver = feeRateResolver;
        this.userBalanceHandler = userBalanceHandler;
        this.matchProcessor = matchProcessor;
        this.tradingOrderRepository = tradingOrderRepository;
        this.tradingOrderRedisPort = tradingOrderRedisPort;
    }

    @Override
    public boolean supports(Order order) {
        return OrderType.MARKET.equals(order.getOrderType());
    }

    @Override
    public TradeMatchingContext match(OrderBook orderBook, MarketOrder marketOrder) {
        List<TradeCreatedEvent> trades = new ArrayList<>();
        NavigableMap<TickPrice, PriceLevel> priceLevels = marketOrder.isBuyOrder()
                ? orderBook.getSellPriceLevels()
                : orderBook.getBuyPriceLevels();

        FeeRate feeRate = feeRateResolver.resolve(marketOrder.getUserId(), marketOrder.getOrderSide());

        log.info("[MarketOrder] Start matching: marketOrderId={}, userId={}, isBuy={}",
                marketOrder.getId().getValue(), marketOrder.getUserId().getValue(), marketOrder.isBuyOrder());

        Iterator<Map.Entry<TickPrice, PriceLevel>> iterator = priceLevels.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<TickPrice, PriceLevel> entry = iterator.next();
            PriceLevel priceLevel = entry.getValue();

            log.info("[MarketOrder] Processing PriceLevel: price={}, ordersCount={}",
                    entry.getKey().getValue(), priceLevel.getOrders().size());

            trades.addAll(matchProcessor.processMarketOrder(
                    marketOrder, priceLevel, feeRate));

            log.info("[MarketOrder] After matching PriceLevel: price={}, remainingOrders={}",
                    entry.getKey().getValue(), priceLevel.getOrders().size());

            if (priceLevel.isEmpty()) {
                iterator.remove();
                log.info("[MarketOrder] Removed empty PriceLevel: price={}", entry.getKey().getValue());
            }

            if (marketOrder.isFilled()) {
                tradingOrderRepository.saveMarketOrder(marketOrder);
                log.info("[MarketOrder] Fully filled: marketOrderId={}", marketOrder.getId().getValue());
                break;
            }
        }

        if (marketOrder.isUnfilled()) {
            marketOrder.cancel();
            tradingOrderRepository.saveMarketOrder(marketOrder);

            log.info("[MarketOrder] Partially unfilled and canceled: marketOrderId={}, remainingPrice={}",
                    marketOrder.getId().getValue(), marketOrder.getRemainingPrice().getValue());
        }

        UserBalanceUpdatedEvent userBalanceUpdatedEvent = clearMinorLockedBalance(marketOrder);

        log.info("[MarketOrder] MarketOrder {} processed. userId={}, remainingPrice={}",
                marketOrder.getId().getValue(), marketOrder.getUserId().getValue(), marketOrder.getRemainingPrice().getValue());
        tradingOrderRedisPort.deleteMarketOrder(RedisKeyPrefix.market(marketOrder.getMarketId().getValue(),
                marketOrder.getId().getValue()));
        return new TradeMatchingContext(trades, userBalanceUpdatedEvent);
    }

    private UserBalanceUpdatedEvent clearMinorLockedBalance(MarketOrder marketOrder) {
        UserBalance userBalance = userBalanceHandler.findUserBalanceByUserId(marketOrder.getUserId());
        return userBalance.getLockBalances().stream()
                .filter(lockBalance -> lockBalance.getId().equals(marketOrder.getId()))
                .findAny()
                .filter(lockBalance -> marketOrder.getRemainingPrice().isPositive())
                .map(lockBalance -> {
                    log.info("[MarketOrder] Locked balance for remaining money: {}", lockBalance.getLockedAmount().getValue());
                    return userBalanceHandler.finalizeLockedAmount(userBalance, lockBalance);
                })
                .orElseGet(() -> {
                    log.info("[MarketOrder] No locked balance to clear for marketOrderId={}", marketOrder.getId().getValue());
                    return new UserBalanceUpdatedEvent(userBalance, MessageType.UPDATE, ZonedDateTime.now(ZoneOffset.UTC));
                });
    }

}
