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
        NavigableMap<TickPrice, PriceLevel> counterPriceLevels = marketOrder.isBuyOrder()
                ? orderBook.getSellPriceLevels()
                : orderBook.getBuyPriceLevels();

        for (Map.Entry<TickPrice, PriceLevel> entry : counterPriceLevels.entrySet()) {

            PriceLevel priceLevel = entry.getValue();
            while (marketOrder.isUnfilled() && !priceLevel.isEmpty()) {
                Order restingOrder = priceLevel.peekOrder();
                log.info("[MarketOrder] Peeked resting order: id={}, price={}, remainingQty={}",
                        restingOrder.getId().getValue(),
                        restingOrder.getOrderPrice().getValue(),
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

                log.info("[MarketOrder] Trade recorded: {}", createdEvent.getDomainType());
                trades.add(createdEvent);
                log.info("[MarketOrder] Executed trade: qty={}, price={}", execQty.getValue(), executionPrice.getValue());

                if (restingOrder.isFilled()) {
                    log.info("[MarketOrder] Resting order {} filled. Removing from PriceLevel", restingOrder.getId().getValue());
                    priceLevel.popOrder();
                    log.info("[MarketOrder] Remaining orders in PriceLevel: {}", priceLevel.getOrders().size());
                }

                if (marketOrder.getRemainingPrice().isZero()) {
                    log.info("[MarketOrder] Market order {} fully matched", marketOrder.getId().getValue());
                    break;
                }
            }
        }
        return new MatchedContext<>(trades, marketOrder);
    }
}
