package shop.shportfolio.common.domain.valueobject;

import java.time.LocalDateTime;

public class CancelledAt extends ValueObject<LocalDateTime> {
    public CancelledAt(LocalDateTime value) {
        super(value);
    }
}
