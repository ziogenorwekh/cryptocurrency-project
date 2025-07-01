package shop.shportfoilo.coupon.domain.valueobject;

import shop.shportfolio.common.domain.valueobject.ValueObject;

import java.time.LocalDate;

public class IssuedAt extends ValueObject<LocalDate> {
    public IssuedAt(LocalDate value) {
        super(value);
    }
}
