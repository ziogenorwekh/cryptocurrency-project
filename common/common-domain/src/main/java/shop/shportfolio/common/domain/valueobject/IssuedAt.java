package shop.shportfolio.common.domain.valueobject;

import java.time.LocalDate;

public class IssuedAt extends ValueObject<LocalDate> {
    public IssuedAt(LocalDate value) {
        super(value);
    }

    public static IssuedAt now() {
        return new IssuedAt(LocalDate.now());
    }
}
