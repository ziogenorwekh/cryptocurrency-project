package shop.shportfolio.trading.domain.entity;

import lombok.Builder;
import lombok.Getter;
import shop.shportfolio.common.domain.valueobject.MarketId;
import shop.shportfolio.common.domain.valueobject.UserId;
import shop.shportfolio.trading.domain.exception.TradingDomainException;
import shop.shportfolio.trading.domain.valueobject.OrderPrice;
import shop.shportfolio.trading.domain.valueobject.OrderSide;
import shop.shportfolio.trading.domain.valueobject.OrderType;
import shop.shportfolio.trading.domain.valueobject.Quantity;


@Getter
// 시장가 주문
public class MarketOrder extends Order {

    private MarketOrder(UserId userId, MarketId marketId, OrderSide orderSide,
                        Quantity quantity, OrderType orderType) {
        super(userId, marketId, orderSide, quantity, orderType);
    }

    public static MarketOrder createMarketOrder(UserId userId, MarketId marketId, OrderSide orderSide,
                                                Quantity quantity, OrderType orderType) {
        MarketOrder marketOrder = new MarketOrder(userId, marketId, orderSide, quantity, orderType);
        marketOrder.validatePlaceable();
        marketOrder.isMarketOrder();
        return marketOrder;
    }

    @Override
    public void validatePlaceable() {
        if (getQuantity() == null || getQuantity().isZero()) {
            throw new TradingDomainException("Order has no quantity.");
        }
        if (this.getOrderStatus().isFinal()) {
            throw new TradingDomainException("Order is already filled or cancelled.");
        }
        if (this.getRemainingQuantity() == null || this.getRemainingQuantity().isZero()) {
            throw new TradingDomainException("Order has no remaining quantity.");
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

}
