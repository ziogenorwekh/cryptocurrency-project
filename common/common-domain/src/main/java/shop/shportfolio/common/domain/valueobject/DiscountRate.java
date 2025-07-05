package shop.shportfolio.common.domain.valueobject;

import java.math.BigDecimal;
import java.util.Objects;

public class DiscountRate extends ValueObject<BigDecimal> {

    public DiscountRate(BigDecimal rate) {
        super(rate);
        if (rate.compareTo(BigDecimal.ZERO) < 0 || rate.compareTo(BigDecimal.ONE) > 0) {
            throw new IllegalArgumentException("Discount rate must be between 0.00 and 1.00");
        }
    }
    public BigDecimal applyTo(BigDecimal amount) {
        return amount.multiply(BigDecimal.ONE.subtract(this.value));
    }
}