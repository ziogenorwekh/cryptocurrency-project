package shop.shportfolio.common.domain.valueobject;

import java.time.LocalDate;

public class IssuedAt extends ValueObject<LocalDate> {
    public IssuedAt(LocalDate value) {
        super(value);
    }
}
