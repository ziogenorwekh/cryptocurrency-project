package shop.shportfolio.portfolio.domain.valueobject;

import shop.shportfolio.common.domain.valueobject.ValueObject;

import java.math.BigDecimal;

public class AvailableQuantity extends ValueObject<BigDecimal> {

    public AvailableQuantity(BigDecimal value) {
        super(value);
    }
}
