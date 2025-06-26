package shop.shportfolio.trading.domain.valueobject;

import shop.shportfolio.common.domain.valueobject.ValueObject;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class TickPrice extends ValueObject<BigDecimal> implements Comparable<TickPrice> {

    public TickPrice(BigDecimal value) {
        super(value);
    }

    @Override
    public BigDecimal getValue() {
        return super.getValue();
    }


    public TickPrice add(TickPrice other) {
        return new TickPrice(this.value.add(other.value));
    }

    public TickPrice subtract(TickPrice other) {
        BigDecimal result = this.value.subtract(other.value);
        if (result.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Resulting price must be non-negative");
        }
        return new TickPrice(result);
    }

    public static TickPrice of(BigDecimal orderPrice, BigDecimal marketItemTick) {
        BigDecimal truncated = orderPrice.divide(marketItemTick, 0, RoundingMode.FLOOR).multiply(marketItemTick);
        return new TickPrice(truncated);
    }

    public TickPrice multiply(BigDecimal factor) {
        return new TickPrice(this.value.multiply(factor));
    }

    @Override
    public int compareTo(TickPrice other) {
        return this.value.compareTo(other.value);
    }
}
