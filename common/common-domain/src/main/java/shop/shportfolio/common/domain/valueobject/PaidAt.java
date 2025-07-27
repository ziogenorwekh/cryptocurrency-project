package shop.shportfolio.common.domain.valueobject;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class PaidAt extends ValueObject<LocalDateTime> {
    public PaidAt(LocalDateTime value) {
        super(value);
    }

    public static PaidAt now() {
        return new PaidAt(LocalDateTime.now(ZoneOffset.UTC));
    }
}
