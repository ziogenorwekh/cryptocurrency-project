package shop.shportfolio.trading.domain.entity.orderbook;

import lombok.Builder;
import lombok.Getter;
import shop.shportfolio.common.domain.entity.AggregateRoot;
import shop.shportfolio.common.domain.valueobject.MarketId;
import shop.shportfolio.common.domain.valueobject.Quantity;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.Order;
import shop.shportfolio.trading.domain.entity.trade.Trade;
import shop.shportfolio.trading.domain.valueobject.TickPrice;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;


// 설계변경 기존: 자체 거래 시스템 개발 -> 외부 api 의존. 향후 다시 자체 거래 시스템 개발
// 호가창(개인들의 주문 요청 등 보관하는 역할)
// 값 가져올때 Order가 OrderStatus가 Open인지 체크하고 값 호출
@Getter
public class OrderBook extends AggregateRoot<MarketId> {


    private final NavigableMap<TickPrice, PriceLevel> buyPriceLevels;
    private final NavigableMap<TickPrice, PriceLevel> sellPriceLevels;

    public OrderBook(MarketId marketId) {
        setId(marketId);
        buyPriceLevels = new ConcurrentSkipListMap<>(Comparator.reverseOrder());
        sellPriceLevels = new ConcurrentSkipListMap<>();
    }

    @Builder
    public OrderBook(MarketId marketId, NavigableMap<TickPrice, PriceLevel> buyPriceLevels,
                     NavigableMap<TickPrice, PriceLevel> sellPriceLevels) {
        setId(marketId);
        this.buyPriceLevels = buyPriceLevels;
        this.sellPriceLevels = sellPriceLevels;
    }

    public void addOrder(LimitOrder order) {
        if (order.isBuyOrder()) {
            addBuyOrder(order);
        } else if (order.isSellOrder()) {
            addSellOrder(order);
        } else {
            throw new IllegalArgumentException("Order must be BUY or SELL");
        }
    }

    public Long getBidsSizeByTickPrice(TickPrice tickPrice) {
        return Optional.ofNullable(buyPriceLevels.get(tickPrice))
                .map(p -> (long) p.getOrders().size())
                .orElse(0L);
    }
    public Long getAsksSizeByTickPrice(TickPrice tickPrice) {
        return Optional.ofNullable(sellPriceLevels.get(tickPrice))
                .map(p -> (long) p.getOrders().size())
                .orElse(0L);
    }

    public void applyExecutedTrade(Trade trade) {
        NavigableMap<TickPrice, PriceLevel> targetLevels =
                trade.isBuyTrade() ? sellPriceLevels : buyPriceLevels;

        TickPrice tickPrice = TickPrice.of(trade.getOrderPrice().getValue());
        PriceLevel priceLevel = targetLevels.get(tickPrice);
        System.out.println("TickPrices in targetLevels: " + targetLevels.keySet());

        if (priceLevel == null) {
            throw new IllegalArgumentException("PriceLevel missing for tick: " + tickPrice.getValue());
        }

        Quantity remainingTradeQty = trade.getQuantity();
        Iterator<Order> iterator = priceLevel.getOrders().iterator();
        while (iterator.hasNext() && remainingTradeQty.isPositive()) {
            Order order = iterator.next();

            if (order.getCreatedAt().getValue().isAfter(trade.getCreatedAt().getValue())) {
                continue;
            }

            Quantity orderQty = order.getRemainingQuantity();

            if (orderQty.compareTo(remainingTradeQty) <= 0) {
                order.applyTrade(orderQty);

                remainingTradeQty = remainingTradeQty.subtract(orderQty);
                iterator.remove();
            } else {
                order.applyTrade(remainingTradeQty);
                remainingTradeQty = Quantity.ZERO;
            }
        }
        if (priceLevel.isEmpty()) {
            targetLevels.remove(tickPrice);
        }

        if (remainingTradeQty.isPositive()) {
            throw new IllegalStateException("Trade quantity remaining after subtraction");
        }
    }

    private void addBuyOrder(LimitOrder order) {
        TickPrice tickPrice = TickPrice.of(order.getOrderPrice().getValue());
        buyPriceLevels.computeIfAbsent(tickPrice, k -> new PriceLevel(tickPrice)).addOrder(order);
    }

    private void addSellOrder(LimitOrder order) {
        TickPrice tickPrice = TickPrice.of(order.getOrderPrice().getValue());
        sellPriceLevels.computeIfAbsent(tickPrice, k -> new PriceLevel(tickPrice)).addOrder(order);
    }
}

