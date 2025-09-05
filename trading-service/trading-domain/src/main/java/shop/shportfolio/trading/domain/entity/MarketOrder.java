package shop.shportfolio.trading.domain.entity;

import lombok.Builder;
import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.trading.domain.exception.TradingDomainException;
import shop.shportfolio.trading.domain.valueobject.OrderSide;
import shop.shportfolio.trading.domain.valueobject.OrderStatus;
import shop.shportfolio.trading.domain.valueobject.OrderType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;


// 시장가 주문
public class MarketOrder extends Order {

    private OrderPrice remainingPrice;

    @Builder
    public MarketOrder(OrderId orderId, UserId userId, MarketId marketId, OrderSide orderSide,
                       Quantity quantity, Quantity remainingQuantity, OrderPrice orderPrice,
                       OrderPrice remainingPrice,
                       OrderType orderType, CreatedAt createdAt, OrderStatus orderStatus) {
        super(orderId, userId, marketId, orderSide, quantity,
                remainingQuantity, orderPrice, orderType, createdAt, orderStatus);
        this.remainingPrice = remainingPrice;
    }

    public static MarketOrder createMarketOrder(UserId userId, MarketId marketId,
                                                OrderSide orderSide, Quantity quantity,
                                                OrderPrice orderPrice, OrderType orderType) {
        MarketOrder marketOrder;
        if (orderSide.equals(OrderSide.BUY)) {
            marketOrder = new MarketOrder(new OrderId(UUID.randomUUID().toString()),
                    userId, marketId, orderSide, null, null, orderPrice, orderPrice, orderType,
                    CreatedAt.now(), OrderStatus.OPEN);
        } else if (orderSide.equals(OrderSide.SELL)) {
            marketOrder = new MarketOrder(new OrderId(UUID.randomUUID().toString()),
                    userId, marketId, orderSide, quantity, quantity, null, null, orderType,
                    CreatedAt.now(), OrderStatus.OPEN);
        } else {
            throw new UnsupportedOperationException("Unsupported OrderSide " + orderSide);
        }
        marketOrder.validatePlaceable();
        marketOrder.isMarketOrder();
        return marketOrder;
    }

    @Override
    public void validatePlaceable() {
        if (this.isBuyOrder()) {
            if (remainingPrice == null || remainingPrice.isZeroOrLess()) {
                throw new TradingDomainException("Market BUY order must have a remaining price.");
            }
        } else { // SELL
            if (getQuantity() == null || getQuantity().isNegative()) {
                throw new TradingDomainException("Market SELL order must have a quantity.");
            }
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

    public Quantity applyTrade(OrderPrice execPrice, Quantity execQty) {
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
    public OrderPrice getOrderPrice() {
        if (!this.isBuyOrder()) {
            throw new UnsupportedOperationException("MarketOrder SELL does not support price");
        }
        return super.getOrderPrice();
    }

    public OrderPrice getRemainingPrice() {
        if (!this.isBuyOrder()) {
            throw new UnsupportedOperationException("MarketOrder SELL does not support price");
        }
        return remainingPrice;
    }

    @Override
    public Quantity getQuantity() {
        if (this.isBuyOrder()) {
            throw new UnsupportedOperationException("Market BUY does not have quantity");
        }
        return super.getQuantity();
    }

    @Override
    public Quantity getRemainingQuantity() {
        if (this.isBuyOrder()) {
            throw new UnsupportedOperationException("Market BUY does not have quantity");
        }
        return super.getRemainingQuantity();
    }
}
