package shop.shportfolio.common.domain.valueobject;

import java.math.BigDecimal;
import java.util.Objects;

public class Price {
    private final BigDecimal value;

    public Price(BigDecimal value) {
        if (value == null || value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price must be non-negative");
        }
        this.value = value;
    }

    public BigDecimal getValue() {
        return value;
    }

    public Price add(Price other) {
        return new Price(this.value.add(other.value));
    }

    public Price subtract(Price other) {
        BigDecimal result = this.value.subtract(other.value);
        if (result.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Resulting price must be non-negative");
        }
        return new Price(result);
    }

    public Price multiply(BigDecimal factor) {
        return new Price(this.value.multiply(factor));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Price)) return false;
        Price price = (Price) o;
        return value.compareTo(price.value) == 0;
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