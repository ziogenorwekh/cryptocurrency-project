package shop.shportfolio.matching.domain.entity;

import lombok.Builder;
import lombok.Getter;
import shop.shportfolio.common.domain.entity.AggregateRoot;
import shop.shportfolio.common.domain.valueobject.MarketId;
import shop.shportfolio.common.domain.valueobject.Quantity;
import shop.shportfolio.matching.domain.valuobject.TotalAskPrice;
import shop.shportfolio.matching.domain.valuobject.TotalBidPrice;
import shop.shportfolio.trading.domain.entity.LimitOrder;
import shop.shportfolio.trading.domain.entity.Order;
import shop.shportfolio.trading.domain.entity.trade.Trade;
import shop.shportfolio.trading.domain.valueobject.TickPrice;

import java.util.Comparator;
import java.util.Iterator;
import java.util.NavigableMap;
import java.util.Optional;
import java.util.concurrent.ConcurrentSkipListMap;


// 설계변경 기존: 자체 거래 시스템 개발 -> 외부 api 의존. 향후 다시 자체 거래 시스템 개발
// 호가창(개인들의 주문 요청 등 보관하는 역할)
// 값 가져올때 Order가 OrderStatus가 Open인지 체크하고 값 호출
@Getter
public class MatchingOrderBook extends AggregateRoot<MarketId> {


    private final TotalAskPrice totalAskPrice;
    private final TotalBidPrice totalBidPrice;
    private final NavigableMap<TickPrice, MatchingPriceLevel> buyPriceLevels;
    private final NavigableMap<TickPrice, MatchingPriceLevel> sellPriceLevels;

    public MatchingOrderBook(MarketId marketId, TotalAskPrice totalAskPrice,
                             TotalBidPrice totalBidPrice) {
        this.totalAskPrice = totalAskPrice;
        this.totalBidPrice = totalBidPrice;
        setId(marketId);
        buyPriceLevels = new ConcurrentSkipListMap<>(Comparator.reverseOrder());
        sellPriceLevels = new ConcurrentSkipListMap<>();
    }

    @Builder
    public MatchingOrderBook(MarketId marketId, TotalAskPrice totalAskPrice, TotalBidPrice totalBidPrice,
                             NavigableMap<TickPrice, MatchingPriceLevel> buyPriceLevels,
                             NavigableMap<TickPrice, MatchingPriceLevel> sellPriceLevels) {
        this.totalAskPrice = totalAskPrice;
        this.totalBidPrice = totalBidPrice;
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

    private void addBuyOrder(LimitOrder order) {
        TickPrice tickPrice = TickPrice.of(order.getOrderPrice().getValue());
        buyPriceLevels.computeIfAbsent(tickPrice, k -> new MatchingPriceLevel(tickPrice)).addOrder(order);
    }

    private void addSellOrder(LimitOrder order) {
        TickPrice tickPrice = TickPrice.of(order.getOrderPrice().getValue());
        sellPriceLevels.computeIfAbsent(tickPrice, k -> new MatchingPriceLevel(tickPrice)).addOrder(order);
    }
}

