package shop.shportfolio.trading.domain.entity;

import lombok.Getter;
import shop.shportfolio.common.domain.valueobject.MarketId;
import shop.shportfolio.common.domain.valueobject.UserId;
import shop.shportfolio.trading.domain.exception.TradingDomainException;
import shop.shportfolio.common.domain.valueobject.OrderPrice;
import shop.shportfolio.trading.domain.valueobject.OrderSide;
import shop.shportfolio.trading.domain.valueobject.OrderType;
import shop.shportfolio.common.domain.valueobject.Quantity;


@Getter
// 시장가 주문
public class MarketOrder extends Order {

    private MarketOrder(UserId userId, MarketId marketId, OrderSide orderSide,
                        OrderPrice orderPrice, OrderType orderType) {
        super(userId, marketId, orderSide, null, orderPrice , orderType);
    }

    public static MarketOrder createMarketOrder(UserId userId, MarketId marketId, OrderSide orderSide,
                                                OrderPrice orderPrice, OrderType orderType) {
        MarketOrder marketOrder = new MarketOrder(userId, marketId, orderSide, orderPrice, orderType);
        marketOrder.validatePlaceable();
        marketOrder.isMarketOrder();
        return marketOrder;
    }


    @Override
    public void validatePlaceable() {
        if (getOrderPrice() == null || getOrderPrice().isZeroOrLess()) {
            throw new TradingDomainException("MarketOrder has no price specified.");
        }
        if (this.getOrderStatus().isFinal()) {
            throw new TradingDomainException("Order is already filled or cancelled.");
        }
    }

    @Override
    public Boolean isPriceMatch(OrderPrice targetPrice) {
        // 시장가 주문은 가격 비교 의미 없음
        return true;
    }

    private void isMarketOrder() {
        if (!this.getOrderType().isMarket()) {
            throw new TradingDomainException("Order type is not market");
        }
    }

    @Override
    public Quantity getQuantity() {
        throw new UnsupportedOperationException("MarketOrder has no quantity.");
    }
}
