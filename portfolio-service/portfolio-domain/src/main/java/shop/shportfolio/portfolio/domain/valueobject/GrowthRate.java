package shop.shportfolio.portfolio.domain.valueobject;

import shop.shportfolio.common.domain.valueobject.ValueObject;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class GrowthRate extends ValueObject<BigDecimal> {

    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");
    private static final int SCALE = 4;  // 소수점 자리수 조정

    public GrowthRate(BigDecimal value) {
        super(validate(value));
    }

    private static BigDecimal validate(BigDecimal value) {
        if (value == null) {
            throw new IllegalArgumentException("GrowthRate value cannot be null");
        }
        if (value.compareTo(new BigDecimal("-100")) < 0) {
            throw new IllegalArgumentException("GrowthRate cannot be less than -100%");
        }
        return value.setScale(SCALE, RoundingMode.HALF_UP);
    }

    public static GrowthRate of(BigDecimal value) {
        return new GrowthRate(value);
    }

    public GrowthRate add(GrowthRate other) {
        return new GrowthRate(this.value.add(other.value));
    }

    public GrowthRate subtract(GrowthRate other) {
        return new GrowthRate(this.value.subtract(other.value));
    }

    public GrowthRate multiply(BigDecimal multiplier) {
        return new GrowthRate(this.value.multiply(multiplier));
    }

    public boolean isPositive() {
        return value.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isNegative() {
        return value.compareTo(BigDecimal.ZERO) < 0;
    }

    @Override
    public String toString() {
        return value + "%";
    }
}
