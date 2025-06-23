package shop.shportfolio.trading.domain.entity;

import lombok.Getter;
import shop.shportfolio.common.domain.entity.AggregateRoot;
import shop.shportfolio.common.domain.valueobject.*;
import shop.shportfolio.trading.domain.exception.TradingDomainException;
import shop.shportfolio.trading.domain.valueobject.*;

import java.math.BigDecimal;
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
//    지정가 주문의 경우, 사용자가 설정한 주문 가격.
//    예: 1BTC당 50,000,000원에 사고 싶다면 이 필드에 저장됨.
//    시장가 주문의 경우, 이 값은 null 또는 무시될 수 있음.
    private OrderPrice orderPrice;
//    주문의 유형.
//    예: LIMIT, MARKET, RESERVATION 등.
    private OrderType orderType;

    private Quantity remainingQuantity;

    private CreatedAt createdAt;

//    오픈을 제외하면 변경 불가
    private OrderStatus orderStatus; // open, filled, cancelled


    protected Order(UserId userId, MarketId marketId, OrderSide orderSide, Quantity quantity, OrderType orderType) {
        setId(new OrderId(UUID.randomUUID()));
        this.userId = userId;
        this.marketId = marketId;
        this.orderSide = orderSide;
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

    public void applyTrade(Quantity executedQty) {
        checkIfModifiable();
        if (executedQty == null || executedQty.isZero() || executedQty.isNegative()) {
            throw new TradingDomainException("Executed quantity must be positive.");
        }
        if (executedQty.getValue().compareTo(this.remainingQuantity.getValue()) > 0) {
            throw new TradingDomainException("Executed quantity exceeds remaining quantity.");
        }
        this.remainingQuantity = this.remainingQuantity.subtract(executedQty);
        if (this.remainingQuantity.isZero()) {
            this.orderStatus = OrderStatus.FILLED;
        }
    }

    private void checkIfModifiable() {
        if (!this.orderStatus.equals(OrderStatus.OPEN)) {
            throw new TradingDomainException("Cannot modify order that is not OPEN");
        }
    }
}
