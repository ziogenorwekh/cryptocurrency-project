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
import shop.shportfolio.trading.domain.entity.MarketOrder;
import shop.shportfolio.trading.domain.entity.Order;
import shop.shportfolio.trading.domain.entity.orderbook.OrderBook;
import shop.shportfolio.trading.domain.entity.orderbook.PriceLevel;
import shop.shportfolio.trading.domain.valueobject.OrderType;
import shop.shportfolio.trading.domain.valueobject.TickPrice;

import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Component
public class MarketOrderMatchingStrategy implements OrderMatchingStrategy<MarketOrder> {

    private final MatchingDomainService matchingDomainService;

    @Autowired
    public MarketOrderMatchingStrategy(MatchingDomainService matchingDomainService) {
        this.matchingDomainService = matchingDomainService;
    }

    @Override
    public boolean supports(Order order) {
        return OrderType.MARKET.equals(order.getOrderType());
    }

    @Override
    public MatchedContext<MarketOrder> match(OrderBook orderBook, MarketOrder marketOrder) {

        List<PredictedTradeCreatedEvent> trades = new ArrayList<>();

        // 전체 OrderBook 상태 로그
        log.info("[MarketOrder] OrderBook levels: buy={}, sell={}",
                orderBook.getBuyPriceLevels().size(),
                orderBook.getSellPriceLevels().size());

        // 새 주문 진입 로그
        log.info("[MarketOrder] New order received: id={}, user={}, side={}, remainingPrice={}, market={}",
                marketOrder.getId().getValue(),
                marketOrder.getUserId().getValue(),
                marketOrder.getOrderSide().getValue(),
                marketOrder.getRemainingPrice().getValue(),
                marketOrder.getMarketId().getValue());

        NavigableMap<TickPrice, PriceLevel> counterPriceLevels = marketOrder.isBuyOrder()
                ? orderBook.getSellPriceLevels()
                : orderBook.getBuyPriceLevels();

        for (Map.Entry<TickPrice, PriceLevel> entry : counterPriceLevels.entrySet()) {

            TickPrice tickPrice = entry.getKey();
            PriceLevel priceLevel = entry.getValue();

            // 가격 레벨 진입 로그
            log.debug("[MarketOrder] Checking price level {} with restingOrders={}, takerRemainingPrice={}",
                    tickPrice.getValue(),
                    priceLevel.getOrders().size(),
                    marketOrder.getRemainingPrice().getValue());

            while (marketOrder.isUnfilled() && !priceLevel.isEmpty()) {
                Order restingOrder = priceLevel.peekOrder();

                // 체결 직전 상태 로그
                log.debug("[MarketOrder] Before trade: takerRemainingPrice={}, restingRemainingQty={}",
                        marketOrder.getRemainingPrice().getValue(),
                        restingOrder.getRemainingQuantity().getValue());

                BigDecimal maxQtyByPrice = marketOrder.getRemainingPrice().getValue()
                        .divide(restingOrder.getOrderPrice().getValue(), 8, BigDecimal.ROUND_DOWN);
                Quantity execQty = Quantity.of(maxQtyByPrice.min(restingOrder.getRemainingQuantity().getValue()));

                if (execQty.isZero()) break;

                marketOrder.applyMarketOrderTrade(restingOrder.getOrderPrice(), execQty);
                restingOrder.applyTrade(execQty);
                OrderPrice executionPrice = restingOrder.getOrderPrice();

                PredictedTradeCreatedEvent createdEvent = matchingDomainService.createPredictedTrade(
                        marketOrder.getMarketId(),
                        marketOrder.getUserId(),
                        marketOrder.getId(),
                        restingOrder.getId(),
                        executionPrice,
                        execQty,
                        marketOrder.isBuyOrder() ? TransactionType.TRADE_BUY : TransactionType.TRADE_SELL
                );

                trades.add(createdEvent);

                // 체결 로그
                log.info("[MarketOrder] Trade executed: takerId={}, makerId={}, price={}, execQty={}, takerRemainingPrice(before)={}, makerRemaining(before)={}",
                        marketOrder.getId().getValue(),
                        restingOrder.getId().getValue(),
                        executionPrice.getValue(),
                        execQty.getValue(),
                        marketOrder.getRemainingPrice().getValue(),
                        restingOrder.getRemainingQuantity().getValue()
                );

                if (restingOrder.isFilled()) {
                    log.debug("[MarketOrder] Resting order filled and removed: orderId={}, userId={}",
                            restingOrder.getId().getValue(),
                            restingOrder.getUserId().getValue());
                    priceLevel.popOrder();
                    log.debug("[MarketOrder] Remaining orders in PriceLevel: {}", priceLevel.getOrders().size());
                }

                if (marketOrder.getRemainingPrice().isZero()) {
                    log.debug("[MarketOrder] Market order {} fully matched", marketOrder.getId().getValue());
                    break;
                }
            }

            // tickPrice 레벨 소진 로그
            if (priceLevel.isEmpty()) {
                log.debug("[MarketOrder] PriceLevel empty after matching: tickPrice={}", tickPrice.getValue());
            }

            // tickPrice별 매칭 후 남은 상태 로그
            log.debug("[MarketOrder] After matching tick {}: remaining takerPrice={}, remaining restingOrders={}",
                    tickPrice.getValue(),
                    marketOrder.getRemainingPrice().getValue(),
                    priceLevel.getOrders().size());

            if (marketOrder.getRemainingPrice().isZero()) break;
        }

        // 최종 매칭 결과 로그
        log.info("[MarketOrder] Matching finished: takerId={}, totalTrades={}, finalRemainingPrice={}",
                marketOrder.getId().getValue(),
                trades.size(),
                marketOrder.getRemainingPrice().getValue());

        return new MatchedContext<>(trades, marketOrder);
    }
}
