package shop.shportfolio.portfolio.domain.valueobject;

import shop.shportfolio.common.domain.valueobject.ValueObject;

import java.math.BigDecimal;

public class TotalAssetValue extends ValueObject<BigDecimal> {
    public TotalAssetValue(BigDecimal value) {
        super(value);
    }
}
