package shop.shportfolio.trading.application.handler.matching.strategy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.trading.application.dto.context.TradeMatchingContext;
import shop.shportfolio.trading.application.handler.UserBalanceHandler;
import shop.shportfolio.trading.application.handler.matching.OrderMatchProcessor;
import shop.shportfolio.trading.application.ports.output.repository.TradingOrderRepositoryPort;
import shop.shportfolio.trading.application.handler.matching.FeeRateResolver;
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

    public MarketOrderMatchingStrategy(
            FeeRateResolver feeRateResolver,
            UserBalanceHandler userBalanceHandler,
            OrderMatchProcessor matchProcessor,
            TradingOrderRepositoryPort tradingOrderRepository
    ) {
        this.feeRateResolver = feeRateResolver;
        this.userBalanceHandler = userBalanceHandler;
        this.matchProcessor = matchProcessor;
        this.tradingOrderRepository = tradingOrderRepository;
    }

    @Override
    public boolean supports(Order order) {
        return OrderType.MARKET.equals(order.getOrderType());
    }

    @Override
    public TradeMatchingContext match(OrderBook orderBook, MarketOrder marketOrder) {
        List<TradeCreatedEvent> trades = new ArrayList<>();

        UserBalance userBalance = userBalanceHandler.findUserBalanceByUserId(marketOrder.getUserId());

        NavigableMap<TickPrice, PriceLevel> priceLevels = marketOrder.isBuyOrder()
                ? orderBook.getSellPriceLevels()
                : orderBook.getBuyPriceLevels();

        FeeRate feeRate = feeRateResolver.resolve(marketOrder.getUserId(), marketOrder.getOrderSide());

        Iterator<Map.Entry<TickPrice, PriceLevel>> iterator = priceLevels.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<TickPrice, PriceLevel> entry = iterator.next();
            PriceLevel priceLevel = entry.getValue();

            trades.addAll(matchProcessor.processMarketOrder(
                    marketOrder, priceLevel, feeRate, userBalance));

            if (priceLevel.isEmpty()) {
                iterator.remove();
            }

            if (marketOrder.isFilled()) {
                tradingOrderRepository.saveMarketOrder(marketOrder);
                break;
            }
        }

        if (marketOrder.isUnfilled()) {
            marketOrder.cancel();
            tradingOrderRepository.saveMarketOrder(marketOrder);
        }
        UserBalanceUpdatedEvent userBalanceUpdatedEvent = clearMinorLockedBalance(userBalance, marketOrder);
        log.info("OrderBook instance hash: {}", System.identityHashCode(orderBook));
        log.info("MarketOrder {} has been successfully processed. and userId is : {}",
                marketOrder.getId().getValue(), marketOrder.getUserId().getValue());
        return new TradeMatchingContext(trades, userBalanceUpdatedEvent);
    }


    private UserBalanceUpdatedEvent clearMinorLockedBalance(UserBalance userBalance, MarketOrder marketOrder) {
        return userBalance.getLockBalances().stream()
                .filter(lockBalance -> lockBalance.getId().equals(marketOrder.getId()))
                .findAny()
                .filter(lockBalance -> marketOrder.isFilled() || marketOrder.getRemainingPrice().isZero())
                .map(lockBalance -> {
                    log.info("locked balance for remaining Money: {}", lockBalance.getLockedAmount().getValue());
                    return userBalanceHandler.finalizeLockedAmount(userBalance, lockBalance);
                })
                .orElse(new UserBalanceUpdatedEvent(userBalance,MessageType.UPDATE, ZonedDateTime.now(ZoneOffset.UTC)));
    }

}
