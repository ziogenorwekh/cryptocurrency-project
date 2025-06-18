package shop.shportfolio.trading.domain.entity;

import shop.shportfolio.common.domain.entity.AggregateRoot;
import shop.shportfolio.common.domain.valueobject.MarketId;
import shop.shportfolio.common.domain.valueobject.OrderBookId;
import shop.shportfolio.trading.domain.valueobject.OrderSide;
import shop.shportfolio.trading.domain.valueobject.PriceLevelPrice;

import java.util.*;

// 호가창(개인들의 주문 요청 등 보관하는 역할)
public class OrderBook extends AggregateRoot<OrderBookId> {

    private MarketId marketId;      // 어떤 마켓의 호가인지 (ex: KRW-BTC)
    // 매수 호가 (가격 내림차순)
    private final NavigableMap<PriceLevelPrice, PriceLevel> buyPriceLevels =
            new TreeMap<PriceLevelPrice, PriceLevel>(Comparator.reverseOrder());

    private final NavigableMap<PriceLevelPrice, PriceLevel> sellPriceLevels =
            new TreeMap<PriceLevelPrice, PriceLevel>();
}
