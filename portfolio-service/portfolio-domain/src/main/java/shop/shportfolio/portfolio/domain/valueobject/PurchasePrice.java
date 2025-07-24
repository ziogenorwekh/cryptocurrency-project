package shop.shportfolio.portfolio.domain.valueobject;

import shop.shportfolio.common.domain.valueobject.ValueObject;

import java.math.BigDecimal;

public class PurchasePrice extends ValueObject<BigDecimal> {
    public PurchasePrice(BigDecimal value) {
        super(value);
    }


    public PurchasePrice add(BigDecimal amount) {
        return new PurchasePrice(this.value.add(amount));
    }
    public PurchasePrice subtract(BigDecimal amount) {
        return new PurchasePrice(this.value.subtract(amount));
    }
    public PurchasePrice multiply(BigDecimal amount) {
        return new PurchasePrice(this.value.multiply(amount));
    }
    public PurchasePrice divide(BigDecimal amount) {
        return new PurchasePrice(this.value.divide(amount));
    }
    public static PurchasePrice of(BigDecimal value) {
        return new PurchasePrice(value);
    }
}
