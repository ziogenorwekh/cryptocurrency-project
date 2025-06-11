package shop.shportfolio.common.domain.valueobject;

import java.math.BigDecimal;
import java.util.Objects;

public class DiscountRate {
    private final BigDecimal rate; // 0.00 ~ 1.00

    public DiscountRate(BigDecimal rate) {
        if (rate == null || rate.compareTo(BigDecimal.ZERO) < 0 || rate.compareTo(BigDecimal.ONE) > 0) {
            throw new IllegalArgumentException("Discount rate must be between 0.00 and 1.00");
        }
        this.rate = rate;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public BigDecimal applyTo(BigDecimal amount) {
        return amount.multiply(BigDecimal.ONE.subtract(rate));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DiscountRate)) return false;
        DiscountRate that = (DiscountRate) o;
        return rate.compareTo(that.rate) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(rate.stripTrailingZeros());
    }

    @Override
    public String toString() {
        return rate.multiply(BigDecimal.valueOf(100)).stripTrailingZeros().toPlainString() + "%";
    }
}