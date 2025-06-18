package shop.shportfolio.trading.domain.entity;

import shop.shportfolio.common.domain.entity.AggregateRoot;
import shop.shportfolio.common.domain.valueobject.MarketId;
import shop.shportfolio.common.domain.valueobject.OrderBookId;
import shop.shportfolio.trading.domain.valueobject.OrderSide;

import java.util.ArrayList;
import java.util.List;

// 호가창(개인들의 주문 요청 등 보관하는 역할)
public class OrderBook extends AggregateRoot<OrderBookId> {

    private MarketId marketId;      // 어떤 마켓의 호가인지 (ex: KRW-BTC)
    private OrderSide side;     // 매수/매도 구분
    // 이 호가에 쌓여있는 주문들 (연관관계)
    private final List<Order> orders = new ArrayList<>();
}
