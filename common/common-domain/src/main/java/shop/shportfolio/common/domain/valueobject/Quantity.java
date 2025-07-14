package shop.shportfolio.common.domain.valueobject;

import java.math.BigDecimal;

public class Quantity extends ValueObject<BigDecimal> implements Comparable<Quantity> {

    public static final Quantity ZERO = new Quantity(BigDecimal.ZERO);

    public Quantity(BigDecimal value) {
        super(value);
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
    }

    public Quantity min(Quantity other) {
        if (other == null) {
            throw new IllegalArgumentException("Other quantity must not be null");
        }
        return this.value.compareTo(other.value) <= 0 ? this : other;
    }

    public BigDecimal getValue() {
        return value;
    }

    public Quantity add(Quantity other) {
        return new Quantity(this.value.add(other.value));
    }

    public Quantity subtract(Quantity other) {
        BigDecimal result = this.value.subtract(other.value);
        if (result.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Resulting quantity must be positive");
        }
        return new Quantity(result);
    }

    public Boolean isZero() {
        return value.compareTo(BigDecimal.ZERO) == 0;
    }

    public Boolean isNegative() {
        return value.compareTo(BigDecimal.ZERO) < 0;
    }

    public Boolean isPositive() {
        return value.compareTo(BigDecimal.ZERO) > 0;
    }


    public Quantity multiply(BigDecimal factor) {
        return new Quantity(this.value.multiply(factor));
    }

    @Override
    public int compareTo(Quantity other) {
        return this.value.compareTo(other.value);
    }
}