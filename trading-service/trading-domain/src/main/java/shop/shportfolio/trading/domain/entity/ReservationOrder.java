package shop.shportfolio.trading.domain.entity;

import shop.shportfolio.trading.domain.valueobject.ExpireAt;
import shop.shportfolio.trading.domain.valueobject.IsRepeatable;
import shop.shportfolio.trading.domain.valueobject.ScheduledTime;
import shop.shportfolio.trading.domain.valueobject.TriggerCondition;

public class ReservationOrder extends Order {

    private TriggerCondition triggerCondition;
    private ScheduledTime scheduledTime;
    private ExpireAt expireAt;
    private IsRepeatable isRepeatable;
}
