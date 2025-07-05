package shop.shportfolio.portfolio.domain.valueobject;

import shop.shportfolio.common.domain.valueobject.ValueObject;

import java.math.BigDecimal;

public class ValuationPrice extends ValueObject<BigDecimal> {

    public ValuationPrice(BigDecimal value) {
        super(value);
    }
}
