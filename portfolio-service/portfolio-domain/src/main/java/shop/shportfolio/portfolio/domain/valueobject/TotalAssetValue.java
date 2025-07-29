package shop.shportfolio.portfolio.domain.valueobject;

import shop.shportfolio.common.domain.valueobject.ValueObject;

import java.math.BigDecimal;

public class TotalAssetValue extends ValueObject<BigDecimal> {
    public TotalAssetValue(BigDecimal value) {
        super(value);
    }

    public static TotalAssetValue of(BigDecimal value) {
        return new TotalAssetValue(value);
    }

    public TotalAssetValue addAmount(BigDecimal amount) {
        return new TotalAssetValue(this.value.add(amount));
    }
    public TotalAssetValue subtractAmount(BigDecimal amount) {
        return new TotalAssetValue(this.value.subtract(amount));
    }
}
