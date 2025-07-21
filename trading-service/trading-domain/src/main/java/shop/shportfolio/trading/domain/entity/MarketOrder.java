package shop.shportfolio.trading.domain.entity;

import lombok.Getter;
import shop.shportfolio.common.domain.valueobject.MarketId;
import shop.shportfolio.common.domain.valueobject.UserId;
import shop.shportfolio.trading.domain.exception.TradingDomainException;
import shop.shportfolio.common.domain.valueobject.OrderPrice;
import shop.shportfolio.trading.domain.valueobject.OrderSide;
import shop.shportfolio.trading.domain.valueobject.OrderStatus;
import shop.shportfolio.trading.domain.valueobject.OrderType;
import shop.shportfolio.common.domain.valueobject.Quantity;

import java.math.BigDecimal;
import java.math.RoundingMode;


@Getter
// 시장가 주문
public class MarketOrder extends Order {

    private OrderPrice remainingPrice;

    private MarketOrder(UserId userId, MarketId marketId, OrderSide orderSide,
                        OrderPrice orderPrice, OrderType orderType) {
        super(userId, marketId, orderSide, null, orderPrice, orderType);
        this.remainingPrice = orderPrice;
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

    public Quantity applyMarketOrderTrade(OrderPrice execPrice, Quantity execQty) {
        checkIfModifiable();

        if (execQty == null || execQty.isZero() || execQty.isNegative()) {
            return Quantity.ZERO;
        }

        BigDecimal amount = execQty.getValue().multiply(execPrice.getValue()).setScale(4, RoundingMode.DOWN);
        BigDecimal newRemainingPrice = this.remainingPrice.getValue().subtract(amount)
                .setScale(4, RoundingMode.DOWN);
        if (newRemainingPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new TradingDomainException("Executed amount exceeds remaining price.");
        }

        this.remainingPrice = OrderPrice.of(newRemainingPrice);

        if (this.remainingPrice.isZero()) {
            this.orderStatus = OrderStatus.FILLED;
        } else {
            this.orderStatus = OrderStatus.PARTIALLY_FILLED;
        }

        return execQty;
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
