package shop.shportfolio.common.domain.valueobject;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class UpdatedAt extends ValueObject<LocalDateTime> {

    public UpdatedAt(LocalDateTime value) {
        super(value);
    }
    public LocalDateTime getValue() {
        return value;
    }

    public static UpdatedAt now() {
        return new UpdatedAt(LocalDateTime.now(ZoneOffset.UTC));
    }
}
