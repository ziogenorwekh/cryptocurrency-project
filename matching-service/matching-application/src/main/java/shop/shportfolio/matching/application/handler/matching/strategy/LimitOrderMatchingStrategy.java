package shop.shportfolio.matching.application.handler.matching.strategy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.valueobject.OrderPrice;
import shop.shportfolio.common.domain.valueobject.Quantity;
import shop.shportfolio.common.domain.valueobject.TransactionType;
import shop.shportfolio.matching.application.dto.order.MatchedContext;
import shop.shportfolio.matching.domain.MatchingDomainService;
import shop.shportfolio.matching.domain.event.PredictedTradeCreatedEvent;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.Order;
import shop.shportfolio.trading.domain.entity.orderbook.OrderBook;
import shop.shportfolio.trading.domain.entity.orderbook.PriceLevel;
import shop.shportfolio.trading.domain.valueobject.OrderType;
import shop.shportfolio.trading.domain.valueobject.TickPrice;
import java.util.*;

@Slf4j
@Component
public class LimitOrderMatchingStrategy implements OrderMatchingStrategy<LimitOrder> {

    private final MatchingDomainService matchingDomainService;

    @Autowired
    public LimitOrderMatchingStrategy(MatchingDomainService matchingDomainService) {
        this.matchingDomainService = matchingDomainService;
    }

    @Override
    public boolean supports(Order order) {
        return OrderType.LIMIT.equals(order.getOrderType());
    }

    @Override
    public MatchedContext<LimitOrder> match(OrderBook orderBook, LimitOrder limitOrder) {
        List<PredictedTradeCreatedEvent> trades = new ArrayList<>();
        NavigableMap<TickPrice, PriceLevel> counterPriceLevels = limitOrder.isBuyOrder()
                ? orderBook.getSellPriceLevels()
                : orderBook.getBuyPriceLevels();

        for (Map.Entry<TickPrice, PriceLevel> entry : counterPriceLevels.entrySet()) {
            TickPrice tickPrice = entry.getKey();
            PriceLevel priceLevel = entry.getValue();
            OrderPrice executionPrice = new OrderPrice(tickPrice.getValue());

            log.info("[LimitOrder] Checking price level {} for matching", tickPrice.getValue());
            if (!limitOrder.canMatchPrice(limitOrder,tickPrice)) break;

            while (limitOrder.isUnfilled() && !priceLevel.isEmpty()) {
                Order restingOrder = priceLevel.peekOrder();
                Quantity execQty = limitOrder.applyTrade(restingOrder.getRemainingQuantity());
                restingOrder.applyTrade(execQty);

                PredictedTradeCreatedEvent createdEvent = matchingDomainService.createPredictedTrade(
                        limitOrder.getMarketId(),
                        limitOrder.getUserId(),
                        limitOrder.getId(),
                        restingOrder.getId(),
                        executionPrice,
                        execQty,
                        limitOrder.isBuyOrder() ? TransactionType.TRADE_BUY : TransactionType.TRADE_SELL
                );

                trades.add(createdEvent);
                log.info("[LimitOrder] Executed trade: qty={}, price={}", execQty.getValue(), executionPrice.getValue());

                if (restingOrder.isFilled()) {
                    log.info("[LimitOrder] Resting order {} filled. Removing from PriceLevel", restingOrder.getId().getValue());
                    priceLevel.popOrder();
                }
            }

            if (limitOrder.isFilled()) break;
        }
        return new MatchedContext<>(trades, limitOrder);
    }
}
