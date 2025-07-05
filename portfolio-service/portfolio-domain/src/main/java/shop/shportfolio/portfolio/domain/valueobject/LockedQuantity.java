package shop.shportfolio.portfolio.domain.valueobject;

import shop.shportfolio.common.domain.valueobject.ValueObject;

import java.math.BigDecimal;

public class LockedQuantity extends ValueObject<BigDecimal> {

    public LockedQuantity(BigDecimal value) {
        super(value);
    }
}
