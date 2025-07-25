package shop.shportfolio.common.domain.valueobject;

import java.time.LocalDate;
import java.time.ZoneOffset;

public class IssuedAt extends ValueObject<LocalDate> {
    public IssuedAt(LocalDate value) {
        super(value);
    }

    public static IssuedAt now() {
        return new IssuedAt(LocalDate.now(ZoneOffset.UTC));
    }
}
