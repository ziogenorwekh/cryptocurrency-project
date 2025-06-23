package shop.shportfolio.trading.domain.entity;

import lombok.Getter;
import shop.shportfolio.common.domain.valueobject.MarketId;
import shop.shportfolio.common.domain.valueobject.UserId;
import shop.shportfolio.trading.domain.exception.TradingDomainException;
import shop.shportfolio.trading.domain.valueobject.*;

// 지정가 주문
@Getter
public class LimitOrder extends Order {

    private OrderPrice orderPrice;

    public LimitOrder(UserId userId, MarketId marketId, OrderSide orderSide,
                      Quantity quantity, OrderPrice orderPrice, OrderType orderType) {
        super(userId, marketId, orderSide, quantity, orderType);
        this.orderPrice = orderPrice;
    }

    public static LimitOrder createLimitOrder(UserId userId, MarketId marketId, OrderSide orderSide,
                                              Quantity quantity, OrderPrice orderPrice, OrderType orderType) {
        LimitOrder limitOrder = new LimitOrder(userId, marketId, orderSide, quantity, orderPrice, orderType);
        limitOrder.validatePlaceable();
        return limitOrder;
    }

    private void isLimitOrder() {
        if (!this.getOrderType().isLimit()) {
            throw new TradingDomainException("Order type is not market");
        }
    }

    @Override
    public void validatePlaceable() {
        validateCommonPlaceable();
    }

    @Override
    public Boolean isPriceMatch(OrderPrice targetPrice) {
        if (targetPrice == null) return false;
        if (this.isBuyOrder()) {
            return this.orderPrice.isGreaterThanOrEqualTo(targetPrice);
        } else {
            return this.orderPrice.isLessThanOrEqualTo(targetPrice);
        }
    }

}
