package shop.shportfolio.common.domain.valueobject;

import java.math.BigDecimal;

public class FeeRate extends ValueObject<BigDecimal> {
    public FeeRate(BigDecimal value) {
        super(value);
    }
}
