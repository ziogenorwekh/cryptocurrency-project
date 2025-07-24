package shop.shportfolio.common.domain.valueobject;

import java.math.BigDecimal;

public class Money extends ValueObject<BigDecimal> {
    public Money(BigDecimal value) {
        super(value);
    }

    public static Money of(BigDecimal amount) {
        return new Money(amount);
    }

    public Money subtract(Money money) {
        return new Money(getValue().subtract(money.getValue()));
    }
    public Money add(Money money) {
        return new Money(getValue().add(money.getValue()));
    }
    public Money multiply(Money money) {
        return new Money(getValue().multiply(money.getValue()));
    }
    public Money divide(Money money) {
        return new Money(getValue().divide(money.getValue()));
    }

    public Boolean isZero() {
        return getValue().compareTo(BigDecimal.ZERO) == 0;
    }

    public Boolean isPositive() {
        return getValue().compareTo(BigDecimal.ONE) > 0;
    }
    public Boolean isNegative() {
        return getValue().compareTo(BigDecimal.ZERO) < 0;
    }
}
