package shop.shportfolio.trading.domain.valueobject;

import shop.shportfolio.common.domain.valueobject.ValueObject;
import java.math.BigDecimal;
import java.util.Objects;

public class LimitPrice extends ValueObject<BigDecimal> {

    public LimitPrice(BigDecimal value) {
        super(value);
        if (value == null) {
            throw new IllegalArgumentException("LimitPrice cannot be null");
        }
        if (value.signum() <= 0) {
            throw new IllegalArgumentException("LimitPrice must be positive");
        }
    }

    // 추가로 필요하면 소수점 자리수 제한도 가능
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LimitPrice)) return false;
        LimitPrice that = (LimitPrice) o;
        // BigDecimal 비교 시 compareTo 사용 (값 비교)
        return this.getValue().compareTo(that.getValue()) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getValue());
    }
}
