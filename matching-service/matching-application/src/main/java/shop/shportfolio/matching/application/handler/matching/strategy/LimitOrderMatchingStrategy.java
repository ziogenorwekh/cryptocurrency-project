package shop.shportfolio.matching.application.handler.matching.strategy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import shop.shportfolio.common.domain.valueobject.FeeAmount;
import shop.shportfolio.common.domain.valueobject.OrderPrice;
import shop.shportfolio.common.domain.valueobject.Quantity;
import shop.shportfolio.common.domain.valueobject.TransactionType;
import shop.shportfolio.matching.application.dto.order.MatchedContext;
import shop.shportfolio.matching.application.support.RedisKeyPrefix;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.Order;
import shop.shportfolio.trading.domain.entity.orderbook.OrderBook;
import shop.shportfolio.trading.domain.entity.orderbook.PriceLevel;
import shop.shportfolio.trading.domain.event.TradeCreatedEvent;
import shop.shportfolio.trading.domain.valueobject.OrderType;
import shop.shportfolio.trading.domain.valueobject.TickPrice;
import shop.shportfolio.trading.domain.valueobject.TradeId;

import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Component
public class LimitOrderMatchingStrategy implements OrderMatchingStrategy<LimitOrder> {

    @Override
    public boolean supports(Order order) {
        return OrderType.LIMIT.equals(order.getOrderType());
    }

    @Override
    public MatchedContext<LimitOrder> match(OrderBook orderBook, LimitOrder limitOrder) {
        List<TradeCreatedEvent> trades = new ArrayList<>();
        NavigableMap<TickPrice, PriceLevel> counterPriceLevels = limitOrder.isBuyOrder()
                ? orderBook.getSellPriceLevels()
                : orderBook.getBuyPriceLevels();


//        for (Map.Entry<TickPrice, PriceLevel> entry : counterPriceLevels.entrySet()) {
//            TickPrice tickPrice = entry.getKey();
//            PriceLevel priceLevel = entry.getValue();
//            OrderPrice executionPrice = new OrderPrice(tickPrice.getValue());
//
//            log.info("[LimitOrder] Checking price level {} for matching", tickPrice.getValue());
//            if (!executionChecker.canMatchPrice(limitOrder, tickPrice)) break;
//
//            while (limitOrder.isUnfilled() && !priceLevel.isEmpty()) {
//                Order restingOrder = priceLevel.peekOrder();
//                Quantity execQty = orderDomainService.applyOrder(limitOrder, restingOrder.getRemainingQuantity());
//                orderDomainService.applyOrder(restingOrder, execQty);
//
//                FeeAmount feeAmount = feeRate.calculateFeeAmount(executionPrice, execQty);
//
//                TradeCreatedEvent tradeEvent = tradeDomainService.createTrade(
//                        new TradeId(UUID.randomUUID()),
//                        limitOrder.getMarketId(),
//                        limitOrder.getUserId(),
//                        limitOrder.getId(),
//                        executionPrice,
//                        execQty,
//                        limitOrder.isBuyOrder() ? TransactionType.TRADE_BUY : TransactionType.TRADE_SELL,
//                        feeAmount,
//                        feeRate
//                );
//
//                if (limitOrder.isBuyOrder()) {
//                } else {
//                }
//
//                trades.add(tradeEvent);
//                log.info("[LimitOrder] Executed trade: qty={}, price={}", execQty.getValue(), executionPrice.getValue());
//
//                if (restingOrder.isFilled()) {
//                    log.info("[LimitOrder] Resting order {} filled. Removing from PriceLevel", restingOrder.getId().getValue());
//                    priceLevel.popOrder();
//                }
//            }
//
//            if (limitOrder.isFilled()) break;
//        }
//
//        return trades;
//        if (limitOrder.isFilled()) {
//            tradingOrderRedisPort.deleteLimitOrder(
//                    RedisKeyPrefix.limit(limitOrder.getMarketId().getValue(), orderId)
//            );
//            log.info("[LimitOrder] Fully filled and removed from Redis: orderId={}", orderId);
//        } else {
//            tradingOrderRedisPort.saveLimitOrder(
//                    RedisKeyPrefix.limit(limitOrder.getMarketId().getValue(), orderId),
//                    limitOrder
//            );
//            log.info("[LimitOrder] Partially/unfilled → saved to Redis/DB: orderId={}, RemainingQty={}",
//                    orderId, limitOrder.getRemainingQuantity().getValue());
//        }

    }
}
