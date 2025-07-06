package shop.shportfolio.common.domain.valueobject;

import java.math.BigDecimal;

public class FeeAmount extends ValueObject<BigDecimal> {
    public FeeAmount(BigDecimal value) {
        super(value);
    }
}
