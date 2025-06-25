package shop.shportfolio.trading.domain.entity;

import lombok.Getter;
import shop.shportfolio.common.domain.entity.AggregateRoot;
import shop.shportfolio.common.domain.valueobject.MarketId;
import shop.shportfolio.trading.domain.valueobject.MarketItemTick;
import shop.shportfolio.trading.domain.valueobject.TickPrice;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;


// 설계변경 기존: 자체 거래 시스템 개발 -> 외부 api 의존. 향후 다시 자체 거래 시스템 개발
// 호가창(개인들의 주문 요청 등 보관하는 역할)
// 값 가져올때 Order가 OrderStatus가 Open인지 체크하고 값 호출
@Getter
public class OrderBook extends AggregateRoot<MarketId> {


    private final MarketItemTick marketItemTick;
    // 어떤 마켓의 호가인지 (ex: KRW-BTC)
            // 매수 호가 (가격 내림차순)
    private final NavigableMap<TickPrice, PriceLevel> buyPriceLevels;

    private final NavigableMap<TickPrice, PriceLevel> sellPriceLevels;

    public OrderBook(MarketId marketId, MarketItemTick marketItemTick) {
        this.marketItemTick = marketItemTick;
        setId(marketId);
        buyPriceLevels = new TreeMap<>(Comparator.reverseOrder());
        sellPriceLevels = new TreeMap<>();
    }

    public void addOrder(Order order) {
        if (order.isBuyOrder()) {
            addBuyOrder(order);
        } else if (order.isSellOrder()) {
            addSellOrder(order);
        } else {
            throw new IllegalArgumentException("Order must be BUY or SELL");
        }

    }
    public Long getSizeByTickPrice(TickPrice tickPrice) {
        return (long) (buyPriceLevels.get(tickPrice).getBuyOrders().size() + sellPriceLevels.get(tickPrice).getSellOrders().size());
    }
    private void addBuyOrder(Order order) {
        TickPrice tickPrice = this.toTickPrice(order);
        buyPriceLevels.computeIfAbsent(tickPrice, k -> new PriceLevel(tickPrice)).addOrder(order);
    }

    private void addSellOrder(Order order) {
        TickPrice tickPrice = this.toTickPrice(order);
        sellPriceLevels.computeIfAbsent(tickPrice, k -> new PriceLevel(tickPrice)).addOrder(order);
    }

    private TickPrice toTickPrice(Order order) {
        BigDecimal raw = order.getOrderPrice().getValue();
        BigDecimal tick = marketItemTick.getValue();
        BigDecimal truncated = raw.divide(tick, 0, RoundingMode.FLOOR).multiply(tick);
        return new TickPrice(truncated);
    }
}

