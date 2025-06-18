package shop.shportfolio.trading.domain.valueobject;

import shop.shportfolio.common.domain.valueobject.ValueObject;
import java.math.BigDecimal;
import java.util.Objects;

// 예약 주문 실행 조건 (ex: BTC 가격이 70,000,000 미만일 때 등)
public class TriggerCondition extends ValueObject<BigDecimal> {

    public TriggerCondition(BigDecimal value) {
        super(value);
        if (value == null) {
            throw new IllegalArgumentException("TriggerCondition value cannot be null");
        }
    }

    public boolean isTriggered(BigDecimal currentPrice) {
        if (currentPrice == null) {
            throw new IllegalArgumentException("Current price cannot be null");
        }
        // 트리거 조건: 현재 가격이 value 이하일 때 트리거 발생
        return currentPrice.compareTo(this.getValue()) <= 0;
    }
}
