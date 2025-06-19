package shop.shportfolio.trading.domain.entity;

import shop.shportfolio.common.domain.valueobject.MarketId;
import shop.shportfolio.common.domain.valueobject.UserId;
import shop.shportfolio.trading.domain.valueobject.*;

// 예약 매수
public class ReservationOrder extends Order {

    private TriggerCondition triggerCondition;
    private ScheduledTime scheduledTime;
    private ExpireAt expireAt;
    private IsRepeatable isRepeatable;

    public ReservationOrder(UserId userId, MarketId marketId, OrderSide orderSide,
                            Quantity quantity, OrderPrice orderPrice, OrderType orderType) {
        super(userId, marketId, orderSide, quantity, orderPrice, orderType);
    }
}
