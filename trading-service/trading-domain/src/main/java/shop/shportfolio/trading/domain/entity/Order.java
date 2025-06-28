package shop.shportfolio.trading.domain.entity;

import lombok.Getter;
import shop.shportfolio.common.domain.entity.AggregateRoot;
import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.trading.domain.exception.TradingDomainException;
import shop.shportfolio.trading.domain.valueobject.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public abstract class Order extends AggregateRoot<OrderId> {

    private UserId userId;
//    이 주문이 속한 거래쌍(Market) 의 ID.
//    예: BTC/KRW, ETH/USDT 등.
    private MarketId marketId;
    //    주문 방향.
//    보통 2가지 값 중 하나: BUY, SELL.
    private OrderSide orderSide; // buy, sell

    private Quantity quantity;

    private OrderPrice orderPrice;
//    주문의 유형.
//    예: LIMIT, MARKET, RESERVATION 등.
    private OrderType orderType;

    private Quantity remainingQuantity;

    private CreatedAt createdAt;

//    오픈을 제외하면 변경 불가
    private OrderStatus orderStatus; // open, filled, cancelled


    protected Order(UserId userId, MarketId marketId, OrderSide orderSide, Quantity quantity,
                    OrderPrice orderPrice, OrderType orderType) {
        setId(new OrderId(UUID.randomUUID().toString()));
        this.userId = userId;
        this.marketId = marketId;
        this.orderSide = orderSide;
        this.orderPrice = orderPrice;
        this.quantity = quantity;
        this.orderType = orderType;
        this.remainingQuantity = quantity;
        this.orderStatus = OrderStatus.OPEN;
        this.createdAt = new CreatedAt(LocalDateTime.now());
    }

    // 가격 관련 메서드는 하위 클래스가 구현하도록 추상 메서드 선언
    public abstract void validatePlaceable();

    public abstract Boolean isPriceMatch(OrderPrice targetPrice);

    protected void validateCommonPlaceable() {
        if (this.getOrderStatus().isFinal()) {
            throw new TradingDomainException("Order is already filled or cancelled.");
        }
        if (this.getRemainingQuantity() == null || this.getRemainingQuantity().isZero()) {
            throw new TradingDomainException("Order has no remaining quantity.");
        }
        if (this.getQuantity() == null || this.getQuantity().isZero()) {
            throw new TradingDomainException("Order has no quantity.");
        }
    }

    public void cancel() {
        checkIfModifiable();
        if (this.orderStatus.isFinal()) {
            throw new TradingDomainException("Order already completed or canceled");
        }
        this.orderStatus = OrderStatus.CANCELED;
    }

    public Boolean isBuyOrder() {
        return this.orderSide.equals(OrderSide.BUY);
    }

    public Boolean isSellOrder() {
        return this.orderSide.equals(OrderSide.SELL);
    }

    public Boolean isOpen() {
        return this.orderStatus.equals(OrderStatus.OPEN);
    }

    public Boolean canMatchWith(Order other) {
        if (other == null) return false;
        if (!this.marketId.equals(other.marketId)) return false;
        if (!this.isOpen() || !other.isOpen()) return false;
        return this.orderSide.isOpposite(other.orderSide);
    }

    /**
     * 주문에 체결 수량 적용
     * @param executedQty 체결 요청 수량
     * @return 실제 체결된 수량 (executedQty 보다 작을 수 있음)
     */
    public Quantity applyTrade(Quantity executedQty) {
        checkIfModifiable();

        if (executedQty == null || executedQty.isZero() || executedQty.isNegative()) {
            return Quantity.ZERO;
        }

        Quantity qtyToApply = executedQty.min(this.remainingQuantity);
        this.remainingQuantity = this.remainingQuantity.subtract(qtyToApply);

        if (this.remainingQuantity.isZero()) {
            this.orderStatus = OrderStatus.FILLED;
        }

        return qtyToApply;
    }

    public Boolean isFilled() {
        return this.orderStatus.equals(OrderStatus.FILLED);
    }

    /**
     * 리밋 오더가 현재 가격에서 체결 가능한지 판단
     * @param limitOrder 리밋오더
     * @param counterPrice 상대 가격 (반대편 호가)
     * @return 체결 가능 여부
     */
    public boolean canMatchPrice(Order limitOrder, TickPrice counterPrice) {
        checkIfModifiable();
        if (limitOrder.isBuyOrder()) {
            // 매수 → 매도호가보다 같거나 높은 가격이면 체결
            return limitOrder.getOrderPrice().getValue().compareTo(counterPrice.getValue()) >= 0;
        } else if (limitOrder.isSellOrder()) {
            // 매도 → 매수호가보다 같거나 낮은 가격이면 체결
            return limitOrder.getOrderPrice().getValue().compareTo(counterPrice.getValue()) <= 0;
        }
        return false;
    }

    private void checkIfModifiable() {
        if (!this.orderStatus.equals(OrderStatus.OPEN)) {
            throw new TradingDomainException("Cannot modify order that is not OPEN");
        }
    }
}
