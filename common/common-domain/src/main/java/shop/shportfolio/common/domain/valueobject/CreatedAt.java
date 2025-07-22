package shop.shportfolio.common.domain.valueobject;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class CreatedAt extends ValueObject<LocalDateTime> {

    public CreatedAt(LocalDateTime value) {
        super(value);
    }
    public LocalDateTime getValue() {
        return value;
    }


    public static CreatedAt now() {
        return new CreatedAt(LocalDateTime.now(ZoneOffset.UTC));
    }
}
