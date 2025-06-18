package shop.shportfolio.trading.domain.valueobject;

import shop.shportfolio.common.domain.valueobject.ValueObject;

import java.math.BigDecimal;

public class PriceLevelPrice extends ValueObject<BigDecimal> implements Comparable<PriceLevelPrice> {

    public PriceLevelPrice(BigDecimal value) {
        super(value);
    }

    @Override
    public BigDecimal getValue() {
        return super.getValue();
    }

    public PriceLevelPrice add(PriceLevelPrice other) {
        return new PriceLevelPrice(this.value.add(other.value));
    }

    public PriceLevelPrice subtract(PriceLevelPrice other) {
        BigDecimal result = this.value.subtract(other.value);
        if (result.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Resulting price must be non-negative");
        }
        return new PriceLevelPrice(result);
    }

    public PriceLevelPrice multiply(BigDecimal factor) {
        return new PriceLevelPrice(this.value.multiply(factor));
    }

    @Override
    public int compareTo(PriceLevelPrice other) {
        return this.value.compareTo(other.value);
    }
}
