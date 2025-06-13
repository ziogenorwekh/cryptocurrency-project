package shop.shportfolio.user.domain.valueobject;

import shop.shportfolio.common.domain.valueobject.ValueObject;

import java.math.BigDecimal;

public class Amount extends ValueObject<BigDecimal> {


    public Amount(BigDecimal value) {
        super(value);
    }
}
