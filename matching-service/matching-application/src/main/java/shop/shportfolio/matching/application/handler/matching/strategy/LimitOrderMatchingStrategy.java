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
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.Order;
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
    public MatchedContext<LimitOrder> match(MatchingOrderBook matchingOrderBook, LimitOrder limitOrder) {
        List<PredictedTradeCreatedEvent> trades = new ArrayList<>();

        // 전체 OrderBook 상태 로그
//        log.info("[LimitOrder] OrderBook levels: buy={}, sell={}",
//                matchingOrderBook.getBuyPriceLevels().size(),
//                matchingOrderBook.getSellPriceLevels().size());

        // 새 주문 진입 로그
//        log.info("[LimitOrder] New order received: id={}, user={}, side={}, price={}, qty={}, remaining={}, market={}",
//                limitOrder.getId().getValue(),
//                limitOrder.getUserId().getValue(),
//                limitOrder.getOrderSide().getValue(),
//                limitOrder.getOrderPrice().getValue(),
//                limitOrder.getQuantity().getValue(),
//                limitOrder.getRemainingQuantity().getValue(),
//                limitOrder.getMarketId().getValue());

        NavigableMap<TickPrice, MatchingPriceLevel> counterPriceLevels = limitOrder.isBuyOrder()
                ? matchingOrderBook.getSellPriceLevels()
                : matchingOrderBook.getBuyPriceLevels();

        for (Map.Entry<TickPrice, MatchingPriceLevel> entry : counterPriceLevels.entrySet()) {
            TickPrice tickPrice = entry.getKey();
            MatchingPriceLevel matchingPriceLevel = entry.getValue();
            OrderPrice executionPrice = new OrderPrice(tickPrice.getValue());

            // 가격 레벨 진입 로그
            log.debug("[LimitOrder] Checking price level {} with restingOrders={}, takerRemaining={}",
                    tickPrice.getValue(),
                    matchingPriceLevel.getOrders().size(),
                    limitOrder.getRemainingQuantity().getValue());

            if (!limitOrder.canMatchPrice(limitOrder, tickPrice)) {
//                log.info("[LimitOrder] Cannot match price: tick={}, takerRemaining={}",
//                        tickPrice.getValue(), limitOrder.getRemainingQuantity().getValue());
                break;
            }

            while (limitOrder.isUnfilled() && !matchingPriceLevel.isEmpty()) {
                Order restingOrder = matchingPriceLevel.peekOrder();

                // 체결 직전 상태 로그
                log.debug("[LimitOrder] Before applyTrade: takerRemaining={}, restingRemaining={}",
                        limitOrder.getRemainingQuantity().getValue(),
                        restingOrder.getRemainingQuantity().getValue());

                Quantity execQty = limitOrder.applyTrade(restingOrder.getRemainingQuantity());
                restingOrder.applyTrade(execQty);

                PredictedTradeCreatedEvent createdEvent = matchingDomainService.createPredictedTrade(
                        limitOrder.getMarketId(),
                        limitOrder.getUserId(),
                        limitOrder,
                        restingOrder,
                        executionPrice,
                        execQty,
                        limitOrder.isBuyOrder() ? TransactionType.TRADE_BUY : TransactionType.TRADE_SELL
                );

                trades.add(createdEvent);

                // 체결 로그
                log.info("[LimitOrder] Trade executed: takerId={}, sellerId={}, price={}, execQty={}, takerRemaining(before)={}, makerRemaining(before)={}",
                        limitOrder.getId().getValue(),
                        restingOrder.getId().getValue(),
                        executionPrice.getValue(),
                        execQty.getValue(),
                        limitOrder.getRemainingQuantity().getValue(),
                        restingOrder.getRemainingQuantity().getValue()
                );

                if (restingOrder.isFilled()) {
                    // resting order 제거 로그
                    log.debug("[LimitOrder] Resting order filled and removed: orderId={}, userId={}",
                            restingOrder.getId().getValue(),
                            restingOrder.getUserId().getValue());
                    matchingPriceLevel.popOrder();
                }
            }

            // tickPrice 레벨 소진 로그
            if (matchingPriceLevel.isEmpty()) {
                log.debug("[LimitOrder] PriceLevel empty after matching: tickPrice={}", tickPrice.getValue());
            }

            if (limitOrder.isFilled()) {
                log.debug("[LimitOrder] Taker order filled, exiting matching loop");
                break;
            }

            // tickPrice별 매칭 후 남은 상태 로그
            log.debug("[LimitOrder] After matching tick {}: remaining taker={}, remaining restingOrders={}",
                    tickPrice.getValue(),
                    limitOrder.getRemainingQuantity().getValue(),
                    matchingPriceLevel.getOrders().size());
        }

        // 최종 매칭 결과 로그
//        log.info("[LimitOrder] Matching finished: takerId={}, totalTrades={}, finalRemaining={}, status={}",
//                limitOrder.getId().getValue(),
//                trades.size(),
//                limitOrder.getRemainingQuantity().getValue(),
//                limitOrder.getOrderStatus());

        return new MatchedContext<>(trades, limitOrder);
    }
}
