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
import shop.shportfolio.trading.domain.entity.MarketOrder;
import shop.shportfolio.trading.domain.entity.Order;
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
    public MatchedContext<MarketOrder> match(MatchingOrderBook matchingOrderBook, MarketOrder marketOrder) {
        return marketOrder.isBuyOrder()
                ? matchBuyOrder(matchingOrderBook, marketOrder)
                : matchSellOrder(matchingOrderBook, marketOrder);
    }

    private MatchedContext<MarketOrder> matchBuyOrder(MatchingOrderBook matchingOrderBook,
                                                      MarketOrder marketOrder) {
        List<PredictedTradeCreatedEvent> trades = new ArrayList<>();

        log.info("[MarketOrder] New BUY order received: id={}, user={}, market={}",
                marketOrder.getId().getValue(),
                marketOrder.getUserId().getValue(),
                marketOrder.getMarketId().getValue());

        NavigableMap<TickPrice, MatchingPriceLevel> counterPriceLevels = matchingOrderBook.getSellPriceLevels();

        for (Map.Entry<TickPrice, MatchingPriceLevel> entry : counterPriceLevels.entrySet()) {
            TickPrice tickPrice = entry.getKey();
            MatchingPriceLevel matchingPriceLevel = entry.getValue();

            log.debug("[MarketOrder] Checking price level {} with restingOrders={}",
                    tickPrice.getValue(), matchingPriceLevel.getOrders().size());

            while (marketOrder.isUnfilled() && !matchingPriceLevel.isEmpty()) {
                Order restingOrder = matchingPriceLevel.peekOrder();

                BigDecimal maxQtyByPrice = marketOrder.getRemainingPrice().getValue()
                        .divide(restingOrder.getOrderPrice().getValue(), 8, BigDecimal.ROUND_DOWN);
                Quantity execQty = Quantity.of(maxQtyByPrice.min(restingOrder.getRemainingQuantity().getValue()));

                // 1️⃣ 최소 거래 단위 체크
                if (execQty.isZero() || execQty.getValue().compareTo(new BigDecimal("0.000001")) < 0) {
                    log.debug("[MarketOrder] ExecQty too small, breaking loop: execQty={}", execQty.getValue());
                    break;
                }

                BigDecimal prevRemainingPrice = marketOrder.getRemainingPrice().getValue();
                marketOrder.applyTrade(restingOrder.getOrderPrice(), execQty);
                restingOrder.applyTrade(execQty);

                // 2️⃣ remainingPrice 변화 없으면 루프 탈출
                if (marketOrder.getRemainingPrice().getValue().compareTo(prevRemainingPrice) >= 0) {
                    log.debug("[MarketOrder] Remaining price not reduced, breaking loop");
                    break;
                }

                OrderPrice executionPrice = restingOrder.getOrderPrice();

                PredictedTradeCreatedEvent createdEvent = matchingDomainService.createPredictedTrade(
                        marketOrder.getMarketId(),
                        marketOrder.getUserId(),
                        marketOrder,
                        restingOrder,
                        executionPrice,
                        execQty,
                        TransactionType.TRADE_BUY
                );
                trades.add(createdEvent);

                log.info("[MarketOrder] Trade executed: takerId={}, makerId={}, execPrice={}, execQty={}, " +
                                "takerRemainingPrice={}, makerRemainingQty={}",
                        marketOrder.getId().getValue(),
                        restingOrder.getId().getValue(),
                        executionPrice.getValue(),
                        execQty.getValue(),
                        marketOrder.getRemainingPrice().getValue(),
                        restingOrder.getRemainingQuantity().getValue());

                if (restingOrder.isFilled()) {
                    log.debug("[MarketOrder] Resting order filled and removed: orderId={}, userId={}",
                            restingOrder.getId().getValue(),
                            restingOrder.getUserId().getValue());
                    matchingPriceLevel.popOrder();
                }

                if (marketOrder.getRemainingPrice().isZero()) break;
            }
            if (marketOrder.getRemainingPrice().isZero()) break;
        }

        log.info("[MarketOrder] Matching finished: takerId={}, totalTrades={}, finalRemainingPrice={}",
                marketOrder.getId().getValue(),
                trades.size(),
                marketOrder.getRemainingPrice().getValue());

        return new MatchedContext<>(trades, marketOrder);
    }


    private MatchedContext<MarketOrder> matchSellOrder(MatchingOrderBook matchingOrderBook,
                                                       MarketOrder marketOrder) {
        List<PredictedTradeCreatedEvent> trades = new ArrayList<>();

        log.info("[MarketOrder] New SELL order received: id={}, user={}, market={}",
                marketOrder.getId().getValue(),
                marketOrder.getUserId().getValue(),
                marketOrder.getMarketId().getValue());

        NavigableMap<TickPrice, MatchingPriceLevel> counterPriceLevels = matchingOrderBook.getBuyPriceLevels();

        for (Map.Entry<TickPrice, MatchingPriceLevel> entry : counterPriceLevels.entrySet()) {
            TickPrice tickPrice = entry.getKey();
            MatchingPriceLevel matchingPriceLevel = entry.getValue();

            log.debug("[MarketOrder] Checking price level {} with restingOrders={}",
                    tickPrice.getValue(), matchingPriceLevel.getOrders().size());

            while (marketOrder.isUnfilled() && !matchingPriceLevel.isEmpty()) {
                Order restingOrder = matchingPriceLevel.peekOrder();

                log.debug("[MarketOrder] Before trade: takerRemainingQty={}, restingRemainingQty={}",
                        marketOrder.getRemainingQuantity().getValue(),
                        restingOrder.getRemainingQuantity().getValue());

                Quantity execQty = marketOrder.applyTrade(restingOrder.getRemainingQuantity());
                restingOrder.applyTrade(execQty);
                OrderPrice executionPrice = new OrderPrice(tickPrice.getValue());

                PredictedTradeCreatedEvent createdEvent = matchingDomainService.createPredictedTrade(
                        marketOrder.getMarketId(),
                        marketOrder.getUserId(),
                        marketOrder,
                        restingOrder,
                        executionPrice,
                        execQty,
                        TransactionType.TRADE_SELL
                );
                trades.add(createdEvent);

                log.info("[MarketOrder] Trade executed: takerId={}, makerId={}, execPrice={}, execQty={}, " +
                                "takerRemainingQty={}, makerRemainingQty={}",
                        marketOrder.getId().getValue(),
                        restingOrder.getId().getValue(),
                        executionPrice.getValue(),
                        execQty.getValue(),
                        marketOrder.getRemainingQuantity().getValue(),
                        restingOrder.getRemainingQuantity().getValue());

                if (restingOrder.isFilled()) {
                    log.debug("[MarketOrder] Resting order filled and removed: orderId={}, userId={}",
                            restingOrder.getId().getValue(),
                            restingOrder.getUserId().getValue());
                    matchingPriceLevel.popOrder();
                }

                if (marketOrder.getRemainingQuantity().isZero()) break;
            }
            if (marketOrder.getRemainingQuantity().isZero()) break;
        }

        log.info("[MarketOrder] Matching finished: takerId={}, totalTrades={}, finalRemainingQty={}",
                marketOrder.getId().getValue(),
                trades.size(),
                marketOrder.getRemainingQuantity().getValue());

        return new MatchedContext<>(trades, marketOrder);
    }

}
