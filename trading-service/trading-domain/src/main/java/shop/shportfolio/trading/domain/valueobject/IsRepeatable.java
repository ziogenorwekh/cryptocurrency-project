package shop.shportfolio.trading.domain.valueobject;

import shop.shportfolio.common.domain.valueobject.ValueObject;

// 예약 조건이 여러 번 실행될 수 있는지 여부 (boolean)
public class IsRepeatable extends ValueObject<Boolean> {

    public IsRepeatable(Boolean value) {
        super(value);
        if (value == null) {
            throw new IllegalArgumentException("IsRepeatable cannot be null");
        }
    }

    public boolean isRepeatable() {
        return getValue();
    }
}