package shop.shportfolio.trading.domain.valueobject;

import shop.shportfolio.common.domain.valueobject.OrderPrice;
import shop.shportfolio.common.domain.valueobject.ValueObject;

// 예약 주문 실행 조건 (ex: BTC 가격이 70,000,000 미만일 때 등)
public class TriggerCondition extends ValueObject<TriggerType> {

    private final OrderPrice targetPrice;

    public TriggerCondition(TriggerType triggerType, OrderPrice targetPrice) {
        super(triggerType);
        this.targetPrice = targetPrice;
    }

    public static TriggerCondition of(TriggerType triggerType, OrderPrice targetPrice) {
        return new TriggerCondition(triggerType, targetPrice);
    }

    public boolean isSatisfiedBy(OrderPrice marketPrice) {
        return switch (this.value) {
            case ABOVE -> marketPrice.isGreaterThanOrEqualTo(targetPrice);
            case BELOW -> marketPrice.isLessThanOrEqualTo(targetPrice);
        };
    }

    public OrderPrice getTargetPrice() {
        return targetPrice;
    }

    // equals, hashCode, toString 등은 생략
}
