package shop.shportfolio.common.domain.valueobject;

import java.math.BigDecimal;

public class FeeAmount extends ValueObject<BigDecimal> {
    public FeeAmount(BigDecimal value) {
        super(value);
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("FeeAmount cannot be negative");
        }
    }

    public static FeeAmount zero() {
        return new FeeAmount(BigDecimal.ZERO);
    }
}
