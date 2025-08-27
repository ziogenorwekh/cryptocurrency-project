package shop.shportfolio.common.domain.valueobject;

import java.math.BigDecimal;

public class OrderPrice extends ValueObject<BigDecimal> {

    public OrderPrice(BigDecimal value) {
        super(value);
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price must be non-negative");
        }
    }

    public BigDecimal getValue() {
        return value;
    }

    public OrderPrice add(OrderPrice other) {
        return new OrderPrice(this.value.add(other.value));
    }

    public OrderPrice subtract(OrderPrice other) {
        BigDecimal result = this.value.subtract(other.value);
        if (result.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Resulting price must be non-negative");
        }
        return new OrderPrice(result);
    }

    public OrderPrice min(OrderPrice other) {
        BigDecimal result = this.value.min(other.value);
        if (result.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Resulting price must be non-negative");
        }
        return new OrderPrice(result);
    }

    public boolean isPositive() {
        return value.compareTo(BigDecimal.ZERO) > 0;
    }

    public static OrderPrice zero() {
        return new OrderPrice(BigDecimal.ZERO);
    }

    public OrderPrice multiply(BigDecimal factor) {
        return new OrderPrice(this.value.multiply(factor));
    }

    public OrderPrice divide(BigDecimal factor) {
        return new OrderPrice(this.value.divide(factor));
    }

    public boolean isLessThanOrEqualTo(OrderPrice other) {
        return value.compareTo(other.value) <= 0;
    }

    public boolean isGreaterThanOrEqualTo(OrderPrice other) {
        return value.compareTo(other.value) >= 0;
    }

    public boolean isGreaterThan(OrderPrice other) {
        return value.compareTo(other.value) > 0;
    }
    public boolean isLessThan(OrderPrice other) {
        return value.compareTo(other.value) < 0;
    }


    public boolean isZeroOrLess() {
        return value.compareTo(BigDecimal.ZERO) <= 0;
    }

    public boolean isZero() {
        return value.compareTo(BigDecimal.ZERO) == 0;
    }

    public static OrderPrice of(BigDecimal value) {
        return new OrderPrice(value);
    }
}