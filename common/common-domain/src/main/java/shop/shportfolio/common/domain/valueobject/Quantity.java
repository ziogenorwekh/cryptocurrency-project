package shop.shportfolio.common.domain.valueobject;

import java.math.BigDecimal;
import java.util.Objects;

public class Quantity extends ValueObject<BigDecimal> {

    public Quantity(BigDecimal value) {
        super(value);
        if (value == null || value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
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
}