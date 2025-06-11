package shop.shportfolio.common.domain.valueobject;

import java.math.BigDecimal;
import java.util.Objects;

public class Quantity {
    private final BigDecimal value;

    public Quantity(BigDecimal value) {
        if (value == null || value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        this.value = value;
    }

    public BigDecimal getValue() {
        return value;
    }

    public Quantity add(Quantity other) {
        return new Quantity(this.value.add(other.value));
    }

    public Quantity subtract(Quantity other) {
        BigDecimal result = this.value.subtract(other.value);
        if (result.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Resulting quantity must be positive");
        }
        return new Quantity(result);
    }

    public Quantity multiply(BigDecimal factor) {
        return new Quantity(this.value.multiply(factor));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Quantity)) return false;
        Quantity quantity = (Quantity) o;
        return value.compareTo(quantity.value) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value.stripTrailingZeros());
    }

    @Override
    public String toString() {
        return value.toPlainString();
    }
}