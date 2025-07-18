package shop.shportfolio.trading.domain.valueobject;

import shop.shportfolio.common.domain.valueobject.ValueObject;

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
}
