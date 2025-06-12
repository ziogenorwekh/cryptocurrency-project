package shop.shportfolio.common.domain.valueobject;

import java.time.LocalDateTime;

public class CreatedAt extends ValueObject<LocalDateTime> {

    public CreatedAt(LocalDateTime value) {
        super(value);
    }
    public LocalDateTime getValue() {
        return value;
    }

}
