package shop.shportfolio.trading.domain.entity;

import lombok.Builder;
import lombok.Getter;
import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.trading.domain.exception.TradingDomainException;
import shop.shportfolio.trading.domain.valueobject.*;

import java.util.UUID;

// 지정가 주문
@Getter
public class LimitOrder extends Order {


    @Builder
    public LimitOrder(OrderId orderId, UserId userId, MarketId marketId, OrderSide orderSide,
                      Quantity quantity,
                      Quantity remainingQuantity,
                      OrderPrice orderPrice,
                      OrderType orderType,
                      CreatedAt createdAt,
                      OrderStatus orderStatus) {
        super(orderId, userId, marketId, orderSide, quantity, remainingQuantity,
                orderPrice, orderType, createdAt,orderStatus);
    }

    private LimitOrder(OrderId orderId, UserId userId, MarketId marketId, OrderSide orderSide,
                      Quantity quantity, OrderPrice orderPrice, OrderType orderType, CreatedAt createdAt) {
        super(orderId, userId, marketId, orderSide, quantity, orderPrice, orderType, createdAt);

    }


    public static LimitOrder createLimitOrder(UserId userId, MarketId marketId, OrderSide orderSide,
                                              Quantity quantity, OrderPrice orderPrice, OrderType orderType) {
        LimitOrder limitOrder = new LimitOrder(new OrderId(UUID.randomUUID().toString()),
                userId, marketId, orderSide, quantity, orderPrice, orderType, CreatedAt.now());
        limitOrder.isLimitOrder();
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
            return getOrderPrice().isGreaterThanOrEqualTo(targetPrice);
        } else {
            return getOrderPrice().isLessThanOrEqualTo(targetPrice);
        }
    }

}
