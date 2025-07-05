package shop.shportfolio.portfolio.domain.valueobject;

import shop.shportfolio.common.domain.valueobject.Quantity;
import shop.shportfolio.common.domain.valueobject.ValueObject;

import java.math.BigDecimal;

public class ValuationAmount extends ValueObject<BigDecimal> {

    public ValuationAmount(Quantity quantity, ValuationPrice valuationPrice) {
        super(quantity.getValue().multiply(valuationPrice.getValue()));
    }
}
