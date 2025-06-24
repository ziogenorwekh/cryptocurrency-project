package shop.shportfolio.trading.domain.entity;


import lombok.Getter;
import shop.shportfolio.common.domain.entity.BaseEntity;
import shop.shportfolio.common.domain.valueobject.CreatedAt;
import shop.shportfolio.common.domain.valueobject.OrderId;
import shop.shportfolio.common.domain.valueobject.UserId;
import shop.shportfolio.trading.domain.exception.TradingDomainException;
import shop.shportfolio.trading.domain.valueobject.OrderPrice;
import shop.shportfolio.trading.domain.valueobject.OrderType;
import shop.shportfolio.trading.domain.valueobject.Quantity;
import shop.shportfolio.trading.domain.valueobject.TradeId;

// 주문이 성공하면 기록되는 거래내역 엔티티
@Getter
public class Trade extends BaseEntity<TradeId> {

    private UserId userId;
    private OrderId buyOrderId;
    private OrderId sellOrderId;
    private OrderPrice orderPrice;
    private Quantity quantity;
    private CreatedAt createdAt;

    public Trade(TradeId tradeId, UserId userId, OrderId buyOrderId, OrderId sellOrderId,
                 OrderPrice orderPrice, Quantity quantity, CreatedAt createdAt) {
        setId(tradeId);
        this.userId = userId;
        this.buyOrderId = buyOrderId;
        this.sellOrderId = sellOrderId;
        this.orderPrice = orderPrice;
        this.quantity = quantity;
        this.createdAt = createdAt;
    }

    public static Trade createMarketTrade(TradeId tradeId, UserId userId, OrderId buyOrderId,
                                          OrderPrice orderPrice, Quantity quantity, CreatedAt createdAt) {
        return new Trade(tradeId, userId, buyOrderId, OrderId.anonymous(), orderPrice, quantity, createdAt);
    }

    public static Trade createLimitTrade(TradeId tradeId, UserId userId, OrderId buyOrderId, OrderId sellOrderId,
                                         OrderPrice orderPrice, Quantity quantity, CreatedAt createdAt) {
        return new Trade(tradeId, userId, buyOrderId, sellOrderId, orderPrice, quantity, createdAt);
    }

    public static Trade createReservationTrade(TradeId tradeId, UserId userId, OrderId buyOrderId, OrderId sellOrderId,
                                         OrderPrice orderPrice, Quantity quantity, CreatedAt createdAt) {
        return new Trade(tradeId, userId, buyOrderId, sellOrderId, orderPrice, quantity, createdAt);
    }
}
